package src.Model.Quests.Events;

public class MowerTriggeredEvent extends Event {
    private final int killedCount;

    public MowerTriggeredEvent(int killedCount) {
        this.killedCount = killedCount;
    }

    public int getKilledCount() {
        return killedCount;
    }
}