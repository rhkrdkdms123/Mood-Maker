package com.example.iothome;

public class SensorData {
    public String hum;
    public String temp;

    public SensorData(){
    }
    public SensorData(String hum, String temp){
        this.hum=hum;
        this.temp=temp;
    }
    public String getHum(){
        return hum;
    }
    public void setHum(String hum){
        this.hum=hum;
    }
    public String getTemp(){
        return temp;
    }
    public void setTemp(String temp){
        this.temp=temp;
    }
}
