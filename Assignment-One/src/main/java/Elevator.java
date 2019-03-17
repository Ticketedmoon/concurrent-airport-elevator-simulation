package main.java;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    private int currentFloor;

    // Max weight is variable passed on constructor parameter.
    private final int maxWeightCapacity;

    // The weight of all the people currently on the elevator.
    private int currentElevatorWeight;

    // Data structures for concurrency
    // Todo: Make this concurrent arraylist?
    private ArrayList<Person> currentPassengers;

    // ConcurrentHashMap useful for discovering all passengers on current floor
    // ConcurrentMap guarantees memory consistency on key/value operations in a multi-threading environment.
    // Structure: (Key, Value) = (person.arrival_floor, person)
    private ConcurrentHashMap<Integer, RequestQueue> requestsForElevator = new ConcurrentHashMap<>();

    // Floors the elevator should visit -- Retains order
    private LinkedHashSet<Integer> floorsToVisit = new LinkedHashSet<>();

    /* Constructor takes maxWeightCapacity as we might be able to have different elevators
     * with different weights, like a freight elevator or something */
    public Elevator(int maxWeightCapacity, int amountOfPeople, ReentrantLock lock, Condition condition) {
        this.direction = "up";
        this.currentFloor = 0;
        this.maxWeightCapacity = maxWeightCapacity;
        this.currentPassengers = new ArrayList<>();
        this.elevatorID = ++Elevator.elevatorID;
        this.amountOfPeople = amountOfPeople;
        this.condition = condition;
        this.lock = lock;

        for(int i = 0; i <= 10; i++) {
            requestsForElevator.put(i, new RequestQueue());
        }
    }

    /**
     * When we queue somebody, the elevator should become 'active'
     * When empty, the elevator 'sleeps' and waits for a request.
     * @param person Person object requesting the elevator
     */
    public void queue(Person person) {
        floorsToVisit.add(person.getArrivalFloor());
        requestsForElevator.get(person.getArrivalFloor()).add(person);
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
        while(amountOfPeople > 0) {
            try {
                Iterator floorsToVisitIterator = floorsToVisit.iterator();
                if (!floorsToVisit.isEmpty() && floorsToVisitIterator.hasNext()) {
                    int floorToVisit = (int) floorsToVisitIterator.next();
                    Person person = (Person) requestsForElevator.get(floorToVisit).peek();
                    System.out.println(person);
                    goToNextFloor(floorToVisit);
                    goToDestinationFloor(person);

                    while(!currentPassengers.isEmpty()) {
                        Person nextPassenger = currentPassengers.get(0);
                        goToDestinationFloor(nextPassenger);
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setDirection(int floor) {
        //TODO sync needed?
        if (floor > currentFloor)
            direction = "up";
        else
            direction = "down";
    }

    // Todo: Remember to re-add passengers that weigh more than limit back onto queue.
    // Todo: if we allow multiple people on a floor (to get on the lift) then we must lock this.
    // Todo: the below must also happen >AFTER< people get off on this current floor (IE people getting off on their dest floor).
    // Todo: In the situation that someone too fat gets on and has to get off, be sure to check other people on the same floor for less weight.
    // Todo: This method can be refactored and reduced.
    // If the elevators current weight + persons weight is less than max, let them on.
    private void allowOnPassengers() {
        RequestQueue peopleAtCurrentFloor = requestsForElevator.get(currentFloor);
        boolean fatPeopleHere = false;

        while (!peopleAtCurrentFloor.isEmpty()) {
            Person person = (Person) peopleAtCurrentFloor.remove();
            if (currentElevatorWeight + person.getPassengerPlusLuggageWeight() < maxWeightCapacity) {
                this.currentPassengers.add(person);
                currentElevatorWeight += person.getPassengerPlusLuggageWeight();
                LOGGER.info(String.format(person.toString() + " successfully got on elevator " + this.elevatorID + " at floor " + this.currentFloor + " and requests floor {%d}", person.getDestFloor()));
                LOGGER.info("Elevator Passengers: " + this.currentPassengers.toString());
                LOGGER.info("Elevator Weight: " + this.currentElevatorWeight + "kgs.");
            } else {
                //TODO When implemented print passenger id and possibly weight of them and luggage?
                //TODO Need to gracefully handle their request being denied and make sure that it does in fact >EVENTUALLY< get sorted
                LOGGER.warning("Elevator weight capacity exceeded! Removing " + person.toString());
                fatPeopleHere = true;
            }
        }

        if (fatPeopleHere) {
            floorsToVisit.add(currentFloor);
        }
    }

    private void removePassengers(Person person) {
        lock.lock();
        try {
            this.currentPassengers.remove(person);
            this.currentElevatorWeight -= (person.getPassengerPlusLuggageWeight());
            this.amountOfPeople--;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    private void goToNextFloor(int requestedFloor) throws InterruptedException {
        // Update direction to travel to arrival floor.
        setDirection(requestedFloor);
        while (currentFloor != requestedFloor) {
            moveElevator();
        }
        allowOnPassengers();
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