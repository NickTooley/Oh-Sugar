package com.example.graysonorr.ohsugar.db;

import java.util.ArrayList;

/**
 * Created by Connor on 10/09/2018.
 */

public class ShoppingList {

    String name;
    String timestamp;
    ArrayList<Food> list;
    double totalSugar;
    double recSugar;

    public ShoppingList(String name, String timestamp, ArrayList<Food> list, double totalSugar, double recSugar){
        this.name = name;
        this.timestamp = timestamp;
        this.list = list;
        this.totalSugar = totalSugar;
        this.recSugar = recSugar;
    }

    public void AddToList(Food item){
        list.add(item);
    }
    public void RemoveFromList(Food item) {for(int i = 0; i < list.size(); i++){
        if (list.get(i).foodID == item.foodID) {
            list.remove(i);
        }
    }}

    public String getName() {return name;}
    public void setName(String name){ this.name = name; }

    public String getTimestamp() {return timestamp;}

    public ArrayList<Food> getList() {return list;}
    public void setList(ArrayList<Food> list) { this.list = list; }

    public double getTotalSugar() {return totalSugar;}
    public void setTotalSugar(double totalSugar) { this.totalSugar = totalSugar; }

    public double getRecSugar() {return recSugar;}
    public void setRecSugar(double recSugar) { this.recSugar = recSugar; }
}
