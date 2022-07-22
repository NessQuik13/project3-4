from flask import Flask, request, jsonify, make_response
from flask_mysqldb import MySQL
import requests

app = Flask(__name__)

#database credentials
app.config['MYSQL_PORT'] = 3306
app.config['MYSQL_USER'] = 'timo'
app.config['MYSQL_PASSWORD'] = 'Welkom02!'
app.config['MYSQL_HOST'] = '145.24.222.137' 
app.config['MYSQL_DB'] = 'banklocal'

mysql = MySQL(app)
cur = mysql.connection.cursor()

#routing table
LANDW = 'https://145.24.222.219:8443/withdraw'
LANDB = 'https://145.24.222.219:8443/balance'


req_data = ""
toCtry = ""
toBank = ""
acctNo = ""
bankrekening = ""
pin = ""
pasNummer = ""
fromCtry = ""
fromBank = ""

@app.route("/balance", methods=['GET', 'POST'])
def balance():
    print("---------------------------------------------")
    print('A balance request has been made...')

    #try to parse body to json
    req_data = request.get_json()
    #print(req_data) #debug
    fromCtry = req_data['head']['fromCtry']
    fromBank = req_data['head']['fromBank']
    
    try:
        toCtry = req_data['head']['toCtry']
        toBank = req_data['head']['toBank']
        acctNo = req_data['body']['acctNo']
        bankrekening = acctNo[:-2] #rekeningnummer = acctNo - pasnummer
        pin = req_data['body']['pin']
        pasNummer = acctNo[-2:] #last 2 characters of iban = pasnummer  
    except:
        err = JsonError(fromCtry, fromBank, "Wrong body... (HTTP 400")
        response = make_response(jsonify(err), 400)
        response.headers["Content-Type"] = "application/json"
        return response

    print("Balance request from {} in {} to {} in {}...".format(fromBank, fromCtry, toBank, toCtry))                                           

    if checkDestination() == None:
        if checkAcctNo() == None:
            if checkPass() == None:
                if checkBlocked() == None:
                    if checkPinBalance() == None:
                        return 'dikke fout vriend', 418
                else: return checkBlocked()
            else: return checkPass()            
        else: return checkAcctNo()
    else: return checkDestination()

@app.route('/withdraw', methods=['POST', 'GET'])
def withdraw():
    print("---------------------------------------------")
    print('A withdraw request has been made')

    #try to parse body to json
    req_data = request.get_json()
    #print(req_data) #debug
    fromCtry = req_data['head']['fromCtry']
    fromBank = req_data['head']['fromBank']

    try:
        toCtry = req_data['head']['toCtry']
        toBank = req_data['head']['toBank']
        acctNo = req_data['body']['acctNo']
        bankrekening = acctNo[:-2] #rekeningnummer = acctNo - pasnummer
        pin = req_data['body']['pin']
        amount = req_data['body']['amount']
        pasNummer = acctNo[-2:] #last 2 characters of iban = pasnummer
    except:
        print("Wrong body...")
        err = JsonError(fromCtry, fromBank, "Wrong body... (HTTP 400")
        response = make_response(jsonify(err), 400)
        response.headers["Content-Type"] = "application/json"
        print("Replied with statuscode 400...")
        return response                       
    
    print("Withdraw request from {} in {} to {} in {}...".format(fromBank, fromCtry, toBank, toCtry))

    if checkDestination() == None:
        if checkAcctNo() == None:
            if checkPass() == None:
                if checkBlocked() == None:
                    if checkPinWithdraw() == None:
                        return 'dikke fout vriend', 418
                else: return checkBlocked()
            else: return checkPass()            
        else: return checkAcctNo()
    else: return checkDestination()

def JsonError(toCtry, toBank, error):
    json_file = {
            "head": {
                "fromCtry": "GR",
                "fromBank": "KRIV",
                "toCtry":   toCtry,
                "toBank":   toBank
            },
            "body": {
                "error": error
            }
        }
    return json_file

def checkDestination():
    #check if request is meant for Greece, else send to landserver
    if toCtry != 'GR':
        print('Bank not in Greece, request forwarded to landnode...')  
        r = requests.post(LANDB, json=req_data, verify=False)
        response = make_response(r.text,r.status_code)
        response.headers["Content-Type"] = "application/json"
        return response
    #check if request is meant for KRIV, else send to landserver
    elif toBank != 'KRIV': 
        print('Bank is not KR-IV, request forwarded to landnode...')   
        try:
            print("Trying to forward request to landnode...")
            r = requests.post(LANDB, json=req_data, verify=False)
            response = make_response(r.text,r.status_code)
            response.headers["Content-Type"] = "application/json"
            return response
        except:
            print("Bad response from landnode...")
            err = JsonError(fromCtry, fromBank, "Bad response from landnode, contact 1037120")
            response = make_response(jsonify(err), 400)
            response.headers["Content-Type"] = "application/json"
            return response

def checkAcctNo():
    #check if acctNo is valid
    sql = '''SELECT EXISTS(SELECT * FROM bankrekening WHERE rekening_nummer = %s)'''
    cur.execute(sql, (bankrekening,))

    results = cur.fetchone()
    if results[0] == 0:
        print('✘ Account number is not valid...')
        err = JsonError(fromCtry, fromBank, "Account number is not valid... (HTTP 404)")
        response = make_response(jsonify(err), 404)
        response.headers["Content-Type"] = "application/json"
        print("Replied with statuscode 404...")
        return response

    else:
        print("✓ Account number is valid")

def checkPass():
    #check if pass is valid
    sql = '''SELECT EXISTS (SELECT * FROM pas WHERE pas_nummer =%s AND rekening_nummer =%s)'''
    cur.execute(sql, (pasNummer,bankrekening))
    results = cur.fetchone()
    print(results)
    if results[0] == 0:
        print('Passnumber is not valid...')
        err = JsonError(fromCtry, fromBank, "Passnumber is not valid... (HTTP 404)")
        response = make_response(jsonify(err), 404)
        response.headers["Content-Type"] = "application/json"
        print("Replied with statuscode 404...")
        return response

    else:
        print("✓ Passnumber is valid")

def checkBlocked():
    #check if pass is blocked
    sql = '''SELECT blocked FROM pas WHERE pas_nummer = %s'''
    cur.execute(sql, (pasNummer,))

    results = cur.fetchone()
    if results[0] == 1: #if blocked
        print("✘ Pass is blocked...")
        err = JsonError(fromCtry, fromBank, "Pass is blocked... (HTTP 403)")
        response = make_response(jsonify(err), 403)
        response.headers["Content-Type"] = "application/json"
        print("Replied with statuscode 403...")
        return response
        
    else:
        print("✓ Pass is not blocked")

def checkPinBalance():
    #check if pin is correct
    sql = '''SELECT EXISTS(
        SELECT * FROM bankrekening 
        INNER JOIN pas on pas.rekening_nummer = bankrekening.rekening_nummer
        WHERE pas.rekening_nummer =%s
        AND pincode = sha2
            ((SELECT CONCAT(%s,salt) FROM pas WHERE pas_nummer = %s),224))
        '''
    cur.execute(sql, (bankrekening, pin,pasNummer))
    results = cur.fetchone()

    if results[0] == 0: #pin is incorrect
        print("✘ Pincode is incorrect")
        #increment pogingen
        sql = '''
            UPDATE pas
            SET pogingen = IF(blocked = 0, pogingen + 1, 3)
            WHERE rekening_nummer =%s;
            '''
        cur.execute(sql, (bankrekening,))
        mysql.connection.commit()

        sql = '''
            UPDATE pas 
            SET blocked = IF(pogingen = 3, 1, 0)
            WHERE rekening_nummer =%s;
            '''
        cur.execute(sql, (bankrekening,))
        mysql.connection.commit()

        #get attempts remaining for pas
        sql = '''SELECT pogingen FROM pas WHERE rekening_nummer =%s;'''
        cur.execute(sql, (bankrekening,))
        results = cur.fetchone()
        attemptsLeft = 3 - results[0] #max 3 attempts
        print("Attempts left: {}".format(attemptsLeft))

        json_file = {
            "head": {
                "fromCtry": "GR",
                "fromBank": "KRIV",
                "toCtry":   fromCtry,
                "toBank":   fromBank
            },
            "body": {
                "attemptsLeft": attemptsLeft
            }
        }
        response = make_response(jsonify(json_file), 401)
        response.headers["Content-Type"] = "application/json"
        print("Replied with statuscode 401...")
        return response

    elif results[0] == 1: #pin is correct
        print("✓ Pincode is correct")
        #reset pogingen
        sql = '''
            UPDATE pas
            SET pogingen = 0
            WHERE rekening_nummer =%s;'''
        cur.execute(sql, (bankrekening,))
        mysql.connection.commit()

        sql = '''SELECT saldo FROM bankrekening WHERE rekening_nummer = %s;'''
        cur.execute(sql, (bankrekening,))
        results = cur.fetchone()
        saldo = results[0]
    
        json_file = {
            "head": {
                "fromCtry": "GR",
                "fromBank": "KRIV",
                "toCtry":   fromCtry,
                "toBank":   fromBank
            },
            "body": {
                "balance":saldo
            }
        }
        response = make_response(jsonify(json_file), 200)
        response.headers["Content-Type"] = "application/json"
        print("Balance request successfull...")
        print("Replied with statuscode 200...")
        return response

def checkPinWithdraw():
    #pin is incorrect
    if results[0] == 0: 
        print("✘ Pincode is incorrect")
        #increment pogingen
        sql = '''
            UPDATE pas
            SET pogingen = IF(blocked = 0, pogingen + 1, 3)
            WHERE rekening_nummer =%s;
            '''
        cur.execute(sql, (bankrekening,))
        mysql.connection.commit()

        sql = '''
            UPDATE pas 
            SET blocked = IF(pogingen = 3, 1, 0)
            WHERE rekening_nummer =%s;
            '''
        cur.execute(sql, (bankrekening,))
        mysql.connection.commit()

        #add attempt to transactie
        sql = '''
            INSERT INTO  transactie (datum, geslaagd, amount, bank, land, rekening_nummer, pas_id)
            VALUES (CURRENT_TIMESTAMP, 0, %s, %s, %s, %s, %s)'''
        cur.execute(sql,(amount,fromBank,fromCtry,bankrekening,pasNummer))
        mysql.connection.commit()

        #get attempts remaining for pas
        sql = '''SELECT pogingen FROM pas WHERE rekening_nummer =%s;'''
        cur.execute(sql, (bankrekening,))
        results = cur.fetchone()
        attemptsLeft = 3 - results[0] #max 3 attempts
        print("Attempts left: {}".format(attemptsLeft))
        json_file = {
            "head": {
                "fromCtry": "GR",
                "fromBank": "KRIV",
                "toCtry":   fromCtry,
                "toBank":   fromBank
            },
            "body": {
                "attemptsLeft": attemptsLeft
            }
        }
        response = make_response(jsonify(json_file), 401)
        response.headers["Content-Type"] = "application/json"
        print("Replied with statuscode 401...")
        return response

    #pin is correct
    #reset pogingen
    sql = '''
        UPDATE pas
        SET pogingen = 0
        WHERE rekening_nummer =%s;'''
    cur.execute(sql, (bankrekening,))
    mysql.connection.commit()

    sql = '''
        SELECT saldo 
        FROM bankrekening
        INNER JOIN pas ON pas.rekening_nummer = bankrekening.rekening_nummer
        WHERE bankrekening.rekening_nummer =%s
        AND pas.pincode = sha2
                ((SELECT CONCAT(%s,salt) FROM pas WHERE pas_nummer = %s),224)'''
    cur.execute(sql, (bankrekening, pin, pasNummer))
    results = cur.fetchone()
    saldo = results[0]
    if saldo >= amount & amount >= 0:
        saldo = saldo - amount
        sql = '''
            UPDATE bankrekening
            SET saldo = %s
            WHERE rekening_nummer = %s'''
        cur.execute(sql, (saldo, bankrekening))
        mysql.connection.commit()
        print("✓ Transaction succesfull")

        sql = '''
            INSERT INTO  transactie (datum, geslaagd, amount, bank, land, rekening_nummer, pas_id)
            VALUES (CURRENT_TIMESTAMP, 1, %s, %s, %s, %s, %s)'''
        cur.execute(sql,(amount,fromBank,fromCtry,bankrekening,pasNummer))
        mysql.connection.commit()

        json_file = {
            "head": {
                "fromCtry": "GR",
                "fromBank": "KRIV",
                "toCtry":   fromCtry,
                "toBank":   fromBank
            },
            "body": {
                "balance":saldo
            }
        }
        response = make_response(jsonify(json_file), 200)
        response.headers["Content-Type"] = "application/json"
        print("Replied with statuscode 200...")
        return response
    else:
        print("✘ Not enough balance")
        err = JsonError(fromCtry, fromBank, "Not enough balance... (406)")
        response = make_response(jsonify(err), 406)
        response.headers["Content-Type"] = "application/json"
        print("Replied with statuscode 406...")
        return response

if __name__ ==  "__main__": 
    app.run(host='0.0.0.0', port=8443, debug=True)
