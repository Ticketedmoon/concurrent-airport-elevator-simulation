package main.java;

import java.util.ArrayList;
import java.util.logging.Logger;

/** Optimise Marks:
 * Any form of creativity that you feel like putting in that will add interest to the marking of the project.
 * 1. Variable weight trolleys
 * 2. Nice GUIs
 * 3. Multiple elevators,
 * 4. 'smart' elevators with AI,
 * 5. Random events such as faulty lifts etc... */

/**
 * Elevator should be a thread, such that it can 'listen' for event changes.
 * We'll need a locking mechanism on behalf of:
 * 1. When an elevator is requested, people must wait for the elevator.
 *    - Elevator will 'signal()' them when it arrives at the appropriate floor.
 * 2. When a person gets on with a weight > current active capacity.
 *    - This person must get off the elevator and 'sleep()' until an elevator with
 *      more space is available.
 * 3. When an elevator breaks down, what exactly happens?
 *    - Passengers will remain in a 'sleep' state until they arrive at there floor, be it, longer than usual.
 *    - Perhaps when the elevator breaks down, there is a variable time between a fixed range (say between 5 - 10 seconds)
 *      before the elevator is recovered and back in action.
 *
 * (Any more conditions be sure to include here)
 */
public class Elevator {

    private static final Logger LOGGER = Logger.getLogger(Elevator.class.getName());

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
    private boolean outOfOrder;

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

    /**
     * When we queue somebody, the elevator should become 'active'
     * When empty, the elevator 'sleeps' and waits for a request.
     * @param person Person object requesting the elevator
     */
    public void queue(Person person) {
        requestsForElevator.add(person);
        LOGGER.info(String.format("Elevator with ID [%d] has been called by [%s]", Elevator.elevatorID, person));
    }

    @Override
    public String toString() {
        return String.format("Elevator with ID %d", Elevator.elevatorID);
    }

}