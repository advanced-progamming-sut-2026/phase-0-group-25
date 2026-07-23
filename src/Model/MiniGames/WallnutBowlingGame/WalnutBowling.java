package src.Model.MiniGames.WallnutBowlingGame;

import src.Enums.ChapterType;
import src.Model.GamePlayType.GamePlay;
import src.Model.Mower;
import src.Model.PlantsAndZombies.Zombie;
import src.Model.User.User;

import java.util.ArrayList;
import java.util.Iterator;

public class WalnutBowling extends GamePlay {

    private ArrayList<String> conveyorBelt = new ArrayList<>();
    private ArrayList<Walnut> activeWalnuts = new ArrayList<>();
    private final int RED_LINE_LIMIT_X = 3;

    public WalnutBowling(ChapterType chapterType, int level, int difficulty, User thisUser) {
        super(chapterType, level, difficulty, thisUser, new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public void sunMaker() {
    }

    public void getPlants() {
        if (conveyorBelt.size() < 10) {
            int chance = random.nextInt(100);
            if (chance < 70) conveyorBelt.add("BowlingWalnut");
            else if (chance < 90) conveyorBelt.add("ExplodingWalnut");
            else conveyorBelt.add("BigWalnut");
            System.out.println("A new walnut arrived on the conveyor belt!");
        }
    }

    public void plantWalnut(int conveyorIndex, int x, int y) {
        if (x > RED_LINE_LIMIT_X) {
            System.out.println("You can only plant behind the RED LINE (Column 1 to 3)!");
            return;
        }
        if (conveyorIndex < 0 || conveyorIndex >= conveyorBelt.size()) {
            System.out.println("Invalid slot in conveyor belt!");
            return;
        }

        String walnutType = conveyorBelt.remove(conveyorIndex);
        Walnut newWalnut;

        switch (walnutType) {
            case "ExplodingWalnut":
                newWalnut = new ExplodingWalnut(x, y);
                break;
            case "BigWalnut":
                newWalnut = new BigWalnut(x, y);
                break;
            default:
                newWalnut = new BowlingWalnut(x, y);
                break;
        }

        activeWalnuts.add(newWalnut);
        System.out.printf("Rolled a %s from (%d, %d)\n", walnutType, x, y);
    }

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;

        if (totalTicksPassed % 50 == 0) {
            getPlants();
        }

        Iterator<Walnut> wIter = activeWalnuts.iterator();
        while (wIter.hasNext()) {
            Walnut w = wIter.next();
            if (w.isActive()) {
                w.update(this);
            } else {
                wIter.remove();
            }
        }

        Iterator<Zombie> zIter = gameZombies.iterator();
        while (zIter.hasNext()) {
            Zombie zombie = zIter.next();
            if (!zombie.isAlive() || zombie.getCurrentHP() <= 0) {
                killAward(this.thisUser);
                glowingAward(this);
                zIter.remove();
            } else {
                zombie.update();
            }
        }
        updateZombieTiles();

        // Checking if the end of the game (Losing) + Activate Mowers :
        int x = mowers.get(0).getX();
        for(Zombie zombie : gameZombies) {
            int yOfz = (int) zombie.getPosition().getY();
            int xOfz = (int) zombie.getPosition().getX();
            Mower thisMower = mowers.stream().filter(p -> p.getY() == yOfz).findFirst().get();

            if (xOfz <= x) {
                if (!thisMower.isUsed()) {
                    System.out.println("The lawn mower in the row" + yOfz + "is triggered and killed these zombies:");
                    thisMower.killZombies(this);
                }
                else {
                    System.out.println("The zombie ate your brain; LOSER!!!");
                }
            }
        }

        // Checking if the end of the game (Winning) :
        if (checkingTheEndOfTheGame()) {
            System.out.println("Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz.");
        }
    }

    public ArrayList<Walnut> getActiveWalnuts() {
        return activeWalnuts;
    }
}