from flask import Flask, request, redirect, jsonify, make_response
import requests
import json

import ssl

context = ssl.SSLContext(ssl.PROTOCOL_TLSv1_2)
context.load_cert_chain('combined.pem', 'country_key.pem')

app = Flask(__name__)

#routing table (B = balance, W = withdraw)
TEST = 'http://127.0.0.1:8443/balance'
NOOBB = 'https://145.24.222.82:8443/api/balance'
NOOBW = 'https://145.24.222.82:8443/api/withdraw'
KRIVB = 'http://145.24.222.137:8443/balance'
KRIVW = 'http://145.24.222.137:8443/withdraw'
SIQCB = 'http://145.24.222.130:8443/balance'
SIQCW = 'http://145.24.222.130:8443/withdraw'
JLMSB = 'http://145.24.222.72:8555/balance'
JLMSW = 'http://145.24.222.72:8555/withdraw'

#balance requests
@app.route("/balance", methods=['POST','GET'])
def balance():
    print("------------------------------------------------------")
    print("Balance request made...")

    #try to parse json
    data = request.get_json()
    #print(data) #debug
    toCtry = data['head']['toCtry']
    toBank = data['head']['toBank']
    fromCtry = data['head']['fromCtry']
    fromBank = data['head']['fromBank']
    print("Incoming balance request from {} in {} to {} in {}...".format(fromBank, fromCtry, toBank, toCtry))

    try:
        acctNo = data['body']['acctNo']
        pin = data['body']['pin']
    except:
        return jsonErrorMessage(fromCtry, fromBank, "Wrong body... (HTTP 400)")
                   
    #request is not meant for Greece
    if toCtry != "GR":
        print("Request outside of Greece -> request forwarded to NOOB")
        print("Redirecting to NOOB/api/balance")
        returndata = returnJsonB(fromCtry, fromBank, toCtry, toBank, acctNo, pin)
        print(returndata)
        return redirectToNOOB(NOOBB, returndata)

    #request is meant for Greece
    else:
        if toBank == "KRIV": #request is meant for KR-IV
            print("Balance request forwarded to KR-IV")
            return returnResponse(KRIVB, data)
        elif toBank == "SIQC": #request is meant for SIQC
            print("Balance request forwarded to SIQC")
            return returnResponse(SIQCB, data)
        elif toBank == "JLMS": #request is meant for JLMS
            print("Balance request forwarded to ")
            return returnResponse(JLMSB, data)
        else:
            print("Bank {} not found in Greece...".format(toBank))
            response = make_response(json.dumps(jsonErrorMessage(fromCtry, fromBank, "Bank {} not found in Greece".format(toBank))),400)
            response.headers["Content-Type"] = "application/json"
            return response          

#withdraw requests
@app.route("/withdraw", methods=['POST','GET'])
def withdraw():
    print("------------------------------------------------------")
    print("Withdraw request made...")

    #try to parse json
    data = request.get_json()
    #print(data) #debug
    toCtry = data['head']['toCtry']
    toBank = data['head']['toBank']
    fromCtry = data['head']['fromCtry']
    fromBank = data['head']['fromBank']
    print("Incoming withdraw request from {} in {} to {} in {}...".format(fromBank, fromCtry, toBank, toCtry))

    try:
        acctNo = data['body']['acctNo']
        pin = data['body']['pin']
        amount = data['body']['amount']
    except:
        return jsonErrorMessage(fromCtry, fromBank, "Wrong body... (HTTP 400)")

    #request is not meant for Greece
    if toCtry != "GR":
        print("Request outside of Greece -> request forwarded to NOOB")
        print("Redirecting to NOOB/api/balance")
        returndata = returnJsonW(fromCtry, fromBank, toCtry, toBank, acctNo, pin, amount)
        print(returndata)
        return redirectToNOOB(NOOBW, returndata)

    #request is meant for Greece
    else:
        if toBank == "KRIV":
            print("Withdraw request forwarded to KR-IV")
            return returnResponse(KRIVW, data)
        elif toBank == "SIQC":
            print("Withdraw request forwarded to SIQC")  
            return returnResponse(SIQCW, data)
        elif toBank == "JLMS":
            print("Withdraw request forwarded to JLMS")
            return returnResponse(JLMSW, data)
        else:
            print("Bank {} not found in Greece...".format(toBank))
            response = make_response(json.dumps(jsonErrorMessage(fromCtry, fromBank, "Bank {} not found in Greece".format(toBank))))
            response.headers["Content-Type"] = "application/json"
            return response

def returnResponse(url, data):
    r = requests.post(url, json=data, verify=False)
    response = make_response(jsonify(r.text),r.status_code)
    response.headers["Content-Type"] = "application/json"
    return response

def redirectToNOOB(url, data):
    r = requests.post(url, 
        cert=('combined.pem','country_key.pem'), 
        verify='root.pem', 
        json=data)
    response = make_response(r.text,r.status_code)
    response.headers["Content-Type"] = "application/json"
    return response

def jsonErrorMessage(toCtry, toBank, error):
    json_file = {
                "head":{
                    "fromBank":"KRIV",
                    "fromCtry":"GR",
                    "toCtry":toCtry,
                    "toBank":toBank
                }, "body":{
                    "error":error
                }
            }
    return json_file

#start test functions
@app.route("/response")
def response():
    json = {
        "head":{
            "iets":"hoihoi",
            "nogiets":"doeidoei"
        }, "body": {
            "ietsinbody":"ook iets"
        }
    }
    return json

@app.route("/test")
def test():
    return jsonErrorMessage("fiets", "pizza", "errormessage")
#end test functions

def returnJsonB(fromCtry, fromBank, toCtry, toBank, acctNo, pin):
    balancePayload = {
    "head":{
       "fromCtry":fromCtry,
       "fromBank":fromBank,
       "toCtry":toCtry,
       "toBank":toBank
    },
    "body":{
       "acctNo":acctNo,
       "pin":pin
    }
    }
    return balancePayload

def returnJsonW(fromCtry, fromBank, toCtry, toBank, acctNo, pin, amount):
    withdrawPayload = {
    "head":{
       "fromCtry":fromCtry,
       "fromBank":fromBank,
       "toCtry":toCtry,
       "toBank":toBank
    },
    "body":{
       "acctNo":acctNo,
       "pin":pin,
       "amount":amount
    }
    }
    return withdrawPayload

if __name__ ==  "__main__":
    app.run(host='0.0.0.0', port=8443, ssl_context=context, debug=True)
