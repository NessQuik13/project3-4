package com.example.projectgui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;
public class BillTypeScreenController {
    @FXML
    private Label T1;
    public void initialize() {
        Singleton language = Singleton.getInstance();
        if (!language.getIsEnglish()) {
            T1.setText("Geef een biljet voorkeur op");
        }
    }
    @FXML
    private Button submitTen;
    @FXML
    protected void submitTenAction() {
        SceneController controller = SceneController.getInstance();
        if (calculateBills(10) == 1) {
            try {
                controller.setScene("WithdrawScreen.fxml");
            }catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            controller.setScene("SubmitRequestScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button submitTwenty;
    @FXML
    protected void submitTwentyAction() {
        SceneController controller = SceneController.getInstance();
        if (calculateBills(20) == 1) {
            try {
                controller.setScene("WithdrawScreen.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            controller.setScene("SubmitRequestScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button submitFifty;
    @FXML
    protected void submitFiftyAction() {
        SceneController controller = SceneController.getInstance();
        if (calculateBills(50) == 1) {
            try {
                controller.setScene("WithdrawScreen.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            controller.setScene("SubmitRequestScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button submitReturn;
    @FXML
    protected void submitReturnAction() {
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("WithdrawScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button submitAbort;
    @FXML
    protected void submitAbortAction() {
        SceneController controller = SceneController.getInstance();
        ArduinoControls.abort();
        try {
            controller.setScene("LanguageScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private int calculateBills(int preference) {
        int money = WithdrawScreenController.getGeld();
        int bills10 = 0;
        int bills20 = 0;
        int bills50 = 0;
        int curBills10 = WithdrawScreenController.getCurrent10();
        int curBills20 = WithdrawScreenController.getCurrent20();
        int curBills50 = WithdrawScreenController.getCurrent50();
        switch (preference) {
            case 10:
                while (money >= 10  && bills10 < 10 && curBills10 > bills10) {
                    money -= 10;
                    bills10 += 1;
                }
                while (money >= 20  && bills20 < 10 && curBills20 > bills20) {
                    money -= 20;
                    bills20 += 1;
                }
                while (money >= 50 && bills50 < 10 && curBills50 > bills50 ) {
                    money -= 50;
                    bills50 += 1;
                }
                // if geld / amount of bills bigger than current, throw error
                if (money > 0) {
                    System.out.println("not enough money in atm");
                    // give user some notification somewhere
                    return 1;
                }
            case 20:
                while (money >= 20 && bills20 < 10 && curBills20 > bills20) {
                    money -= 20;
                    bills20 += 1;
                }
                while (money >= 50 && bills50 < 10 && curBills50 > bills50) {
                    money -= 50;
                    bills50 += 1;
                }
                while (money >= 10 && bills10 < 10 && curBills10 > bills10) {
                    money -= 10;
                    bills10 += 1;
                }
                // if geld / amount of bills bigger than current, throw error
                if (money > 0) {
                    System.out.println("not enough money in atm");
                    // give user some notification somewhere
                    return 1;
                }
            case 50:
                while (money >= 50 && bills50 < 10 && curBills50 > bills50) {
                    money -= 50;
                    bills50 += 1;
                }
                while (money >= 20 && bills20 < 10 && curBills20 > bills20) {
                    money -= 20;
                    bills20 += 1;
                }
                while (money >= 10 && bills10 < 10 && curBills10 > bills10) {
                    money -= 10;
                    bills10 += 1;
                }
                // if geld / amount of bills bigger than current, throw error
                if (money > 0) {
                    System.out.println("not enough money in atm, something went wrong in withdrawscreen");
                    // give user some notification somewhere
                    return 1;
                }
        }
        SubmitRequestScreenController.setBills10Dispense(bills10);
        SubmitRequestScreenController.setBills20Dispense(bills20);
        SubmitRequestScreenController.setBills50Dispense(bills50);
        WithdrawScreenController.setCurrent10(curBills10 - bills10);
        WithdrawScreenController.setCurrent20(curBills20 - bills20);
        WithdrawScreenController.setCurrent50(curBills50 - bills50);
        return 0;
    }
}
