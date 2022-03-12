package sygnalizacja.controller;

import sygnalizacja.symulator.TrafficLight;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Controller {
    private final LinkedList<TrafficLight> trafficLights;
    private final int duration;

    public Controller(int duration){
        this.duration = duration;
        trafficLights = new LinkedList<>();
        createTrafficLights();
        perform();
    }

    private void perform(){
        int time = 0;
        int totalWaitingTime = 0;
        int totalCars = 0;
        while(time < duration){
            for(TrafficLight tl : trafficLights)
                tl.step(time);
            time++;
        }
        for(TrafficLight tl: trafficLights){
            System.out.println(tl.getId() + " " + tl.getTotalWaitingTime() + " " + tl.getTotalCars());
            totalWaitingTime += tl.getTotalWaitingTime();
            totalCars += tl.getTotalCars();
        }
        //System.out.println((double)totalWaitingTime / totalCars);
    }

    private void createTrafficLights(){
        ArrayList<Integer> nextId = new ArrayList<>();
        try{
            List<String> lines = Files.readAllLines(Paths.get("testJunction.txt"));
            for(String line: lines){
                if(line.length() == 0 || line.charAt(0) == '#')
                    continue;

                //id - 0, greenLength - 1, redLength - 2, carsInHour - 3 next - 4, coliding - 5, 6, 7, ...
                ArrayList<Integer> data = getNumbersFromLine(line);
                TrafficLight trafficLight = new TrafficLight(data.get(0), data.get(1), data.get(2), data.get(3));
                trafficLights.add(trafficLight);
                nextId.add(data.get(4));
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        for(int i = 0; i < nextId.size(); i++)
            for(TrafficLight tl: trafficLights)
                if(nextId.get(i) == tl.getId())
                    trafficLights.get(i).setNext(tl);
    }

    //assumes the line format is correct
    private ArrayList<Integer> getNumbersFromLine(String line){
        ArrayList<Integer> result = new ArrayList<>();
        int begin = 0;
        for(int i = 1; i < line.length(); i++){
            if(line.charAt(i) == ' '){
                result.add(Integer.parseInt(line.substring(begin, i)));
                begin = i + 1;
            }
        }
        result.add(Integer.parseInt(line.substring(begin)));
        return result;
    }
}
