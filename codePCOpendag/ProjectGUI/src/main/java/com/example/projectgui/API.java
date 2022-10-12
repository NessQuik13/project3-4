package com.example.projectgui;

import java.io.*;
import java.net.ConnectException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URISyntaxException;
import java.net.URI;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class API {
    private static String hardCodedPin = "1234";
    public static int balanceResponse;
    public static int loginAttemptsLeft = 3;
    public static long displayBalance;
    public static int withdrawResponse;
    public static void resetlogin() {
        loginAttemptsLeft = 3;
    }

        //    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, ParseException {
//        //balance("GR","KRIV","GRKRIV0000123401", 1234);
//        balance("T1","NERD","RUNERD0000432100", 4321);
//        //withdraw("GR","KRIV","GRKRIV0000123401",1234, 100);
//        //withdraw("GR","KRIV","GRKRIV0000123401",1234, 100);
//        //test();
//    }
        static public int readBalance() throws IOException{
                FileReader reader = new FileReader("balance.txt");
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line;
                int balance = 0;
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println(line);
                    balance = Integer.parseInt(line);
                }
                reader.close();
                return balance;
        }
        static public void writebalance(int input) throws IOException {
            FileWriter writer = new FileWriter("balance.txt");
            writer.write(String.valueOf(input));
            writer.close();
        }

        static public void balance(String pin) throws IOException {
            if (!pin.equals(hardCodedPin)) {
                balanceResponse = 401;
                loginAttemptsLeft -= 1;
            }
            int balance = readBalance();
            balanceResponse = 200;
            displayBalance = balance;
        }

        static public void withdraw(int amount) throws IOException {
            int balance = readBalance();
            int newBalance = balance - amount;
            if (newBalance < 0) {
                writebalance(newBalance);
                withdrawResponse = 200;
                return;
            }
            withdrawResponse = 401;


        }
    }