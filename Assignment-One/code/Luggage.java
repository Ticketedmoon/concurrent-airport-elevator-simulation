public class Luggage {
    private String luggageId;
    private int weight;

    public Luggage(String luggageId, int weight) {
        this.luggageId = luggageId;
        this.weight = weight;
    }

    public int getWeight() {
        return this.weight;
    }

    public String getLuggageId() {
        return this.luggageId;
    }
}