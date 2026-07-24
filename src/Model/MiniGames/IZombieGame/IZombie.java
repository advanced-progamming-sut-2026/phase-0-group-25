package src.Model.MiniGames.IZombieGame;

import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Position;
import src.Model.PlantsAndZombies.Zombie;
import src.Model.PlayGroundType.PlayGround;
import src.Model.Tile;

import java.util.*;

public class IZombie {
    private final int RED_LINE_X = 5;
    private int mySuns;
    private PlayGround playGround;
    private ArrayList<Zombie> myZombies;
    private ArrayList<SunZombie> sunZombies;
    private ArrayList<BattlePlant> fieldPlants;
    private ArrayList<Tile> tiles;
    private boolean[] brainsEaten;
    private Map<String, Integer> availableZombies;
    private int totalTicksPassed = 0;
    private boolean isPaused = false;
    private Random random = new Random();

    public IZombie() {
        this.mySuns = 150;
        this.myZombies = new ArrayList<>();
        this.sunZombies = new ArrayList<>();
        this.fieldPlants = new ArrayList<>();
        this.tiles = new ArrayList<>();
        this.brainsEaten = new boolean[5];

        this.availableZombies = new LinkedHashMap<>();
        availableZombies.put("RegularZombie", 50);
        availableZombies.put("ConeheadZombie", 75);
        availableZombies.put("PoleVaultingZombie", 100);
        availableZombies.put("BucketheadZombie", 125);
        availableZombies.put("NewspaperZombie", 100);

        initTiles();
        setPlants();
        initSunZombies();
    }

    private void initTiles() {
        for (int y = 1; y <= 5; y++) {
            for (int x = 1; x <= 9; x++) {
                tiles.add(new Tile(new Position(x, y), true));
            }
        }
    }

    public void setPlants() {
        String[] plantTypes = {"Peashooter", "Sunflower", "WallNut", "SnowPea"};
        for (int y = 1; y <= 5; y++) {
            for (int x = 1; x <= 4; x++) {
                if (random.nextBoolean()) {
                    String randomPlant = plantTypes[random.nextInt(plantTypes.length)];
                    BattlePlant plant = new BattlePlant(randomPlant, new Position(x, y), 300);
                    plant.setColumn(x);
                    plant.setRow(y);
                    fieldPlants.add(plant);

                    Tile tile = getTileAt(x, y);
                    if (tile != null) tile.addPlant(plant);
                }
            }
        }
        System.out.println("Random plants were planted on the left side of the red line.");
    }

    private void initSunZombies() {
        for (int y = 1; y <= 5; y++) {
            Position pos = new Position(9, y);
            SunZombie sz = new SunZombie(pos);
            sunZombies.add(sz);
            myZombies.add(sz);
        }
    }


    public void placeZombie(String zombieName, int x, int y) {
        if (x <= RED_LINE_X) {
            System.out.println("Invalid position! You must place zombies behind the RED LINE (Column > 5).");
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
        Zombie newZombie = new Zombie(zombieName, pos, 200, 10, 0.2);
        myZombies.add(newZombie);

        System.out.printf("Placed %s at (%d, %d) for %d suns.\n", zombieName, x, y, cost);
    }


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

        Iterator<Zombie> zIter = myZombies.iterator();
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

        Iterator<BattlePlant> pIter = fieldPlants.iterator();
        while (pIter.hasNext()) {
            BattlePlant plant = pIter.next();
            if (plant.isAlive() && plant.getCurrentHP() > 0) {
                plant.update();
            } else {
                Tile tile = getTileAt(plant.getColumn(), plant.getRow());
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
            finishGame();
            return;
        }

        int minCost = Collections.min(availableZombies.values());

        if (mySuns < minCost && myZombies.isEmpty()) {
            System.out.println("GAME OVER! You ran out of suns and zombies before eating all brains.");
            finishGame();
        }
    }

    public void advanceTime(int ticks) {
        for (int i = 0; i < ticks; i++) {
            update();
        }
    }

    public void finishGame() {
        this.isPaused = true;
        System.out.println("Game Finished.");
    }

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

                Tile t = getTileAt(x, y);
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

    public void showTileStatus(int x, int y) {
        Tile tile = getTileAt(x, y);
        if (tile != null) {
            System.out.printf("Tile (%d, %d) -> Plants: %d, Zombies: %d\n",
                    x, y, tile.getPlants().size(), tile.getZombies().size());
        }
    }

    public void showPlantStatus() {
        System.out.println("=== Available Zombies to Buy ===");
        for (Map.Entry<String, Integer> entry : availableZombies.entrySet()) {
            System.out.printf("- %s : %d Suns\n", entry.getKey(), entry.getValue());
        }
    }

    // --- Cheat Functions ---
    public void cheatAddSun(int amount) {
        this.mySuns += amount;
        System.out.println("Cheat Activated: Added " + amount + " suns. Total: " + mySuns);
    }

    public void removeCooldown() {
        System.out.println("Cooldowns removed (Not applicable in I, Zombie mode).");
    }

    private Tile getTileAt(int x, int y) {
        return tiles.stream()
                .filter(t -> (int) t.getPosition().getX() == x && (int) t.getPosition().getY() == y)
                .findFirst()
                .orElse(null);
    }
}