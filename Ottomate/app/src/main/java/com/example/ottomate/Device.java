package com.example.ottomate;

public class Device {
    private double time_in_seconds;
    private double cost_in_rupees;

    public Device(double run_time, double cost) {
        this.time_in_seconds = run_time;
        this.cost_in_rupees = cost;
    }

    public double getTime_in_seconds() {
        return time_in_seconds /1000;
    }

    public void setTime(double run_time) {
        this.time_in_seconds = run_time;
    }

    public double getCost_in_rupees() {
        cost_in_rupees = ((cost_in_rupees /1000)*(((time_in_seconds /100)/60)/60))*5.5;
        cost_in_rupees = Math.round(cost_in_rupees * 100.0) / 100.0;
        return cost_in_rupees;
    }

    public void setCost(double cost) {
        this.cost_in_rupees = cost;
    }
}