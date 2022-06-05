package com.example.projectgui;

    import javafx.beans.property.SimpleStringProperty;
    public class FileData {
        SimpleStringProperty balance;
        SimpleStringProperty transaction;

        FileData(String balance, String transaction) {
            this.balance = new SimpleStringProperty(balance);
            this.transaction = new SimpleStringProperty(transaction);
        }
        public String getBalance(){
            return balance.get();
        }
        public void setBalance(String balance){
            this.balance.set(balance);
        }
        public String getTransaction(){
            return transaction.get();
        }
        public void setTransaction(String transaction){
            this.transaction.set(transaction);
        }
    }

