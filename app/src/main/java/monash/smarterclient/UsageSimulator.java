package monash.smarterclient;

import java.util.Random;

public class UsageSimulator {
    private double fridgeUsage;
    private double washMachUsge;
    private double airCondUsage;

    public UsageSimulator(){
        Random random = new Random();
        fridgeUsage = random.nextDouble() / 2 + 0.3;
        washMachUsge = random.nextDouble() * 0.9 + 0.4;
        airCondUsage = random.nextDouble() * 4 + 1;
    }

    public double getFridgeUsage(){
        return fridgeUsage;
    }

    public double getAirCondUsage(){
        return airCondUsage;
    }

    public double getWashMachUsge() {
        return washMachUsge;
    }

    /*
      @param int totalHour
      @return double[] hourlyFridgeUsge;
      return a hourly fridge usage based on the given hours
      that user requires.
     */
    public double[] generFridgeUsage(int totalHour){
        double[] hourlyFridgeUsage = new double[totalHour];
        for (int i = 0; i < hourlyFridgeUsage.length; i ++){
            hourlyFridgeUsage[i] = fridgeUsage;
        }
        return hourlyFridgeUsage;
    }

    //return the hourly fridge usage in a day, 24 pieces data in the return array.
    public double[] generFridgeUsage(){
        double[] hourlyFridgeUsage = new double[24];
        int i = 0;
        while (i < 24){
            hourlyFridgeUsage[i] = fridgeUsage;
            i ++;
        }
        return hourlyFridgeUsage;
    }

    //return the hourly usage of washing machine in 24 hours(per day)
    public double[] generWashMachUsage(){
        double[] hourlyWashMachUsage = new double[24];
        // Initial hourly usage by filling it with zero.
        int i = 0;
        while (i < 24){
            hourlyWashMachUsage[i] = 0;
            i ++;
        }

        Random random = new Random();
        int workHours = random.nextInt(4); // Generate total working hours in a day
        int startWork = random.nextInt(15 - workHours) + 6; // Generate starting time in a day
        for (;workHours > 0; workHours --, startWork ++){   // Insert working hours into result
            hourlyWashMachUsage[startWork] = washMachUsge;
        }
        return hourlyWashMachUsage;
    }

    // Require 24 hours temperature data for generating the air condition usage
    public double[] generAirCondUsage(double temperature[]){
        int counter = 0;
        int hours = temperature.length;
        double[] hourlyAirCondUsage = new double[hours];
        for (int i = 0; i < hours; i ++){
            if (temperature[i] >= 26 || temperature[i] <= 17)
                hourlyAirCondUsage[i] = airCondUsage;
            else
                hourlyAirCondUsage[i] = 0;
        }
        return hourlyAirCondUsage;
    }
}
