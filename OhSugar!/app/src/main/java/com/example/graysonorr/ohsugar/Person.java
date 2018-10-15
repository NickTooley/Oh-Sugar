package com.example.graysonorr.ohsugar;

import java.util.UUID;

/**
 * Created by caincb1 on 27/08/2018.
 */

public class Person {

    UUID personID;
    String gender;
    String age;
    int recSugar;

    public Person(String gender, String age){
        personID = UUID.randomUUID();
        this.gender = gender;
        this.age = age;

        switch(age){
            case "4-6":
                //19g per day, 133g per week
                recSugar = 133;
                break;
            case "7-11":
                //24g per day, 168g per week
                recSugar = 168;
                break;
            default:
                //30g per day, 210g per week
                recSugar = 210;
                break;
        }
    }
}
