package com.example.demo;

import java.util.ArrayList;

public class Stage {
    private String stageName;
    private String theme;
    private int minSide;
    private int maxSide;
    private int maxWord;
    private ArrayList<String> gridWords = new ArrayList<>();

    public Stage(String stageName, String theme, int minSide, int maxSide){
        this.stageName = stageName;
        this.theme = theme;
        this.minSide = minSide;
        this.maxSide = maxSide;
    }

    public String getStageName(){
        return this.stageName;
    }
    public String getTheme(){
        return this.theme;
    }
    public void setMaxWord(int wordNum){
        this.maxWord = wordNum;
    }
    public int getMaxWord(){
        return this.maxWord;
    }
    public ArrayList<String> getGridWords(){
        return this.gridWords;
    }

    public int getMaxSide() {
        return this.maxSide;
    }

    public int getMinSide() {
        return this.minSide;
    }
}
