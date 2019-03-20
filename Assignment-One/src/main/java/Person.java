package main.java;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import static main.java.Airport.retrieveTime;

public class Person implements Runnable {

    // Logger
    private static final Logger LOGGER = Logger.getLogger(Person.class.getName());

    // Concurrency Control Mechanisms
    private final ReentrantLock personLock = new ReentrantLock();
    private final Condition personCondition = personLock.newCondition();

    // Synchronization primitives
    private boolean hasGotOnElevator = false;
    private boolean hasGotOffElevator = false;

    // Counter for static concurrent incrementation
    private static int id_counter = 0;

    // Each person has access to all the elevators
    private ArrayList<Elevator> elevators;

    // Default Person Object parameters.
    private int id;
    private int weight;
    private int arrivalTime;
    private int arrivalFloor;
    private int destFloor;
    private Luggage luggage;

    public Person(int weight, int luggageWeight, int arrivalTime, int arrivalFloor, int destFloor, ArrayList<Elevator> elevators) {
        this.weight = weight;
        this.arrivalTime = arrivalTime;
        this.arrivalFloor = arrivalFloor;
        this.destFloor = destFloor;
        this.luggage = new Luggage(luggageWeight);
        this.elevators = elevators;
        this.id = ++id_counter;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Person:" + getId());
        LOGGER.info(String.format("Person with ID {%d} has arrived at the airport at time {%s}", this.id, retrieveTime()));
        requestElevator();
    }

    /**
     * Individual Calling of the elevator.
     */
    private void requestElevator() {
        Thread.currentThread().setName("Person:" + getId());
        personLock.lock();
        try {
            LOGGER.info(String.format("%s has requested the elevator[%s] to floor {%s} with destination floor {%s} at %s seconds",
                        this, this.elevators.get(0).getElevatorID(), this.getArrivalFloor(), this.getDestFloor(), retrieveTime()));

            // For name just focus on 1 elevator working, we can get more later.
            this.elevators.get(0).queue(this);

            // Wrap .awaits() in while loops on behalf of 'Spurious Wake-ups'
            while(!hasGotOnElevator) {
                personCondition.await();
            }

            LOGGER.info(String.format(this + " successfully got on elevator {%s} at floor {%d} and requests floor {%d}", this.elevators.get(0).getElevatorID(), arrivalFloor, getDestFloor()));
            LOGGER.info("Elevator Passengers: " + this.elevators.get(0).getCurrentPassengers());
            LOGGER.info("Elevator Weight: " + this.elevators.get(0).getCurrentElevatorWeight() + "kgs.");

            while(!hasGotOffElevator) {
                personCondition.await();
            }

            LOGGER.info(String.format("Person with ID {%d} has arrived at their destination floor " +
                    "{%d} and has left the elevator at %s seconds.", this.id, this.destFloor, retrieveTime()));
            LOGGER.info("Elevator Weight: " + this.elevators.get(0).getCurrentElevatorWeight() + "kgs.");
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

    public ReentrantLock getPersonLock() {
        return personLock;
    }

    public Condition getPersonCondition() {
        return personCondition;
    }

    public void getOnElevator() {
        hasGotOnElevator = true;
    }

    public void getOffElevator() {
        hasGotOffElevator = true;
    }
    @Override
    public String toString() {
        return String.format("Person with ID {%d}", this.id);
    }
}