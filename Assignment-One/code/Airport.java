import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Airport {

    private static ExecutorService taskExecutor = Executors.newFixedThreadPool(4);
    private static ArrayList<Person> people = new ArrayList<>();

    public void initialize() {

        // Person Constructor:
        // int weight, int luggageID, int luggageWeight,
        // int arrivalTime, int arrivalFloor, int destFloor

        System.out.println("test");

        Person j = new Person(1, 200, 70, 10, 5, 9);
        Person k = new Person(2, 300, 80, 7, 5, 7);
        Person m = new Person(3, 400, 90, 6, 1, 1);

        taskExecutor.submit(j);
        taskExecutor.shutdownNow();
    }
}
