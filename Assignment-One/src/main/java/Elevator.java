package main.java;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
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
public class Elevator implements Runnable {

    // Logger
    private static final Logger LOGGER = Logger.getLogger(Elevator.class.getName());

    // Concurrency Control Mechanisms
    private final ReentrantLock lock;
    private final Condition condition;

    // Static elevatorID such that we can increment the class variable
    private static int elevatorID = 0;

    // Static and Switch variables
    private String direction;

    // Amount of people (!Important for graceful program stoppage)
    private int amountOfPeople;

    // Start floor is 0 when elevators are created
    private int currentFloor ;

    // Max weight is variable passed on constructor parameter.
    private final int maxWeightCapacity;

    // The weight of all the people currently on the elevator.
    private int currentElevatorWeight;

    // Data structures for concurrency
    private ArrayList<Person> currentPassengers;

    // RequestQueue implements BlockingQueue (Concurrent queue data structure)
    private RequestQueue requestsForElevator;

    // Introduce possible side effects, locking procedure more difficult but > marks.
    private boolean outOfOrder;

    /* Constructor takes maxWeightCapacity as we might be able to have different elevators
     * with different weights, like a freight elevator or something */
    public Elevator(int maxWeightCapacity, int amountOfPeople, ReentrantLock lock, Condition condition) {
        this.direction = "up";
        this.currentFloor = 0;
        this.maxWeightCapacity = maxWeightCapacity;
        this.currentPassengers = new ArrayList<>();
        this.requestsForElevator = new RequestQueue();
        this.elevatorID = ++Elevator.elevatorID;
        this.outOfOrder = false;
        this.amountOfPeople = amountOfPeople;
        this.lock = lock;
        this.condition = condition;
    }

    /**
     * When we queue somebody, the elevator should become 'active'
     * When empty, the elevator 'sleeps' and waits for a request.
     * @param person Person object requesting the elevator
     */
    public void queue(Person person) {
        requestsForElevator.add(person);
    }

    public int getElevatorID() {
        return elevatorID;
    }

    @Override
    public String toString() {
        return String.format("Elevator with ID %d", Elevator.elevatorID);
    }

    @Override
    public void run() {
        // Run this method forever?
        // Maybe change this from Thread to executor service? less verbose.
        while(amountOfPeople > 0) {
            try {
                if (!requestsForElevator.isEmpty()) {
                    Person person = (Person) requestsForElevator.remove();
                    goToArrivalFloor(person);
                    goToDestinationFloor(person);
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info(String.format("Elevator with ID {%d} Ending Gracefully.", getElevatorID()));
    }

    private void setDirection(int floor) {
        //TODO sync needed?
        if (floor > currentFloor)
            direction = "up";
        else
            direction = "down";
    }

    private void allowOnPassengers(Person person) {
        // If the elevators current weight + persons weight is less than max, let them on.
        //TODO if we allow multiple people on a floor (to get on the lift) then we must lock this.
        //TODO the below must also happen >AFTER< people get off on this current floor (IE people getting off on their dest floor).
        if (currentElevatorWeight + person.getPassengerPlusLuggageWeight() < maxWeightCapacity) {
            this.currentPassengers.add(person);
            currentElevatorWeight += person.getPassengerPlusLuggageWeight();
            LOGGER.info(String.format(person.toString() + " successfully got on elevator " + this.elevatorID +" at floor " + this.currentFloor + " and requests floor {%d}", person.getDestFloor()));
            LOGGER.info("Elevator Passengers: " + this.currentPassengers.toString());
            LOGGER.info("Elevator Weight: " + this.currentElevatorWeight + "kgs.");
        }
        else {
            //TODO When implemented print passenger id and possibly weight of them and luggage?
            //TODO Need to gracefully handle their request being denied and make sure that it does in fact >EVENTUALLY< get sorted
            LOGGER.warning("Elevator weight capacity exceeded! Removing " + person.toString());
        }
    }

    private void removePassengers(Person person) {
        lock.lock();
        try {
            this.currentPassengers.remove(person);
            this.currentElevatorWeight -= person.getWeight();
            this.amountOfPeople--;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    private void goToArrivalFloor(Person person) throws InterruptedException {
        // Update direction to travel to arrival floor.
        setDirection(person.getArrivalFloor());
        while (currentFloor != person.getArrivalFloor()) {
            moveElevator();
        }
        allowOnPassengers(person);
    }

    private void goToDestinationFloor(Person person) throws InterruptedException {
        // Update direction to travel to destination floor.
        //TODO blocked by people getting on?
        setDirection((person.getDestFloor()));
        while (currentFloor != person.getDestFloor()) {
            moveElevator();
        }
        removePassengers(person);
    }

    private void moveElevator() throws InterruptedException {
        // We must traverse to the correct floor.
        // Elevator can move between floors at 0.5 seconds? for now at least.
        if (direction.equals("down"))
            currentFloor--;
        else
            currentFloor++;

        LOGGER.info(String.format("Elevator with ID {%d} now on floor {%d} moving %s", this.getElevatorID(), this.currentFloor, this.direction));
        Thread.sleep(5000);
    }
}