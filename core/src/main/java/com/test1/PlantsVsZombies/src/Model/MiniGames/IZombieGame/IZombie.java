package com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.MiniGameType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.*;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.Tile;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;

import java.util.*;

public class IZombie extends GamePlay {
    private final int RED_LINE_X = 5;
    private final MiniGameType miniGameType = MiniGameType.I_ZOMBIE;
    private boolean isSeted = false;
    private ArrayList<SunZombie> sunZombies;
    private boolean[] brainsEaten;
    private Map<String, Integer> availableZombies;

    public IZombie(ChapterType chapterType, int level, int difficulty, User thisUser,
                   ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        super(chapterType, level, difficulty, thisUser, plants, zombies, boosted);
        setLevelObjectives("Reach the house with your zombies");

        this.allWaves.clear();
        this.gameZombies.clear();
        this.mowers.clear();

        this.mySuns = 150;
        this.sunZombies = new ArrayList<>();
        this.brainsEaten = new boolean[5];

        this.availableZombies = new LinkedHashMap<>();
        availableZombies.put("DEFAULT", 50);
        availableZombies.put("CONE_HEAD", 75);
        availableZombies.put("BUCKET_HEAD", 100);
        availableZombies.put("KNIGHT", 125);
        availableZombies.put("NEWSPAPER", 100);
    }

    public void setPlants() {
        String[] plantTypes = {
            PlantType.PEASHOOTER.getName(),
            PlantType.SUNFLOWER.getName(),
            PlantType.WALL_NUT.getName(),
            PlantType.SNOW_PEA.getName()
        };

        for (int y = 1; y <= 5; y++) {
            for (int x = 1; x <= 4; x++) {
                String randomPlantName = plantTypes[random.nextInt(plantTypes.length)];
                Position pos = new Position(getRealX(x), getRealY(y));

                BattlePlant plant = PlantFactory.createBattlePlant(randomPlantName, 1, pos);
                plant.setColumn(x);
                plant.setRow(y);

                this.gamePlants.add(plant);
                Tile tile = getTileByPosition(x, y);
                tile.addPlant(plant);
            }
        }
        System.out.println("Random plants were planted.");
    }

    private void initSunZombies() {
        for (int y = 1; y <= 5; y++) {
            Position pos = new Position(1800, getRealY(y));
            SunZombie sz = new SunZombie(pos);
            sunZombies.add(sz);

            this.gameZombies.add(sz);
        }
    }

    public void placeZombie(String zombieName, int x, int y) {
        if (x < RED_LINE_X) {
            System.out.println("Invalid position! Place behind the RED LINE.");
            return;
        }

        if (!availableZombies.containsKey(zombieName)) {
            System.out.println("This zombie type is not available in this stage!");
            return;
        }

        int cost = availableZombies.get(zombieName);
        if (mySuns < cost) {
            System.out.println("Not enough sun! You need " + cost + " suns.");
            return;
        }

        mySuns -= cost;
        Position pos = new Position(getRealX(x), getRealY(y));

        Zombie newZombie = ZombieFactory.createZombie(zombieName, pos);
        this.gameZombies.add(newZombie);

        System.out.printf("Placed %s at (%d, %d) for %d suns.\n", zombieName, x, y, cost);
    }

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;

        if (!isSeted) {
            setPlants();
            initSunZombies();
            isSeted = true;
        }

        Iterator<SunZombie> szIter = sunZombies.iterator();
        while (szIter.hasNext()) {
            SunZombie sz = szIter.next();
            if (sz.isAlive() && sz.getCurrentHP() > 0) {
                int generated = sz.generateSun(totalTicksPassed);
                if (generated > 0) {
                    mySuns += generated;
                    System.out.printf("SunZombie at row %d generated %d suns! Total Suns: %d\n",
                        (int) sz.getPosition().getY(), generated, mySuns);
                }
            } else {
                szIter.remove();
            }
        }

        Iterator<Zombie> zIter = gameZombies.iterator();
        while (zIter.hasNext()) {
            Zombie z = zIter.next();
            if (!z.isAlive() || z.getCurrentHP() <= 0) {
                Position zPos = Position.getRowAndColumn(z.getPosition());
                System.out.printf("Zombie of type %s is dead at (%d, %d)\n",
                    z.getName(), (int) zPos.getX(), (int) zPos.getY());
                zIter.remove();
                continue;
            }

            z.update();

            int row = (int) Position.getRowAndColumn(z.getPosition()).getY();
            if (z.getPosition().getX() <= 20) {
                if (!brainsEaten[row - 1]) {
                    brainsEaten[row - 1] = true;
                    zIter.remove();
                    System.out.printf("A zombie ATE THE BRAIN in row %d! 🧠\n", row);
                }
            }
        }

        updateZombieTiles();

        Iterator<Projectile> pj = projectiles.iterator();
        while (pj.hasNext()) {
            Projectile thisProjectile = pj.next();

            if (thisProjectile.isActive()) {
                thisProjectile.update();
            } else {
                pj.remove();
            }
        }

        Iterator<BattlePlant> pIter = gamePlants.iterator();
        while (pIter.hasNext()) {
            BattlePlant plant = pIter.next();
            boolean isSunFlower = plant.getPlantStats().getAbilities().contains("producing sun");
            if (plant.isAlive() && plant.getCurrentHP() > 0) {
                if (!isSunFlower) {
                    plant.update();
                }
            } else {
                Tile tile = getTileByPosition(plant.getColumn(), plant.getRow());
                if (tile != null) tile.removePlant();
                System.out.printf("Plant %s at (%d, %d) is destroyed.\n", plant.getName(), plant.getColumn(), plant.getRow());
                pIter.remove();
            }
        }

        checkGameStatus();
    }

    private void checkGameStatus() {
        boolean allBrainsEaten = true;
        for (boolean eaten : brainsEaten) {
            if (!eaten) {
                allBrainsEaten = false;
                break;
            }
        }

        if (allBrainsEaten) {
            onWin();
            System.out.println("VICTORY! You ate all 5 brains and defeated the plants!");
            endGame(true);
            return;
        }

        int minCost = 50;

        if (mySuns < minCost && gameZombies.isEmpty()) {
            System.out.println("GAME OVER! You ran out of suns and zombies before eating all brains.");
            endGame(false);
        }
    }

    @Override
    public void onWin() {
        UsersManager.getInstance().handleMiniGameWin(miniGameType, this.level);
    }
}
