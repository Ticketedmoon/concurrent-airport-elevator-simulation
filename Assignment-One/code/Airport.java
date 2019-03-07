package code;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Airport {

    private static final Logger LOGGER = Logger.getLogger(Airport.class.getName());

    private ScheduledExecutorService taskExecutor;
    private ArrayList<Person> people;

    // TODO: Can the program speed be increased here?
    public void initialize() {

        int startAmountOfPeople = ThreadLocalRandom.current().nextInt(1, 10 + 1);
        this.taskExecutor = Executors.newScheduledThreadPool(startAmountOfPeople);
        this.people = generatePeople(startAmountOfPeople);

        LOGGER.info(String.format("Total Threads Generated: %d", startAmountOfPeople));

        for (Person person : people) {
            taskExecutor.schedule(person, person.getArrivalTime(),  TimeUnit.SECONDS);
        }

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
    // For concurrent access, using ThreadLocalRandom instead of Math.random() results
    // in less contention and, ultimately, better performance.
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
}
