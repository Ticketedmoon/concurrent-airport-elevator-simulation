import java.util.logging.Logger;
import java.util.UUID;

public class Person implements Runnable {

    // Logger
    private static final Logger LOGGER = Logger.getLogger(Person.class.getName());

    // Object creation time - useful for understanding elapsed time between threads.
    private static long timeOfCreation = System.nanoTime();

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

        String luggageID =  UUID.randomUUID().toString();
        this.luggage = new Luggage(luggageID, luggageWeight);
        this.id = ++id_counter;
    }

    @Override
    public void run() {
        System.out.println(System.nanoTime());
        double arrivalTime = (System.nanoTime() - timeOfCreation) / 1000000.0;
        LOGGER.info(String.format("Person (%d) has arrived at time: %.2f. (Milliseconds)", this.id, arrivalTime));
    }
}