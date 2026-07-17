package Model.Wave;

public class FinalWave extends Wave {

    public FinalWave(double previousDifficulty, int baseBudget) {
        super(previousDifficulty * 2.0, baseBudget);
    }

    public boolean isGameWon(int deadZombiesCount) {
        return this.pendingZombies.isEmpty() && deadZombiesCount == this.totalZombies;
    }

    public void finishGame() {
        System.out.println("Dear humanz, zis is not done yet...");
    }
}