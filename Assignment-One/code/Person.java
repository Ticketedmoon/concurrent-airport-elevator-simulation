package code;

import java.sql.Timestamp;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class Person implements Runnable {

    // Logger
    private static final Logger LOGGER = Logger.getLogger(Person.class.getName());

    // Counter for static concurrent incrementation
    private static int id_counter = 0;

    // Default Person Object parameters.
    private int id = 0;
    private int weight;
    private int arrivalTime;
    private int arrivalFloor;
    private int destFloor;
    private Luggage luggage;
    private boolean pickedCorrectButton;

    public Person(int weight, int luggageWeight, int arrivalTime, int arrivalFloor, int destFloor) {
        this.weight = weight;
        this.arrivalTime = arrivalTime;
        this.arrivalFloor = arrivalFloor;
        this.destFloor = destFloor;
        this.luggage = new Luggage(luggageWeight);
        this.id = ++id_counter;
    }

    @Override
    public void run() {
        Timestamp arrivalTime = new Timestamp(System.currentTimeMillis());
        LOGGER.info(String.format("Person (%d) has arrived at %s", this.id, arrivalTime.toString()));
    }

    public long getArrivalTime() {
        return this.arrivalTime;
    }

    @Override
    public String toString() {
        return String.format("Person ID: {%d}", this.id);
    }
}