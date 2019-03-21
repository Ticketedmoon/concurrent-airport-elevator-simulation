package main.java;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import static main.java.Airport.retrieveTime;

/** Person Thread Class
 *  Elevator communication needed for accessing, using and getting off elevator.*/
public class Person implements Runnable {

    // Logger
    private static final Logger LOGGER = Logger.getLogger(Person.class.getName());

    // General Person lock & Condition - Shared among people
    private final ReentrantLock personLock;
    private final Condition personCondition;

    // Synchronization primitives
    private AtomicBoolean hasGotOnElevator = new AtomicBoolean(false);
    private AtomicBoolean hasGotOffElevator = new AtomicBoolean(false);

    // Counter for static concurrent incrementation
    private static int id_counter = 0;

    // Each person has access to all the elevators
    private Elevator elevatorA;

    // Default Person Object parameters.
    private int id;
    private int weight;
    private int arrivalTime;
    private int arrivalFloor;
    private int destFloor;
    private Luggage luggage;

    public Person(int weight, int luggageWeight, int arrivalTime, int arrivalFloor, int destFloor, Elevator elevatorA,
                  ReentrantLock personLock, Condition personCondition) {
        this.weight = weight;
        this.arrivalTime = arrivalTime;
        this.arrivalFloor = arrivalFloor;
        this.destFloor = destFloor;
        this.luggage = new Luggage(luggageWeight);
        this.elevatorA = elevatorA;
        this.personLock = personLock;
        this.personCondition = personCondition;
        this.id = ++id_counter;
    }

    @Override
    public void run() {
        LOGGER.info(String.format("Person with ID {%d} has arrived at the airport at time {%s}", this.id, retrieveTime()));
        Thread.currentThread().setName("Person:" + getId());
        requestElevator(elevatorA);
    }

    /**
     * Individual Calling of the elevator.
     */
    private void requestElevator(Elevator elevator) {
        personLock.lock();
        try {
            LOGGER.info(String.format("%s has requested the elevator[%s] to floor {%s} with destination floor {%s} at %s seconds",
                        this, elevator.getElevatorID(), this.getArrivalFloor(), this.getDestFloor(), retrieveTime()));

            // Focus on 1 elevator working.
            elevator.queue(this);

            // Wrap .awaits() in while loops on behalf of 'Spurious Wake-ups'
            while(!hasGotOnElevator.get()) {
                personCondition.await();
            }

            LOGGER.info(String.format(this + " successfully got on elevator {%s} at floor {%d} and requests floor {%d}",
                        elevator.getElevatorID(), arrivalFloor, getDestFloor()));
            LOGGER.info("Elevator Passengers: " + elevator.getCurrentPassengers());
            LOGGER.info("Elevator Weight: " + elevator.getCurrentElevatorWeight() + "kgs.");

            // Have they got off the elevator? If No -> Sleep.
            while(!hasGotOffElevator.get()) {
                personCondition.await();
            }

            LOGGER.info(String.format("Person with ID {%d} has arrived at their destination floor " +
                        "{%d} and has left the elevator at %s seconds.", this.id, this.destFloor, retrieveTime()));
            LOGGER.info("Elevator Weight: " + elevator.getCurrentElevatorWeight() + "kgs.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            personLock.unlock();
        }
    }

    public long getArrivalTime() {
        return this.arrivalTime;
    }

    public int getArrivalFloor() {
        return this.arrivalFloor;
    }

    public int getDestFloor() {
        return this.destFloor;
    }

    public int getId() { return this.id; }

    public int getWeight() { return this.weight; }

    public int getLuggageWeight() { return this.luggage.getWeight(); }

    public int getPassengerPlusLuggageWeight() { return this.getWeight() + this.getLuggageWeight(); }

    public void getOnElevator() { hasGotOnElevator.getAndSet(true); }

    public void getOffElevator() {
        hasGotOffElevator.getAndSet(true);
    }

    @Override
    public String toString() {
        return String.format("Person with ID {%d}", this.id);
    }
}