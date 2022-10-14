package com.example.projectgui;

import java.io.*;
import java.io.IOException;

public class API {
    private static String hardCodedPin = "1234";
    public static int balanceResponse;
    public static int loginAttemptsLeft = 3;
    public static long displayBalance;
    public static int withdrawResponse;
    public static void resetlogin() {
        loginAttemptsLeft = 3;
    }

    static public int readBalance() throws IOException{
        FileReader reader = new FileReader("balance.txt");
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        int balance = 0;
        while ((line = bufferedReader.readLine()) != null) {
            //System.out.println(line);
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
                return;
            }
            int balance = readBalance();
            balanceResponse = 200;
            displayBalance = balance;
        }

        static public void withdraw(int amount) throws IOException {
            int balance = readBalance();
            int newBalance = balance - amount;
            System.out.println("new balance" + newBalance);
            if (newBalance >= 0) {
                displayBalance = balance;
                writebalance(newBalance);
                withdrawResponse = 200;
                return;
            }
            withdrawResponse = 401;
        }
    }