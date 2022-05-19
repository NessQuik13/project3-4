package com.example.projectgui;

public final class Singleton {

    private static Singleton INSTANCE;
    private boolean isEnglish = false;

    public static Singleton getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Singleton();
        }

        return INSTANCE;
    }

    public boolean getIsEnglish() { return isEnglish;}

    public void setEnglish(boolean b) {
        isEnglish = b;
    }
}

