package src.Model.MiniGames.WallnutBowlingGame;

import src.Enums.ChapterType;
import src.Enums.MiniGameType;
import src.Model.GamePlayType.GamePlay;
import src.Model.Mower;
import src.Model.PlantsAndZombies.Position;
import src.Model.PlantsAndZombies.Zombie;
import src.Model.PlantsAndZombies.ZombieFactory;
import src.Model.User.User;
import src.Model.User.UsersManager;
import src.Model.Wave.FinalWave;
import src.Model.Wave.Wave;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class WalnutBowling extends GamePlay {
    static int spawnX = 1800;
    private final int RED_LINE_LIMIT_X = 3;
    private final MiniGameType miniGameType = MiniGameType.WALNUT_BOWLING;
    private ArrayList<String> conveyorBelt = new ArrayList<>();
    private ArrayList<Walnut> activeWalnuts = new ArrayList<>();

    public WalnutBowling(ChapterType chapterType, int level, int difficulty, User thisUser,
                         ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        super(chapterType, level, difficulty, thisUser, plants, zombies, boosted);
    }

    @Override
    public void sunMaker() {
    }

    public void generateConveyorPlants() {
        if (conveyorBelt.size() < 10) {
            int chance = random.nextInt(100);
            if (chance < 70) {
                conveyorBelt.add("BowlingWalnut");
                System.out.println("A new BowlingWalnut arrived on the conveyor belt!");
            } else if (chance < 90) {
                conveyorBelt.add("ExplodingWalnut");
                System.out.println("A new ExplodingWalnut arrived on the conveyor belt!");
            } else {
                conveyorBelt.add("BigWalnut");
                System.out.println("A new BigWalnut arrived on the conveyor belt!");
            }
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
                newWalnut = new ExplodingWalnut(getRealX(x), getRealY(y));
                break;
            case "BigWalnut":
                newWalnut = new BigWalnut(getRealX(x), getRealY(y));
                break;
            default:
                newWalnut = new BowlingWalnut(getRealX(x), getRealY(y));
                break;
        }

        activeWalnuts.add(newWalnut);
        System.out.printf("Rolled a %s from (%d, %d)\n", walnutType, x, y);
    }

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;
        timeToSpawn = Math.max(timeToSpawn - 1, 0);

        if (totalTicksPassed % 200 == 0) {
            generateConveyorPlants();
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
        Iterator<Zombie> z = gameZombies.iterator();
        while (z.hasNext()) {
            Zombie zombie = z.next();

            if (!zombie.isAlive() || zombie.getCurrentHP() <= 0) {
                killAward(this.thisUser);
                glowingAward(this);
                Position zPos = Position.getRowAndColumn(zombie.getPosition());
                System.out.printf("Zombie of type %s is dead at (%d, %d)\n",
                        zombie.getName(), (int) zPos.getX(), (int) zPos.getY());
                z.remove();
            } else {
                zombie.update();
            }
        }
        updateZombieTiles();

        // Spawning zombies :
        if (timeToSpawn == 0) {
            timeToSpawn = getRandomTime();
            for (Wave thisWave : allWaves) {
                if (thisWave.hasZombiesLeftToSpawn()) {
                    if (!thisWave.getStarted()) {
                        if (thisWave instanceof FinalWave) {
                            System.out.println("The final wave has come.");
                        } else {
                            System.out.printf("Wave %d started.\n", thisWave.getWaveNum());
                        }
                        thisWave.setStarted(true);
                    }
                    String nameOfZ = thisWave.spawnNextZombie().getName();
                    Position positionOfZ;
                    int spawnY = getNextRandomY();
                    if (chapterType != ChapterType.FROSTBITE_CAVES && Math.random() >= 0.9) {
                        positionOfZ = new Position(spawnX - 200, getRealY(spawnY));
                    } else {
                        positionOfZ = new Position(spawnX, getRealY(spawnY));
                    }
                    Zombie newZombie = ZombieFactory.createZombie(nameOfZ, positionOfZ);
                    System.out.printf("Zombie %s spawned at wave %d in lane %d which costed %d.\n",
                            nameOfZ, thisWave.getWaveNum(), spawnY, newZombie.getCost());
                    this.gameZombies.add(newZombie);
                    thisWave.addZombieToSpawned(newZombie);
                }
                if (!thisWave.isReadyForNextWave()) {
                    break;
                }
            }
        }

        // Checking if the end of the game (Losing) + Activate Mowers :
        int x = 20;
        for (Zombie zombie : gameZombies) {
            int yOfz = (int) zombie.getPosition().getY();
            int xOfz = (int) zombie.getPosition().getX();
            Mower thisMower = mowers.stream().filter(m -> getRealY(m.getY()) == yOfz).findFirst().get();

            if (xOfz <= x) {
                if (!thisMower.isUsed()) {
                    System.out.println("The lawn mower in the row " + (int) (thisMower.getY()) + " is triggered and killed these zombies:");
                    thisMower.killZombies(this);
                } else {
                    System.out.println("The zombie ate your brain; LOSER!!!");
                    this.isPaused = true;
                }
            }
        }


        // Checking if the end of the game (Winning) :
        if (checkingTheEndOfTheGame()) {
            onWin();
            System.out.println("Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz.");
            this.isPaused = true;
        }
    }

    @Override
    public void onWin() {
        UsersManager.getInstance().handleMiniGameWin(miniGameType, this.level);
    }

    public ArrayList<Walnut> getActiveWalnuts() {
        return activeWalnuts;
    }
}