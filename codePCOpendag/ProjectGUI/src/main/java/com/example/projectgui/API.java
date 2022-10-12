package com.example.projectgui;

import java.net.ConnectException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URISyntaxException;
import java.net.URI;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class API {

    public static String balanceResponse;
    public static int loginAttemptsLeft;
    public static long displayBalance;
    public static String withdrawResponse;
        //    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, ParseException {
//        //balance("GR","KRIV","GRKRIV0000123401", 1234);
//        balance("T1","NERD","RUNERD0000432100", 4321);
//        //withdraw("GR","KRIV","GRKRIV0000123401",1234, 100);
//        //withdraw("GR","KRIV","GRKRIV0000123401",1234, 100);
//        //test();
//    }
        static public void test(){
            JSONObject headDetails= new JSONObject();
            headDetails.put("fromCtry","GR");
            headDetails.put("fromBank","KRIV");
            headDetails.put("toCtry", "RU");
            headDetails.put("toBank", "NERD");

            JSONObject json_file = new JSONObject();
            json_file.put("head",headDetails);

            JSONObject bodyDetails = new JSONObject();
            bodyDetails.put("acctNo", "RUNERD0000432100");
            bodyDetails.put("pin", "4321");

            json_file.put("body",bodyDetails);

            System.out.println(json_file);
        }

        static public int balance(String toCtry, String toBank, String acctNo, String pin) throws URISyntaxException, IOException, InterruptedException, ParseException {

            JSONObject headDetails= new JSONObject();
            headDetails.put("fromCtry","GR");
            headDetails.put("fromBank","KRIV");
            headDetails.put("toCtry", toCtry);
            headDetails.put("toBank", toBank);

            JSONObject json_file = new JSONObject();
            json_file.put("head",headDetails);

            JSONObject bodyDetails = new JSONObject();
            bodyDetails.put("acctNo", acctNo);
            bodyDetails.put("pin", pin);

            json_file.put("body",bodyDetails);

            System.out.println(json_file);

            System.out.println(json_file.toString());

            //send json to 145.24.222.137:8443/balance
            var uri = new URI("http://145.24.222.137:8443/balance");
            //var uri = new URI("http://145.24.222.139:8443/balance");
            //var uri = new URI("http://127.0.0.1:5000/balance");

            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder(uri).POST(BodyPublishers.ofString(json_file.toJSONString()))
                    .header("Content-type", "application/json").build();
            var response = client.send(request, BodyHandlers.ofString());

            System.out.println(response.body());

            int status = response.statusCode();
            System.out.println("Statuscode: " + status);
            balanceResponse = String.valueOf(status);
            switch(status){
                case 200: //Success, returns balance
                    //read balance from JSON
                    JSONParser parser = new JSONParser();
                    JSONObject responseJSON = (JSONObject) parser.parse(response.body());
                    System.out.println(responseJSON);

                    JSONObject temp = new JSONObject();
                    temp = (JSONObject)responseJSON.get("body");

                    var balance = temp.get("balance");
                    System.out.println("Balance: " + balance);
                    long temp1 = (long)balance;
                    int returnBalance = (int)temp1;
                    displayBalance = (long)balance;

                    System.out.println(returnBalance);
                    return returnBalance;
                case 401: //Incorrect pin, returns attempts left
                    JSONParser parser1 = new JSONParser();
                    JSONObject responseJSON1 = (JSONObject) parser1.parse(response.body());
                    System.out.println(responseJSON1);

                    JSONObject temp2 = new JSONObject();
                    temp2 = (JSONObject)responseJSON1.get("body");

                    long attemptsLeft = (long) temp2.get("attemptsLeft");
                    System.out.println("Attempts left: " + attemptsLeft);
                    int returnAttemptsLeft = (int)attemptsLeft;
                    loginAttemptsLeft = (int)attemptsLeft;
                    System.out.println(returnAttemptsLeft);
                    return returnAttemptsLeft;
                default:
                    return status;
            }
        }

        static public int withdraw(String toCtry, String toBank, String acctNo, String pin, int amount) throws URISyntaxException, IOException, InterruptedException, ParseException {
            //make json with parameters
            JSONObject headDetails= new JSONObject();
            headDetails.put("fromCtry","GR");
            headDetails.put("fromBank","KRIV");
            headDetails.put("toCtry", toCtry);
            headDetails.put("toBank", toBank);

            JSONObject json_file = new JSONObject();
            json_file.put("head",headDetails);

            JSONObject bodyDetails = new JSONObject();
            bodyDetails.put("acctNo", acctNo);
            bodyDetails.put("pin", pin);
            bodyDetails.put("amount", amount);

            json_file.put("body",bodyDetails);

            //send json to 145.24.222.137:8443/withdraw
            var uri = new URI("http://145.24.222.137:8443/withdraw");
            //var uri = new URI("http://127.0.0.1:5000/withdraw");

            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder(uri).POST(BodyPublishers.ofString(json_file.toJSONString()))
                    .header("Content-type", "application/json").build();
            var response = client.send(request, BodyHandlers.ofString());

            System.out.println(response.body());
            int status = response.statusCode();
            System.out.println("Statuscode: " + status);
            withdrawResponse = String.valueOf(status);

            switch (status) {
                case 200: //Success, returns balance after transaction
                    JSONParser parser = new JSONParser();
                    JSONObject responseJSON = (JSONObject) parser.parse(response.body());
                    System.out.println(responseJSON);


                    JSONObject temp = new JSONObject();
                    temp = (JSONObject) responseJSON.get("body");

                    long balance = (long) temp.get("balance");
                    System.out.println("Balance: " + balance);
                    int returnBalance = (int) balance;
                    System.out.println(returnBalance);
                    return returnBalance;
                case 401: //Incorrect pin, return attempts left
                    JSONParser parser1 = new JSONParser();
                    JSONObject responseJSON1 = (JSONObject) parser1.parse(response.body());
                    System.out.println(responseJSON1);

                    JSONObject temp2 = new JSONObject();
                    temp2 = (JSONObject) responseJSON1.get("body");

                    long attemptsLeft = (long) temp2.get("attemptsLeft");
                    System.out.println("Attempts left: " + attemptsLeft);
                    int returnAttemptsLeft = (int)attemptsLeft;
                    System.out.println(returnAttemptsLeft);
                    return returnAttemptsLeft;
                default:
                    return status;
            }
        }
    }