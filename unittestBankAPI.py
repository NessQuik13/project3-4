from email import message
import requests
import unittest


#code to test API on server

class testAPIMethods(unittest.TestCase):
    def testBalance(self):
        url = 'http://145.24.222.137:8443/balance'
        json_file = {
                "head": {
                    "fromCtry": "GR",
                    "fromBank": "KRIV",
                    "toCtry":   "GR",
                    "toBank":   "KRIV"
                },
                "body": {
                    "pin": 1234,
                    "acctNo":"GRKRIV0000123401"
                }
            }
        r = requests.post(url, json = json_file)
        self.assertEqual(r.status_code, 200)
        print("With correct credentials: ✔")
            
        

        json_file['body']['pin'] = 1235
        r = requests.post(url, json = json_file)
        print("With incorrect pin and correct acctNo: ✔")
        self.assertEqual(r.status_code, 401)

        json_file['body']['pin'] = 1234
        json_file['body']['acctNo'] = "GRKRIV1111123401"
        r = requests.post(url, json = json_file)
        print("With correct pin and incorrect acctNo: ✔")
        self.assertEqual(r.status_code, 404)

        json_file['body']['acctNo'] = "GRKRIV0000123402"
        r = requests.post(url, json = json_file)
        print("With incorrect passnumber: ✔")
        self.assertEqual(r.status_code, 404)
        
        print("Balance request test finished...")
        
    def testWithdraw(self):
        print("Withdraw test started...")
        url = 'http://145.24.222.137:8443/withdraw'
        json_file = {
                "head": {
                    "fromCtry": "GR",
                    "fromBank": "KRIV",
                    "toCtry":   "GR",
                    "toBank":   "KRIV"
                },
                "body": {
                    "pin": 1234,
                    "acctNo":"GRKRIV0000123401",
                    "amount" : 50
                }
            }    
        
        r = requests.post(url, json = json_file)
        print("With correct credentials: ✔")
        self.assertEqual(r.status_code, 200)

        json_file['body']['pin'] = 1235
        r = requests.post(url, json = json_file)
        print("With incorrect pin and correct acctNo: ✔")
        self.assertEqual(r.status_code, 401)

        json_file['body']['acctNo'] = "GRKRIV0000123501"
        json_file['body']['pin'] = 1234
        r = requests.post(url, json = json_file)
        print("With correct pin and incorrect acctNo: ✔")
        self.assertEqual(r.status_code, 404)

        json_file['body']['acctNo'] = "GRKRIV0000123402"
        r = requests.post(url, json = json_file)
        print("With incorrect passnumber: ✔")
        self.assertEqual(r.status_code, 404)

        json_file['body']['acctNo'] = "GRKRIV0000123401"
        json_file['body']['amount'] = 99999999999
        r = requests.post(url, json = json_file)
        print("With withdraw amount higher than balance: ✔")
        self.assertEqual(r.status_code, 406)
        
        print("Tests completed...")
    

foo = testAPIMethods()
foo.testBalance()
foo.testWithdraw()