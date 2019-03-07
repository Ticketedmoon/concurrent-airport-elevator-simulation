package code;

import java.util.UUID;

public class Luggage {
    private String luggageId;
    private int weight;

    /**
     * Luggage Object Constructor.
     * LuggageID is a random string generated using java.util.UUID.
     * @param weight Weight amount passed in (Generally random)
     */
    public Luggage(int weight) {
        this.luggageId = UUID.randomUUID().toString();
        this.weight = weight;
    }

    public int getWeight() {
        return this.weight;
    }

    public String getLuggageId() {
        return this.luggageId;
    }
}