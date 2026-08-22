package com.test1.PlantsVsZombies.src.Model.MiniGames.VasebreakerGame;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.MiniGameType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.*;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.Tile;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class VaseBreaker extends GamePlay {
    private final MiniGameType miniGameType = MiniGameType.VASEBREAKER;
    private ArrayList<Jar> jars = new ArrayList<>();
    private ArrayList<BattlePlant> inventory = new ArrayList<>();

    public VaseBreaker(ChapterType chapterType, int level, int difficulty, User thisUser,
                       ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        super(chapterType, level, difficulty, thisUser, plants, zombies, boosted);
        this.allWaves.clear();
        this.gameZombies.clear();
        this.gamePlants.clear();


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
        System.out.println("The jars were placed. Total: " + jars.size());
    }

    @Override
    public void sunMaker() {
    }

    public BattlePlant breakJar(int x, int y) {
        Jar targetJar = null;
        for (Jar j : jars) {
            if ((int) j.getPosition().getX() == getRealX(x) && (int) j.getPosition().getY() == getRealY(y) && !j.isBroken()) {
                targetJar = j;
                break;
            }
        }

        if (targetJar == null) return null;

        targetJar.setBroken(true);
        System.out.printf("Jar at (%d, %d) broken!\n", x, y);

        Entity content = targetJar.getContent();
        if (content instanceof Zombie) {
            Zombie z = (Zombie) content;
            z.setPosition(new Position(getRealX(x), getRealY(y)));
            z.setRow(y);
            z.setColumn(x);
            gameZombies.add(z);
            System.out.printf("A %s emerged from the jar!\n", z.getName());
            return null;
        } else if (content instanceof BattlePlant) {
            BattlePlant plant = (BattlePlant) content;
            System.out.printf("Plant %s ready to be planted directly!\n", plant.getName());
            return plant;
        }
        return null;
    }

    public boolean plantOnTile(BattlePlant plant, int x, int y) {
        if (plant == null) return false;

        boolean hasJar = jars.stream().anyMatch(j ->
            !j.isBroken() && (int) j.getPosition().getX() == getRealX(x) &&
                (int) j.getPosition().getY() == getRealY(y));

        Tile targetTile = getTileByPosition(x, y);

        if (targetTile != null && targetTile.isArable() && targetTile.getPlants().isEmpty() && !hasJar) {
            plant.setRow(y);
            plant.setColumn(x);
            plant.setPosition(new Position(getRealX(x), getRealY(y)));

            this.gamePlants.add(plant);
            targetTile.addPlant(plant);

            System.out.printf("Planted %s at (%d, %d).\n", plant.getName(), x, y);
            return true;
        }
        return false;
    }

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;

        Iterator<BattlePlant> bp = gamePlants.iterator();
        while (bp.hasNext()) {
            BattlePlant plant = bp.next();

            if (plant.isAlive() && plant.getCurrentHP() > 0) {
                plant.update();
                plant.setCooldown(Math.max(plant.getCooldown() - 1, 0));
            } else {
                Tile currentTile = tiles.stream()
                    .filter(t -> (int) t.getPosition().getX() == plant.getColumn() &&
                        (int) t.getPosition().getY() == plant.getRow())
                    .findFirst()
                    .orElse(null);

                if (currentTile != null) {
                    currentTile.getPlants().remove(plant);
                }
                bp.remove();
            }
        }

        Iterator<Zombie> z = gameZombies.iterator();
        while (z.hasNext()) {
            Zombie zombie = z.next();

            if (!zombie.isAlive() || zombie.getCurrentHP() <= 0) {
                killAward(this.thisUser);
                Position zPos = Position.getRowAndColumn(zombie.getPosition());
                System.out.printf("Zombie %s is dead at (%d, %d)\n", zombie.getName(), (int) zPos.getX(), (int) zPos.getY());
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

        updateZombieTiles();

        if (checkWinCondition()) {
            onWin();
            System.out.println("CONGRATULATIONS! You broke all jars and defeated all zombies!");
            this.isPaused = true;
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
