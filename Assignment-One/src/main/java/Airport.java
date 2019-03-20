package main.java;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

// For concurrent access, using ThreadLocalRandom instead of Math.random() results
// in less contention and, ultimately, better performance.
public class Airport {

    // Logger
    private static final Logger LOGGER = Logger.getLogger(Airport.class.getName());
    private static long launchTime;

    // Concurrency Control Mechanisms
    private final ReentrantLock isFinished = new ReentrantLock();
    private final Condition isFinishedCondition = isFinished.newCondition();

    // Spawn X people for now.
    private ScheduledExecutorService person_executor;

    // Spawn 1 elevator for now.
    private ExecutorService elevator_executor;

    private Elevator elevatorA;
    private ArrayList<Person> people;
    private ArrayList<Elevator> elevators;
    private AtomicInteger startAmountOfPeople;

    public Airport(int peopleAmount, int elevatorAmount) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s - %5$s%6$s%n");

        // Move the generation of people up here so we can exit program gracefully.
        startAmountOfPeople =  new AtomicInteger(ThreadLocalRandom.current().nextInt(peopleAmount, peopleAmount + 1));
        person_executor = Executors.newScheduledThreadPool(startAmountOfPeople.get());
        elevator_executor = Executors.newFixedThreadPool(elevatorAmount);
        elevators = new ArrayList<>();
    }

    /**
     * Start the airport base-functionality.
     * Allow people in, Allow elevator access, Maximise Concurrency.
     * */
    public void initialize() {
        Thread.currentThread().setName("Airport Thread");
        isFinished.lock();
        elevatorA = new Elevator(400, startAmountOfPeople, isFinished, isFinishedCondition);

        elevators.add(elevatorA);

        // Initialise Elevator Threads/Tasks here
        elevator_executor.execute(elevatorA);

        // Initialise People Threads/Tasks here
        this.schedulePeople(startAmountOfPeople.get(), person_executor);

        // Monitor for state completion here
        this.monitorExecutors();
    }

    private void monitorExecutors() {
        try {
            isFinishedCondition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            isFinished.unlock();
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
        return new Person(weight, luggageWeight, arrivalTime, arrivalFloor, destFloor, elevators);
    }

    @SuppressWarnings("Duplicates")
    private Person generatePerson(int arrivalTime) {
        int weight = ThreadLocalRandom.current().nextInt(50, 100 + 1);
        int luggageWeight = ThreadLocalRandom.current().nextInt(5, 30 + 1);
        int [] floors = ThreadLocalRandom.current().ints(1, 10 + 1)
                .distinct().limit(2).toArray();
        int arrivalFloor = floors[0];
        int destFloor = floors[1];
        return new Person(weight, luggageWeight, arrivalTime, arrivalFloor, destFloor, elevators);
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
        return !isFinished.isLocked();
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
