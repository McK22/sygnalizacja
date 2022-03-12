package sygnalizacja.controller;

import sygnalizacja.symulator.TrafficLight;

public class Main {
    public static int nextId = 1;

    public static void main(String[] args){
        Controller controller = new Controller(3600);
    }

    public static int getNextId(){
        return nextId++;
    }
}
