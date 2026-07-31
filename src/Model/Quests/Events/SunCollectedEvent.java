package src.Model.Quests.Events;

public class SunCollectedEvent extends Event {
    private final int amount;

    public SunCollectedEvent(int amount) {
        this.amount = amount;
    }

    public int getAmount() { return amount; }
}