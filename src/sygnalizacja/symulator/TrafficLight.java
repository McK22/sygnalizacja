package sygnalizacja.symulator;

import sygnalizacja.controller.Main;

import java.util.LinkedList;

import static sygnalizacja.symulator.LightColor.*;

public class TrafficLight {
    private final int id;
    private final int greenLength;
    private final int yellowLength = 2;
    private final int redLength;
    private final double spawnInterval;
    private final LinkedList<Car> line;

    private TrafficLight withPriority;
    private TrafficLight next;
    private LightColor lightColor;
    private int secondsToLightSwitch;
    private int totalWaitingTime = 0;
    private int totalCars = 0;
    private double lastSpawnTime = 0;

    public TrafficLight(int id, int greenLength, int redLength, int carsInHour, TrafficLight next){
        this.id = id;
        this.greenLength = greenLength;
        this.redLength = redLength;
        spawnInterval = 3600.0 / carsInHour;
        line = new LinkedList<>();
        lightColor = GREEN;
        secondsToLightSwitch = greenLength;
        this.next = next;
    }

    public TrafficLight(int id, int greenLength, int redLength, int carsInHour){
        this.id = id;
        this.greenLength = greenLength;
        this.redLength = redLength;
        spawnInterval = 3600.0 / carsInHour;
        line = new LinkedList<>();
        lightColor = GREEN;
        secondsToLightSwitch = greenLength;
        next = null;
    }

    public void step(int time){
        if(lastSpawnTime + spawnInterval <= time){
            spawnCar(time);
            lastSpawnTime = time;
        }

        print(time);
        int cars = line.size();
        while(cars-- > 0){
            Car car = line.poll();
            assert car != null;
            if(!car.move(lightColor))//the car didn't pass the lights
                line.add(car);
            else
                removeCar(car, time);
        }

        if(--secondsToLightSwitch == 0)
            switchLight();
    }

    public LightColor getLightColor(){
        return lightColor;
    }

    public int getTotalCars(){
        return totalCars;
    }

    public int getTotalWaitingTime(){
        return totalWaitingTime;
    }

    public int getId(){
        return id;
    }

    public double getAverageWaitingTime(){
        return (double)totalWaitingTime / totalCars;
    }

    public void setWithPriority(TrafficLight withPriority){
        this.withPriority = withPriority;
    }

    public void setNext(TrafficLight next){
        this.next = next;
    }

    public void print(int time){
        System.out.print("Waiting at " + id + ": " + line.size() + " color: ");
        if(lightColor == GREEN)
            System.out.print("GREEN");
        else if(lightColor == YELLOW)
            System.out.print("YELLOW");
        else
            System.out.print("RED");
        System.out.println(" secondsLeft: " + secondsToLightSwitch + " time: " + time);
    }

    public void addCar(Car car, int time){
        if(!line.isEmpty())
            car.setNext(line.getLast());
        line.add(car);
        car.reload(time);
    }

    private void switchLight(){
        if(lightColor == GREEN){
            lightColor = YELLOW;
            secondsToLightSwitch = yellowLength;
        }else if(lightColor == YELLOW){
            lightColor = RED;
            secondsToLightSwitch = redLength;
        }else{
            lightColor = GREEN;
            secondsToLightSwitch = greenLength;
        }
    }

    private void removeCar(Car car, int time){
        totalCars++;
        totalWaitingTime += car.getWaitingTime(time);
        if(next != null)
            next.addCar(car, time);
        if(!line.isEmpty())
            line.getFirst().setNext(null);
    }

    private void spawnCar(int time){
        if(line.isEmpty())
            line.add(new Car(time, null));
        else
            line.add(new Car(time, line.getLast()));
    }
}
