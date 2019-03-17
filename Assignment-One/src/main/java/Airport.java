package main.java;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

// For concurrent access, using ThreadLocalRandom instead of Math.random() results
// in less contention and, ultimately, better performance.
public class Airport {

    // Logger
    private static final Logger LOGGER = Logger.getLogger(Airport.class.getName());

    // Concurrency Control Mechanisms
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    // Spawn X people for now.
    private ScheduledExecutorService person_executor;

    // Spawn 1 elevator for now.
    private ExecutorService elevator_executor;

    private ArrayList<Person> people;
    private ArrayList<Elevator> elevators;
    private int startAmountOfPeople;

    public Airport() {
        // Move the generation of people up here so we can exit program gracefully.
        startAmountOfPeople = ThreadLocalRandom.current().nextInt(1, 2 + 1);
        person_executor = Executors.newScheduledThreadPool(startAmountOfPeople);
        elevator_executor = Executors.newFixedThreadPool(1);
        elevators = new ArrayList<>();
    }

    /**
     * Start the airport base-functionality.
     * Allow people in, Allow elevator access, Maximise Concurrency.
     * */
    public void initialize() {
        Elevator elevatorA = new Elevator(400, startAmountOfPeople, lock, condition);
        elevators.add(elevatorA);
        Future elevator_status = elevator_executor.submit(elevatorA);
        this.schedulePeople(startAmountOfPeople, person_executor);
        this.monitorExecutors(elevator_status);
    }

    private void monitorExecutors(Future elevator_status) {
        try {
            while (!elevator_status.isDone()) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            elevator_executor.shutdown();
            person_executor.shutdown();
            LOGGER.info("Elevator Service Finished.");
        }
    }

    private Person generatePerson() {
        int weight = ThreadLocalRandom.current().nextInt(50, 100 + 1);
        int luggageWeight = ThreadLocalRandom.current().nextInt(5, 30 + 1);
        int arrivalTime =  ThreadLocalRandom.current().nextInt(1, 10 + 1);
        int [] floors = ThreadLocalRandom.current().ints(1, 10 + 1)
                .distinct().limit(2).toArray();
        int arrivalFloor =  floors[0];
        int destFloor =  floors[1];
        return new Person(weight, luggageWeight, arrivalTime, arrivalFloor, destFloor, elevators, lock, condition);
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
        LOGGER.info(String.format("Total People Threads Generated: %d", startAmountOfPeople));
        this.people = generatePeople(startAmountOfPeople);
        for (Person person : people) {
            taskExecutor.schedule(person, person.getArrivalTime(), TimeUnit.SECONDS);
        }
    }
}
