public class Launcher {

    private ExecutorService taskExecutor = Executors.newFixedThreadPool(4); 

    public static void main(String[] args) {
        // Person Constructor:
        // int weight, int luggageID, int luggageWeight, 
        // int arrivalTime, int arrivalFloor, int destFloor

        System.out.println("test");

        Person j = new Person(1, 200, 70, 10, 5, 9);
        Person k = new Person(2, 300, 80, 7, 5, 7);
        Person m = new Person(3, 400, 90, 6, 1, 1);

        List <Person> people = new ArrayList();

        List<Runnable<?>> tasks = people; // your tasks 

        taskExecutor.execute(j);

    }

}