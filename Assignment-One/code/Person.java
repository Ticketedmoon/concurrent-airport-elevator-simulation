public class Person
{
    // ID should either be a number or a name.
    private static int id = 0;
    private int weight;
    private Luggage luggage;
    private int arrivalTime;
    private int arrivalFloor;
    private int destFloor;

    private boolean pickedCorrectButton; //do mathRandom, could be tricky to do without locking

    public Person(int weight, int luggageID, int luggageWeight, int arrivalTime, int arrivalFloor, int destFloor) {
        this.weight = weight;
        this.luggage = new Luggage(luggageID, luggageWeight);
        this.arrivalTime = arrivalTime;
        this.arrivalFloor = arrivalFloor;
        this.destFloor = destFloor;

        this.id = ++Person.id;
    }
}