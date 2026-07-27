package src.Model.MiniGames.IZombieGame;

import src.Enums.ChapterType;
import src.Enums.PlantType;
import src.Model.GamePlayType.GamePlay;
import src.Model.PlantsAndZombies.*;
import src.Model.Tile;
import src.Model.User.User;
import java.util.*;

public class IZombie extends GamePlay {
    private final int RED_LINE_X = 5;
    private ArrayList<SunZombie> sunZombies;
    private boolean[] brainsEaten;
    private Map<String, Integer> availableZombies;

    public IZombie(ChapterType chapterType, int level, int difficulty, User thisUser,
                   ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        super(chapterType, level, difficulty, thisUser, plants, zombies, boosted);

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

        setPlants();
        initSunZombies();
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
                if (random.nextBoolean()) {
                    String randomPlantName = plantTypes[random.nextInt(plantTypes.length)];
                    Position pos = new Position(x, y);

                    BattlePlant plant = PlantFactory.createBattlePlant(randomPlantName, 1, pos);
                    plant.setColumn(x);
                    plant.setRow(y);

                    this.gamePlants.add(plant);

                    Tile tile = getTileByPosition(x, y);
                    if (tile != null) tile.addPlant(plant);
                }
            }
        }
        System.out.println("Random plants were planted using PlantFactory.");
    }

    private void initSunZombies() {
        for (int y = 1; y <= 5; y++) {
            Position pos = new Position(9, y);
            SunZombie sz = new SunZombie(pos);
            sunZombies.add(sz);

            this.gameZombies.add(sz);
        }
    }

    public void placeZombie(String zombieName, int x, int y) {
        if (x <= RED_LINE_X) {
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
        Position pos = new Position(x, y);

        Zombie newZombie = ZombieFactory.createZombie(zombieName, pos);
        this.gameZombies.add(newZombie);

        System.out.printf("Placed %s at (%d, %d) for %d suns.\n", zombieName, x, y, cost);
    }

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;

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
                zIter.remove();
                continue;
            }

            z.update();

            int row = (int) z.getPosition().getY();
            if (z.getPosition().getX() <= 1.0) {
                if (!brainsEaten[row - 1]) {
                    brainsEaten[row - 1] = true;
                    System.out.printf("A zombie ATE THE BRAIN in row %d! 🧠\n", row);
                }
            }
        }

        Iterator<BattlePlant> pIter = gamePlants.iterator();
        while (pIter.hasNext()) {
            BattlePlant plant = pIter.next();
            if (plant.isAlive() && plant.getCurrentHP() > 0) {
                plant.update();
            } else {
                Tile tile = getTileByPosition(plant.getColumn(), plant.getRow());
                if (tile != null) tile.removePlant();
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
            System.out.println("VICTORY! You ate all 5 brains and defeated the plants!");
            Pause();
            return;
        }

        int minCost = Collections.min(availableZombies.values());

        if (mySuns < minCost && gameZombies.isEmpty()) {
            System.out.println("GAME OVER! You ran out of suns and zombies before eating all brains.");
            Pause();
        }
    }

    @Override
    public void showMap() {
        System.out.println("=== I, ZOMBIE BOARD ===");
        System.out.println("Suns: " + mySuns);
        System.out.print("Brains status: ");
        for (int i = 0; i < 5; i++) {
            System.out.printf("[Row %d: %s] ", i + 1, brainsEaten[i] ? "EATEN" : "OK");
        }
        System.out.println("\n------------------------------------------------");
        for (int y = 1; y <= 5; y++) {
            System.out.printf("Row %d: ", y);
            for (int x = 1; x <= 9; x++) {
                if (x == RED_LINE_X) System.out.print("|RED LINE| ");

                Tile t = getTileByPosition(x, y);
                boolean hasPlant = t != null && !t.getPlants().isEmpty();
                boolean hasZombie = t != null && !t.getZombies().isEmpty();

                char p = hasPlant ? 'P' : ' ';
                char z = hasZombie ? 'Z' : ' ';
                System.out.printf("[%c%c] ", p, z);
            }
            System.out.println();
        }
        System.out.println("------------------------------------------------");
    }

    public void showPlantStatus() {
        System.out.println("=== Available Zombies to Buy ===");
        for (Map.Entry<String, Integer> entry : availableZombies.entrySet()) {
            System.out.printf("- %s : %d Suns\n", entry.getKey(), entry.getValue());
        }
    }
}