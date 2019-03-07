package main.java;

import java.util.ArrayList;

/** Optimise Marks:
  * Any form of creativity that you feel like putting in that will add interest to the marking of the project.
  * 1. Variable weight trolleys
  * 2. Nice GUIs
  * 3. Multiple elevators,
  * 4. 'smart' elevators with AI,
  * 5. Random events such as faulty lifts etc... */

public class Elevator {

    // Static elevatorID such that we can increment the class variable
    private static int elevatorID = 0;

    // Static and Switch variables
    private String direction = "up";

    // Start floor is 0 when elevators are created
    private int currentFloor = 0;

    // Max weight is variable passed on constructor parameter.
    private final int maxWeightCapacity;

    // The weight of all the people currently on the elevator.
    private int currentWeight;

    // Data structures for concurrency
    private ArrayList<Person> currentPassengers;

    // RequestQueue implements BlockingQueue (Concurrent queue data structure)
    private RequestQueue requestsForElevator;

    // Introduce possible side effects, locking procedure more difficult but > marks.
    boolean outOfOrder;

    /* Constructor takes maxWeightCapacity as we might be able to have different elevators
     * with different weights, like a freight elevator or something */
    public Elevator(int maxWeightCapacity) {
        this.direction = "up";
        this.currentFloor = 0;
        this.maxWeightCapacity = maxWeightCapacity;
        this.currentPassengers = new ArrayList<>();
        this.requestsForElevator = new RequestQueue();
        this.elevatorID = ++Elevator.elevatorID;
        this.outOfOrder = false;
    }

}