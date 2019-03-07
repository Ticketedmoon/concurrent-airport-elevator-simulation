package main.java;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class Airport {

    private static final Logger LOGGER = Logger.getLogger(Airport.class.getName());

    private ScheduledExecutorService taskExecutor;
    private ArrayList<Person> people;
    private Elevator elevatorA;
    private ArrayList<ScheduledFuture> orderPeopleArrived = new ArrayList<ScheduledFuture>();

    // Just initialise 1 elevator for now.
    public Airport() {
        this.elevatorA = new Elevator(400);
    }

    // For concurrent access, using ThreadLocalRandom instead of Math.random() results
    // in less contention and, ultimately, better performance.
    public void open() {
        int startAmountOfPeople = ThreadLocalRandom.current().nextInt(1, 10 + 1);
        this.taskExecutor = Executors.newScheduledThreadPool(startAmountOfPeople);
        this.schedulePeople(startAmountOfPeople, taskExecutor);

        // Wait 10 seconds - given the max range a person can take to arrive is 10 seconds.
        try {
           taskExecutor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            taskExecutor.shutdownNow();
        }
    }

    // TODO: Maybe move this method and generatePeople() to plane class?
    @NotNull
    private Person generatePerson() {
        int weight = ThreadLocalRandom.current().nextInt(50, 100 + 1);
        int luggageWeight = ThreadLocalRandom.current().nextInt(5, 30 + 1);
        int arrivalTime =  ThreadLocalRandom.current().nextInt(1, 10 + 1);
        int arrivalFloor =  ThreadLocalRandom.current().nextInt(1, 10 + 1);
        // TODO: destFloor must be different to arrival floor.
        int destFloor =  ThreadLocalRandom.current().nextInt(1, 10 + 1);

        return new Person(weight, luggageWeight, arrivalTime, arrivalFloor, destFloor);
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
     * Reverse collection due to ScheduledExecutor (Callable) being appended inversely.
     * @param startAmountOfPeople Total Amount of People eventually to arrive at the elevator.
     * @param taskExecutor The scheduledExecutorService with size, S.
     */
    private void schedulePeople(int startAmountOfPeople, ScheduledExecutorService taskExecutor) {
        LOGGER.info(String.format("Total Threads Generated: %d", startAmountOfPeople));
        this.people = generatePeople(startAmountOfPeople);
        for (Person person : people) {
            orderPeopleArrived.add(taskExecutor.schedule(person, person.getArrivalTime(), TimeUnit.SECONDS));
        }
        Collections.reverse(orderPeopleArrived);
    }
}
