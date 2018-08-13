package com.example.graysonorr.ohsugar;

/**
 * Created by toolnj1 on 6/08/2018.
 */

public class UserPrefs {

    private double SUGARCUBES = 4.0;
    private double TEASPOONS = 2.0;
    private double GRAMS = 1.0;
    private double TABLESPOONS = 6.0;

    private double selectedDouble = getGRAMS();
    private String selectedString = "Grams";

    public double getSUGARCUBES() {
        return SUGARCUBES;
    }

    public void setSUGARCUBES(double SUGARCUBES) {
        this.SUGARCUBES = SUGARCUBES;
    }

    public double getTEASPOONS() {
        return TEASPOONS;
    }

    public void setTEASPOONS(double TEASPOONS) {
        this.TEASPOONS = TEASPOONS;
    }

    public double getGRAMS() {
        return GRAMS;
    }

    public void setGRAMS(double GRAMS) {
        this.GRAMS = GRAMS;
    }

    public double getTABLESPOONS() {
        return TABLESPOONS;
    }

    public void setTABLESPOONS(double TABLESPOONS) {
        this.TABLESPOONS = TABLESPOONS;
    }

    public double getSelectedDouble() {
        return selectedDouble;
    }

    public void setSelectedDouble(double selectedDouble) {
        this.selectedDouble = selectedDouble;
    }

    public String getSelectedString() {
        return selectedString;
    }

    public void setSelectedString(String selectedString) {
        this.selectedString = selectedString;
    }
}
