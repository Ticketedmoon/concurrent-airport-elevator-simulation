package main.java;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

// For concurrent access, using ThreadLocalRandom instead of Math.random() results
// in less contention and, ultimately, better performance.
public class Airport {

    private static final Logger LOGGER = Logger.getLogger(Airport.class.getName());

    // Concurrency Control Mechanisms
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private ScheduledExecutorService person_executor;
    private ArrayList<Person> people;
    private ArrayList<Elevator> elevators = new ArrayList<>();

    public Airport() {
        // Start elevator once airport is initialised.
        elevators.add(new Elevator(400, lock, condition));
        elevators.get(0).start();
    }

    /**
     * Start the airport base-functionality.
     * Allow people in, Allow elevator access, Maximise Concurrency.
     * */
    public void initialize() {
        int startAmountOfPeople = ThreadLocalRandom.current().nextInt(1, 3 + 1);
        this.person_executor = Executors.newScheduledThreadPool(startAmountOfPeople);
        this.schedulePeople(startAmountOfPeople, person_executor);
    }

    // TODO: Maybe move this method and generatePeople() to plane class?
    // TODO: destFloor must be different to arrival floor.
    @NotNull
    private Person generatePerson() {
        int weight = ThreadLocalRandom.current().nextInt(50, 100 + 1);
        int luggageWeight = ThreadLocalRandom.current().nextInt(5, 30 + 1);
        int arrivalTime =  ThreadLocalRandom.current().nextInt(1, 10 + 1);
        int arrivalFloor =  ThreadLocalRandom.current().nextInt(1, 10 + 1);
        int destFloor =  generateDestFloor(arrivalFloor);
        return new Person(weight, luggageWeight, arrivalTime, arrivalFloor, destFloor, getElevators(), lock, condition);
    }

    private int generateDestFloor(int arrivalFloor)
    {
        int destFloor = ThreadLocalRandom.current().nextInt(1,10 + 1);
        while(destFloor == arrivalFloor)
        {
            destFloor = ThreadLocalRandom.current().nextInt(1,10 + 1);
        }
        return destFloor;
    }
    /**
     * Get first available elevator
     */
    public ArrayList<Elevator> getElevators() {
        // For now just return 1 elevator.
        return this.elevators;
    }

    /**
     * Generate a list of people with random parameters (within a specific range)
     * @param amount The amount of people to generate
     * @return Arraylist of Person objects with random parameters.
     */
    private ArrayList<Person> generatePeople(int amount) {
        ArrayList<Person> people = new ArrayList<>();
        for(int i = 0; i < amount; i++) {
            Person person = this.generatePerson();
            people.add(person);
        }
        return people;
    }

    /**
     * Schedule people to arrive at random intervals to the elevator (just a single elevator for now).
     * @param startAmountOfPeople Total Amount of People eventually to arrive at the elevator.
     * @param taskExecutor The scheduledExecutorService with size, S.
     */
    private void schedulePeople(int startAmountOfPeople, ScheduledExecutorService taskExecutor) {
        LOGGER.info(String.format("Total Threads Generated: %d", startAmountOfPeople));
        this.people = generatePeople(startAmountOfPeople);
        for (Person person : people) {
            taskExecutor.schedule(person, person.getArrivalTime(), TimeUnit.SECONDS);
        }
    }
}
