public class Luggage
{
    private int luggageId;
    private int weight;

    public Luggage(int luggageId, int weight) {
        this.luggageId = luggageId;
        this.weight = weight;
    }

    public int getWeight() {
        return this.weight;
    }

    public int getLuggageId() {
        return this.luggageId;
    }
}