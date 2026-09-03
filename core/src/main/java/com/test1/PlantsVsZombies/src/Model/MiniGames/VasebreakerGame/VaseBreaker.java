package com.test1.PlantsVsZombies.src.Model.MiniGames.VasebreakerGame;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.MiniGameType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.*;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.Tile;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.ScreenShake;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class VaseBreaker extends GamePlay {
    private final MiniGameType miniGameType = MiniGameType.VASEBREAKER;
    private final ArrayList<Jar> jars = new ArrayList<>();
    private final ArrayList<BattlePlant> inventory = new ArrayList<>();

    public VaseBreaker(ChapterType chapterType, int level, int difficulty, User thisUser,
                       ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        super(chapterType, level, difficulty, thisUser, plants, zombies, boosted);
        activeInstance = this;
        this.allWaves.clear();
        this.gameZombies.clear();
        this.gamePlants.clear();
        setLevelObjectives("Break all jars and defeat all zombies to win.");
        this.mySuns = 0;

        setupJars();
    }

    private void setupJars() {
        jars.clear();
        String[] possiblePlants = {"PEASHOOTER", "REPEATER", "WALL_NUT", "BONK_CHOY", "SNOW_PEA"};
        String[] possibleZombies = {"DEFAULT", "CONE_HEAD", "BUCKET_HEAD"};

        for (int y = 1; y <= 5; y++) {
            for (int x = 5; x <= 9; x++) {
                Position pos = new Position(getRealX(x), getRealY(y));

                if (x == 9 && y == 3) {
                    Zombie gargantuar = ZombieFactory.createZombie("GARGANTUAR", pos);
                    jars.add(new GargantuarJar(pos, gargantuar));
                } else if ((x + y) % 4 == 0) {
                    String pName = possiblePlants[random.nextInt(possiblePlants.length)];
                    BattlePlant plant = PlantFactory.createBattlePlant(pName, 1, pos);
                    jars.add(new PlantJar(pos, plant));
                } else {
                    if (Math.random() < 0.5) {
                        String pName = possiblePlants[random.nextInt(possiblePlants.length)];
                        BattlePlant plant = PlantFactory.createBattlePlant(pName, 1, pos);
                        jars.add(new SimpleJar(pos, plant));
                    } else {
                        String zName = possibleZombies[random.nextInt(possibleZombies.length)];
                        Zombie basicZombie = ZombieFactory.createZombie(zName, pos);
                        jars.add(new SimpleJar(pos, basicZombie));
                    }
                }
            }
        }
    }

    @Override
    public void sunMaker() {
    }

    public BattlePlant breakJar(int x, int y) {
        Jar targetJar = null;
        float targetX = getRealX(x);
        float targetY = getRealY(y);

        for (Jar j : jars) {
            if (!j.isBroken() && Math.abs(j.getPosition().getX() - targetX) < 20 && Math.abs(j.getPosition().getY() - targetY) < 20) {
                targetJar = j;
                break;
            }
        }

        if (targetJar == null) return null;

        targetJar.setBroken(true);

        if (targetJar instanceof GargantuarJar) {
            ScreenShake.shake(0.45f, 16f);
        } else {
            ScreenShake.shake(0.12f, 5f);
        }

        Entity content = targetJar.getContent();
        if (content instanceof Zombie z) {
            z.setPosition(new Position(targetX, targetY));
            z.setRow(y);
            z.setColumn(x);
            gameZombies.add(z);
            updateZombieTiles();
            return null;
        } else if (content instanceof BattlePlant plant) {
            return plant;
        }
        return null;
    }

    public boolean plantOnTile(BattlePlant plant, int x, int y) {
        if (plant == null) return false;

        float targetX = getRealX(x);
        float targetY = getRealY(y);

        boolean hasJar = jars.stream().anyMatch(j ->
            !j.isBroken() && Math.abs(j.getPosition().getX() - targetX) < 20 && Math.abs(j.getPosition().getY() - targetY) < 20);

        Tile targetTile = getTileByPosition(x, y);

        if (targetTile != null && targetTile.isArable() && targetTile.getPlants().isEmpty() && !hasJar) {
            plant.setRow(y);
            plant.setColumn(x);
            plant.setPosition(new Position(targetX, targetY));

            this.gamePlants.add(plant);
            targetTile.addPlant(plant);
            return true;
        }
        return false;
    }

    @Override
    public void update() {
        if (isPaused) return;
        activeInstance = this;
        totalTicksPassed++;


        updateZombieTiles();


        Iterator<BattlePlant> bp = gamePlants.iterator();
        while (bp.hasNext()) {
            BattlePlant plant = bp.next();

            if (plant.isAlive() && plant.getCurrentHP() > 0) {
                plant.update();
                plant.setCooldown(Math.max(plant.getCooldown() - 1, 0));
            } else {
                Tile currentTile = getTileByPosition(plant.getColumn(), plant.getRow());
                if (currentTile != null) {
                    currentTile.getPlants().removeIf(p -> p.getName().equals(plant.getName()));
                }
                incrementLostPlants();
                bp.remove();
            }
        }


        Iterator<Zombie> z = gameZombies.iterator();
        while (z.hasNext()) {
            Zombie zombie = z.next();

            if (!zombie.isAlive() || zombie.getCurrentHP() <= 0) {
                killAward(this.thisUser);
                z.remove();
            } else {
                zombie.update();
            }
        }


        Iterator<Projectile> pj = projectiles.iterator();
        while (pj.hasNext()) {
            Projectile thisProjectile = pj.next();

            if (thisProjectile.isActive()) {
                thisProjectile.update();
            } else {
                pj.remove();
            }
        }


        for (Zombie zombie : gameZombies) {
            if (!zombie.isAlive()) continue;

            float zX = (float) zombie.getPosition().getX();
            if (zX <= 390f) {
                System.out.println("The zombie ate your brain; LOSER!!!");
                UsersManager.getInstance().addGamesPlayed();
                endGame(false);
                return;
            }
        }


        if (checkWinCondition()) {
            onWin();
            System.out.println("CONGRATULATIONS! You broke all jars and defeated all zombies!");
            endGame(true);
        }
    }

    @Override
    public void onWin() {
        UsersManager.getInstance().handleMiniGameWin(miniGameType, this.level);
    }

    private boolean checkWinCondition() {
        if (jars.isEmpty()) return false;
        boolean allJarsBroken = jars.stream().allMatch(Jar::isBroken);
        return allJarsBroken && gameZombies.isEmpty();
    }

    public ArrayList<Jar> getJars() {
        return jars;
    }

    public ArrayList<BattlePlant> getInventory() {
        return inventory;
    }
}
