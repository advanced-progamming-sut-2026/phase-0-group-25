package com.test1.PlantsVsZombies.src.Model.MiniGames.WallnutBowlingGame;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.MiniGameType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.ZombieFactory;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.Model.Wave.FinalWave;
import com.test1.PlantsVsZombies.src.Model.Wave.Wave;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class WalnutBowling extends GamePlay {
    private static final float CARD_START_Y = 980f;
    private static final float CARD_HEIGHT = 105f;
    private static final float CARD_SPACING = 11f;
    static int spawnX = 1850;
    private final int RED_LINE_LIMIT_X = 3;
    private final MiniGameType miniGameType = MiniGameType.WALNUT_BOWLING;
    private final ArrayList<BowlingCard> conveyorBelt = new ArrayList<>();
    private final ArrayList<Walnut> activeWalnuts = new ArrayList<>();
    private final List<float[]> activeExplosions = new ArrayList<>();

    public WalnutBowling(ChapterType chapterType, int level, int difficulty, User thisUser,
                         ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        super(chapterType, level, difficulty, thisUser, plants, zombies, boosted);
        setLevelObjectives("Use the wall nuts to kill the zombies.");
        this.mySuns = 0;

        for (int i = 0; i < 3; i++) {
            generateConveyorPlants();
        }
    }

    @Override
    public void sunMaker() {
    }

    public void generateConveyorPlants() {
        if (conveyorBelt.size() < 7) {
            int chance = random.nextInt(100);
            String type;
            if (chance < 65) {
                type = "BowlingWalnut";
            } else if (chance < 85) {
                type = "ExplodingWalnut";
            } else {
                type = "BigWalnut";
            }


            BowlingCard card = new BowlingCard(type, 80f);
            conveyorBelt.add(card);
            recalculateTargets();
        }
    }

    public void recalculateTargets() {
        for (int i = 0; i < conveyorBelt.size(); i++) {
            float targetY = CARD_START_Y - (i * (CARD_HEIGHT + CARD_SPACING));
            conveyorBelt.get(i).setTargetY(targetY);
        }
    }


    public void plantWalnut(BowlingCard card, int x, int y) {
        if (x > RED_LINE_LIMIT_X || card == null) return;
        if (!conveyorBelt.contains(card)) return;

        conveyorBelt.remove(card);
        recalculateTargets();

        float realX = getRealX(x);
        float realY = getRealY(y) + 15f;
        Walnut newWalnut;

        switch (card.getNutType()) {
            case "ExplodingWalnut" -> newWalnut = new ExplodingWalnut(realX, realY);
            case "BigWalnut" -> newWalnut = new BigWalnut(realX, realY);
            default -> newWalnut = new BowlingWalnut(realX, realY);
        }

        activeWalnuts.add(newWalnut);
    }

    public void plantWalnut(int conveyorIndex, int x, int y) {
        if (conveyorIndex < 0 || conveyorIndex >= conveyorBelt.size()) return;
        BowlingCard card = conveyorBelt.get(conveyorIndex);
        plantWalnut(card, x, y);
    }

    public void addExplosionEffect(float x, float y) {
        activeExplosions.add(new float[]{x, y, 0f});
    }

    public void updateWithDelta(float delta) {
        if (isPaused) return;


        for (BowlingCard card : conveyorBelt) {
            card.update(delta);
        }

        Iterator<float[]> expIt = activeExplosions.iterator();
        while (expIt.hasNext()) {
            float[] exp = expIt.next();
            exp[2] += delta;
            if (exp[2] > 1.2f) expIt.remove();
        }

        Iterator<Walnut> wIter = activeWalnuts.iterator();
        while (wIter.hasNext()) {
            Walnut w = wIter.next();
            if (w.isActive()) {
                w.update(this, delta);
            } else {
                wIter.remove();
            }
        }
    }

    @Override
    public int calculateWaves(ChapterType chapterType, int level) {
        int waves = super.calculateWaves(chapterType, level);
        return (waves > 0) ? waves : 3;
    }

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;
        timeToSpawn = Math.max(timeToSpawn - 1, 0);

        if (totalTicksPassed % 100 == 0) {
            generateConveyorPlants();
        }

        Iterator<Zombie> z = gameZombies.iterator();
        while (z.hasNext()) {
            Zombie zombie = z.next();

            if (!zombie.isAlive() || zombie.getCurrentHP() <= 0) {
                killAward(this.thisUser);
                addKilledZombieCost(zombie.getWaveNum(), zombie.getCost());
                z.remove();
            } else {
                zombie.update();
            }
        }

        updateZombieTiles();


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
                    int spawnY = getNextRandomY();
                    Position pos = new Position(spawnX, getRealY(spawnY));
                    Zombie newZombie = ZombieFactory.createZombie(nameOfZ, pos);


                    newZombie.setWaveNum(thisWave.getWaveNum());

                    this.gameZombies.add(newZombie);
                    thisWave.addZombieToSpawned(newZombie);
                }
                if (!thisWave.isReadyForNextWave()) break;
            }
        }

        for (Zombie zombie : gameZombies) {
            if (!zombie.isAlive()) continue;

            float zX = (float) zombie.getPosition().getX();
            if (zX <= 390f) {
                System.out.println("The zombie ate your brain; LOSER!!!");
                UsersManager.getInstance().addGamesPlayed();
                this.isPaused = true;
                endGame(false);
                break;
            }
        }

        if (checkingTheEndOfTheGame()) {
            onWin();
            endGame(true);
        }
    }

    @Override
    public void onWin() {
        UsersManager.getInstance().handleMiniGameWin(miniGameType, this.level);
    }

    public ArrayList<BowlingCard> getConveyorBelt() {
        return conveyorBelt;
    }

    public ArrayList<Walnut> getActiveWalnuts() {
        return activeWalnuts;
    }

    public List<float[]> getActiveExplosions() {
        return activeExplosions;
    }
}
