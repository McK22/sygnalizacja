package sygnalizacja.symulator;

import sygnalizacja.controller.Main;

import static sygnalizacja.symulator.LightColor.GREEN;
import static sygnalizacja.symulator.LightColor.YELLOW;

public class Car {
    public static final int MAX_SPEED = 50; //speed in km/h
    public static final int INITIAL_DISTANCE = 100; //distance in meters
    public static final int MIN_GAP = 5; //gap in meters;
    public static final double SPEED_MULTIPLIER = 3.6;

    private final int id;

    private int spawnTime;
    private double distanceToLights;
    private double speed;
    private Car next;

    public Car(int spawnTime, Car next){
        id = Main.getNextId();
        this.spawnTime = spawnTime;
        this.next = next;
        distanceToLights = INITIAL_DISTANCE;
        speed = MAX_SPEED;
    }

    //returns true if passed the lights
    public boolean move(LightColor lightColor){
        if(next != null){
            double gap = distanceToLights - next.distanceToLights - speed / SPEED_MULTIPLIER;
            if(gap > speed / 2 && gap > MIN_GAP) {
                speed = Math.min(MAX_SPEED, speed + 10.0);
                gap = distanceToLights - next.distanceToLights - speed / SPEED_MULTIPLIER;
            }
            if(gap < speed / 2 || gap < MIN_GAP){
                speed = next.speed;
                gap = Math.max(speed / 2, MIN_GAP);
                distanceToLights = next.distanceToLights + gap + speed / SPEED_MULTIPLIER;
            }
        }else if(lightColor == GREEN || (lightColor == YELLOW && distanceToLights < speed / 2))
            speed = Math.min(MAX_SPEED, speed + 10.0);
        else{
            speed = Math.max(0.0, speed - 10.0);
            speed = Math.max(speed, distanceToLights / 2);
        }
        distanceToLights -= speed / SPEED_MULTIPLIER;
        print();
        return distanceToLights < 0;
    }

    public void setNext(Car next){
        this.next = next;
    }

    public void print(){
        Integer nextId;
        if(next != null)
            nextId = next.id;
        else
            nextId = null;
        System.out.println("id: " + id + " distanceToLights: " + Math.round(distanceToLights) + " speed: " + Math.round(speed) + " nextId: " + nextId);
    }

    public void reload(int time){
        spawnTime = time;
        distanceToLights = INITIAL_DISTANCE;
    }

    public int getWaitingTime(int currentTime){
        return currentTime - spawnTime;
    }
}
