package main.java;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import static java.lang.Thread.*;

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
    private final ReentrantLock elevatorLock = new ReentrantLock();
    private final Condition elevatorCondition = elevatorLock.newCondition();

    private final ReentrantLock personLock;
    private final Condition personCondition;

    // Static elevatorID such that we can increment the class variable
    private String elevatorID;
    private static int elevatorCount = 0;

    // Static and Switch variables
    private String direction;

    // Amount of people (!Important for graceful program stoppage)
    private AtomicInteger amountOfPeople;

    // Start floor is 0 when elevators are created
    private AtomicInteger currentFloor;

    // Max weight is variable passed on constructor parameter.
    private final int maxWeightCapacity;

    // The weight of all the people currently on the elevator.
    private int currentElevatorWeight;

    // Data structures for concurrency
    private LinkedBlockingQueue<Person> currentPassengers;

    // ConcurrentHashMap useful for discovering all passengers on current floor
    // ConcurrentMap guarantees memory consistency on key/value operations in a multi-threading environment.
    // Structure: (Key, Value) = (person.arrival_floor, person)
    private ConcurrentHashMap<Integer, LinkedBlockingQueue<Person>> requestsForElevator = new ConcurrentHashMap<>();

    // Arrival/Destination floors the elevator should visit -- Retains order
    private LinkedList<Person> floorsToVisit = new LinkedList<>();

    // Control state for graceful program termination.
    private ReentrantLock isFinished;
    private Condition isFinishedCondition;

    /* Constructor takes maxWeightCapacity as we might be able to have different elevators
     * with different weights, like a freight elevator or something */
    public Elevator(int maxWeightCapacity, AtomicInteger amountOfPeople, ReentrantLock airportClosedLock, Condition airportClosedCondition,
                    ReentrantLock personLock, Condition personCondition) {
        this.direction = "up";
        this.currentFloor = new AtomicInteger(0);
        this.maxWeightCapacity = maxWeightCapacity;
        this.currentPassengers = new LinkedBlockingQueue<>();
        this.amountOfPeople = amountOfPeople;
        this.isFinished = airportClosedLock;
        this.isFinishedCondition = airportClosedCondition;
        this.personLock = personLock;
        this.personCondition = personCondition;

        elevatorID = getChar(elevatorCount);
        elevatorCount = ++Elevator.elevatorCount;

        // Set-up floors as a map. (Int -> LinkedBlockingQueue)
        //todo if multiple elevators this will be protected.
        for(int floorNo = 0; floorNo <= 10; floorNo++) {
            requestsForElevator.put(floorNo, new LinkedBlockingQueue<>());
        }
    }

    /**
     * When we queue somebody, the elevator should become 'active'
     * When empty, the elevator 'sleeps' and waits for a request.
     * @param person Person object requesting the elevator
     */
    public void queue(Person person) {
        floorsToVisit.add(person);
        requestsForElevator.get(person.getArrivalFloor()).add(person);
    }

    public String getChar(int i) {
        return Character.toString(i < 0 || i > 25 ? '?' : (char)('A' + i));
    }

    // Getter: Elevator ID
    public String getElevatorID() { return elevatorID; }

    // Getter: Current Elevator Weight
    public int getCurrentElevatorWeight() { return currentElevatorWeight; }

    // Getter: Clone of current passengers - no state leakage
    public LinkedBlockingQueue<Person> getCurrentPassengers() { return currentPassengers; }

    // Getter: Current Floor
    public int getCurrentFloor() {
        return currentFloor.get();
    }

    @Override
    public String toString() {
        return String.format("Elevator with ID %s", this.getElevatorID());
    }

    @Override
    public void run() {
        currentThread().setName("Elevator:" + this.elevatorID);
        elevatorLock.lock();
        isFinished.lock();
        try {
            while (amountOfPeople.get() > 0) {
                if (!floorsToVisit.isEmpty()) {
                    visitArrivalFloor(floorsToVisit.peek().getArrivalFloor());
                    visitDestinationOfPassengers();
                }
                else {
                    LOGGER.info(String.format("Elevator with ID {%s} sleeping on floor {%d}", getElevatorID(), currentFloor.get()));
                }
                sleep(3000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            elevatorLock.unlock();
            isFinishedCondition.signal();
            isFinished.unlock();
        }
    }

    //TODO sync needed?
    private void setDirection(int floor) {
        if (floor > currentFloor.get())
            direction = "up";
        else if(floor == currentFloor.get())
            direction = "stationary";
        else
            direction = "down";

        try {
            sleep(500);
            LOGGER.info(String.format("Elevator with ID {%s} moving: %s", getElevatorID(), direction));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Todo: Remember to re-add passengers that weigh more than limit back onto queue.
    // Todo: if we allow multiple people on a floor (to get on the lift) then we must elevatorLock this.
    // Todo: the below must also happen >AFTER< people get off on this current floor (IE people getting off on their dest floor).
    // Todo: In the situation that someone too fat gets on and has to get off, be sure to check other people on the same floor for less weight.
    // Todo: This method can be refactored and reduced.
    // If the elevators current weight + persons weight is less than max, let them on.
    private void allowOnPassengers() {
        elevatorLock.lock();
        try {
            LinkedBlockingQueue peopleOnFloorWaiting = requestsForElevator.get(currentFloor.get());
            while (!peopleOnFloorWaiting.isEmpty()) {
                Person person = (Person) peopleOnFloorWaiting.peek();
                personLock.lock();
                try {
                    if (currentElevatorWeight + person.getPassengerPlusLuggageWeight() < maxWeightCapacity) {
                        currentPassengers.add(person);
                        currentElevatorWeight += person.getPassengerPlusLuggageWeight();
                        requestsForElevator.get(currentFloor.get()).remove(person);
                        floorsToVisit.remove(person);

                        person.getOnElevator();
                        personCondition.signalAll();
                    } else {
                        LOGGER.warning("Person with ID {" + person + "} attempting to get on elevator with ID {" + elevatorID + "}");
                        LOGGER.warning("Elevator weight capacity exceeded! Person with ID {" + person + "} being removed");
                        return;
                    }
                } finally {
                    personLock.unlock();
                }
            }
        } finally {
            elevatorCondition.signal();
            elevatorLock.unlock();
        }
    }

    private void removePassengers() {
        elevatorLock.lock();
        try {
            for (Person person : currentPassengers) {
                personLock.lock();
                try {
                    if (person.getDestFloor() == currentFloor.get()) {
                        this.currentPassengers.remove(person);
                        this.currentElevatorWeight -= person.getPassengerPlusLuggageWeight();
                        this.amountOfPeople.decrementAndGet();

                        person.getOffElevator();
                        personCondition.signalAll();
                    }
                } finally {
                    personLock.unlock();
                }
            }
        } finally {
            elevatorCondition.signal();
            elevatorLock.unlock();
        }
    }

    // Todo: what would happen if we let on too many people before the initial elevator request is arrived at?
    // Todo: Update this method to account for multiple people waiting on the elevator travelling in the same direction.
    private void visitArrivalFloor(int requestedFloor) throws InterruptedException {
        // Update direction to travel to arrival floor.
        if (!requestsForElevator.get(requestedFloor).isEmpty()) {
            while (currentFloor.get() != requestedFloor) {
                setDirection(requestedFloor);
                moveElevator();
            }
        }

        allowOnPassengers();
    }

    // Update direction to travel to destination floor.
    //TODO blocked by people getting on?
    private void visitDestinationOfPassengers() throws InterruptedException {
        while (!currentPassengers.isEmpty()) {
            int floor = currentPassengers.peek().getDestFloor();
            while (currentFloor.get() != floor) {
                setDirection(floor);
                moveElevator();
            }
        }
    }

    private void moveElevator() throws InterruptedException {
        elevatorLock.lock();
        try {
            if (direction.equals("down"))
                currentFloor.getAndDecrement();
            else
                currentFloor.getAndIncrement();

            LOGGER.info(String.format("Elevator with ID {%s} now on floor {%d}", getElevatorID(), this.currentFloor.get()));
            sleep(500);

            removePassengers();
            allowOnPassengers();
            sleep(500);

        } finally {
            elevatorLock.unlock();
        }
    }
}