package main.java;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// For concurrent access, using ThreadLocalRandom instead of Math.random() results
// in less contention and, ultimately, better performance.
public class Airport {

    // Logger
    private static final Logger LOGGER = Logger.getLogger(Airport.class.getName());
    private static long launchTime;
    private static FileHandler fh;

    // Concurrency Control Mechanisms
    private final ReentrantLock airportClosedLock = new ReentrantLock();
    private final Condition airportClosedCondition = airportClosedLock.newCondition();

    // Concurrency Control Mechanisms
    private final ReentrantLock personLock = new ReentrantLock();
    private final Condition personCondition = personLock.newCondition();

    // Spawn X people for now.
    private ScheduledExecutorService person_executor;

    // Spawn 1 elevator for now.
    private ExecutorService elevator_executor;

    private Elevator elevatorA;
    private ArrayList<Person> people;
    private AtomicInteger startAmountOfPeople;

    public Airport(int peopleAmount) {
        // Move the generation of people up here so we can exit program gracefully.
        startAmountOfPeople =  new AtomicInteger(ThreadLocalRandom.current().nextInt(peopleAmount, peopleAmount + 1));
        person_executor = Executors.newScheduledThreadPool(startAmountOfPeople.get());
        elevator_executor = Executors.newFixedThreadPool(1);
    }

    /**
     * Start the airport base-functionality.
     * Allow people in, Allow elevator access, Maximise Concurrency.
     * */
    public void initialize() {
        Thread.currentThread().setName("Airport Thread");
        airportClosedLock.lock();
        elevatorA = new Elevator(400, startAmountOfPeople, airportClosedLock, airportClosedCondition,
                personLock, personCondition);

        // Initialise Elevator Threads/Tasks here
        elevator_executor.execute(elevatorA);

        // Initialise People Threads/Tasks here
        this.schedulePeople(startAmountOfPeople.get(), person_executor);

        // Monitor for state completion here
        this.monitorExecutors();
    }

    private void monitorExecutors() {
        try {
            while(startAmountOfPeople.get() > 0){
                airportClosedCondition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            airportClosedLock.unlock();
            elevator_executor.shutdown();
            person_executor.shutdown();
            LOGGER.info("Elevator Service Finished.");
        }
    }

    @SuppressWarnings("Duplicates")
    private Person generatePerson() {
        int weight = ThreadLocalRandom.current().nextInt(50, 100 + 1);
        int luggageWeight = ThreadLocalRandom.current().nextInt(5, 30 + 1);
        int arrivalTime =  ThreadLocalRandom.current().nextInt(3, 120 + 1);
        int [] floors = ThreadLocalRandom.current().ints(1, 10 + 1)
                .distinct().limit(2).toArray();
        int arrivalFloor = floors[0];
        int destFloor = floors[1];
        return new Person(weight, luggageWeight, arrivalTime, arrivalFloor, destFloor, elevatorA,
                personLock, personCondition);
    }

    @SuppressWarnings("Duplicates")
    private Person generatePerson(int arrivalTime) {
        int weight = ThreadLocalRandom.current().nextInt(50, 100 + 1);
        int luggageWeight = ThreadLocalRandom.current().nextInt(5, 30 + 1);
        int [] floors = ThreadLocalRandom.current().ints(1, 10 + 1)
                .distinct().limit(2).toArray();
        int arrivalFloor = floors[0];
        int destFloor = floors[1];
        return new Person(weight, luggageWeight, arrivalTime, arrivalFloor, destFloor, elevatorA, personLock, personCondition);
    }

    /**
     * Generate a list of people with random parameters (within a specific range)
     * @param amount The amount of people to generate
     * @return Arraylist of Person objects with random parameters.
     */
    private ArrayList<Person> generatePeople(int amount) {
        ArrayList<Person> people = new ArrayList<>();
        Person firstPerson = generatePerson(0);
        people.add(firstPerson);
        //magic number to take into account the default person arriving at 0.
        for(int i = 0; i < amount-1; i++) {
            Person person = this.generatePerson();
            people.add(person);
        }
        return people;
    }

    public static long retrieveTime()
    {
        long elapsedTime = System.nanoTime() - launchTime;
        return TimeUnit.NANOSECONDS.toSeconds(elapsedTime);
    }

    public Elevator getElevatorA() {
        return elevatorA;
    }

    public boolean isServiceFinished() {
        return !airportClosedLock.isLocked();
    }

    /**
     * Schedule people to arrive at random intervals to the elevator (just a single elevator for now).
     * @param startAmountOfPeople Total Amount of People eventually to arrive at the elevator.
     * @param taskExecutor The scheduledExecutorService with size, S.
     */
    private void schedulePeople(int startAmountOfPeople, ScheduledExecutorService taskExecutor) {
        LOGGER.info(String.format("Total People Threads Generated: %d", startAmountOfPeople));
        this.people = generatePeople(startAmountOfPeople);
        launchTime = System.nanoTime();
        for (Person person : people) {
            taskExecutor.schedule(person, person.getArrivalTime(), TimeUnit.SECONDS);
        }
    }
}
