package com.example.demo;

public class GridSpot {
    private String btnText;
    private boolean checked;
    public GridSpot(){
        this.btnText = "-";
        this.checked = false;
    }

    public void clickedSpot(String word){
        btnText = word;
    }

    public String getBtnText(){
        return this.btnText;
    }

    public void setChecked(){
        this.checked = true;
    }
    public void unCheck(){
        this.checked = false;
    }

    public boolean isChecked(){
        return this.checked;
    }



}
