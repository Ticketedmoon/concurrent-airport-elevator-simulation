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
    // after each new Elevator object is created.
    private static int elevatorID = 0;

    // Static and Switch variables
    private String direction = "up";
    private int currentFloor = 0;
    private final int maxWeightCapacity;
    private boolean isFull;

    // Data structures for concurrency
    private ArrayList<Person> currentPassengers;

    // RequestQueue implements BlockingQueue (Concurrent queue data structure)
    private RequestQueue requestsForElevator;

    // Attempt: Multiple concurrent elevators for more marks.

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