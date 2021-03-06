package util;

//Programmed by Quinn Berrman
//Student number: 20363251


import java.io.Serializable;

public class Stat implements Serializable {
    private String name;
    private float baseValue;
    private float currentValue;
    private int baseCost;
    private int currentCost;
    private boolean incremental;

    protected Stat() {
        
    }

    public Stat(String name, float value, int cost, boolean incremental) {
        this.name = name;
        this.baseValue = value;
        this.baseCost = cost;
        this.currentValue = baseValue;
        this.currentCost = cost;
        this.incremental = incremental;
    }

    public float getValue() {
        return currentValue;
    }
    public float getNextValue() {
        if(incremental) {
            return currentValue + (baseValue*0.1f);
        } else {
            return currentValue * 0.9f;
        }
    } 
    public boolean buyNext(int gold) {
        if(gold > getCost()) {
            currentValue = getNextValue();
            return true;
        }
        return false;
    }
    public void incrementCost() {
        currentCost = getNextCost();
    }
    public int getCost() {
        return currentCost;
    }
    public int getNextCost() {
        return currentCost + (baseCost/10);
    }
    public String getName() {
        return name;
    }
}
