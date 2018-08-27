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
                recSugar = 19;
                break;
            case "7-11":
                recSugar = 24;
                break;
            default:
                recSugar = 30;
                break;
        }
    }
}
