package src.Model.MiniGames.VasebreakerGame;

import src.Enums.ChapterType;
import src.Enums.MiniGameType;
import src.Enums.PlantType;
import src.Model.GamePlayType.GamePlay;
import src.Model.Mower;
import src.Model.PlantsAndZombies.*;
import src.Model.PlantsAndZombies.Projectiles.Projectile;
import src.Model.Tile;
import src.Model.User.User;
import src.Model.User.UsersManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class VaseBreaker extends GamePlay {
    private final MiniGameType miniGameType = MiniGameType.VASEBREAKER;  // identify this game
    private boolean isSeted = false;
    private ArrayList<Jar> jars = new ArrayList<>();
    private ArrayList<DroppedSeedPacket> droppedSeedPackets = new ArrayList<>();
    private ArrayList<BattlePlant> inventory = new ArrayList<>();

    public VaseBreaker(ChapterType chapterType, int level, int difficulty, User thisUser,
                       ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        super(chapterType, level, difficulty, thisUser, plants, zombies, boosted);
        this.allWaves.clear();
        this.gameZombies.clear();
        this.gamePlants.clear();
    }

    private void setupJars() {
        for (int y = 1; y <= 5; y++) {
            for (int x = 5; x <= 9; x++) {
                Position pos = new Position(getRealX(x), getRealY(y));

                if (x == 9 && y == 3) {
                    Zombie gargantuar = ZombieFactory.createZombie("GARGANTUAR", pos);
                    jars.add(new GargantuarJar(pos, gargantuar));
                } else if ((x + y) % 3 == 0) {
                    BattlePlant plant = PlantFactory.createBattlePlant(PlantType.PEASHOOTER.getName(), 1, pos);
                    jars.add(new PlantJar(pos, plant));
                } else {
                    Zombie basicZombie = ZombieFactory.createZombie("DEFAULT", pos);
                    jars.add(new SimpleJar(pos, basicZombie));
                }
            }
        }
        System.out.println("The jars were placed.");
    }

    @Override
    public void sunMaker() {
    }

    public void breakJar(int x, int y) {
        Jar targetJar = null;
        for (Jar j : jars) {
            if ((int) j.getPosition().getX() == getRealX(x) && (int) j.getPosition().getY() == getRealY(y) && !j.isBroken()) {
                targetJar = j;
                break;
            }
        }

        if (targetJar == null) {
            System.out.println("No unbroken jar found at position (" + x + ", " + y + ")!");
            return;
        }

        targetJar.setBroken(true);
        System.out.printf("Jar at (%d, %d) broken!\n", x, y);

        Entity content = targetJar.getContent();
        if (content instanceof Zombie) {
            Zombie z = (Zombie) content;
            z.setPosition(new Position(getRealX(x), getRealY(y)));
            gameZombies.add(z);
            System.out.printf("A %s emerged from the jar at (%d, %d)!\n", z.getName(), x, y);
        } else if (content instanceof BattlePlant) {
            BattlePlant plant = (BattlePlant) content;

            droppedSeedPackets.add(
                    new DroppedSeedPacket(plant, new Position(getRealX(x), getRealY(y)), 100));
            System.out.printf("A Seed Packet for %s dropped on ground at (%d, %d)!\n", plant.getName(), x, y);
        } else {
            System.out.println("The jar was empty!");
        }
    }

    public void collectSeedPacket(int x, int y) {
        DroppedSeedPacket targetPacket = null;
        for (DroppedSeedPacket sp : droppedSeedPackets) {
            if ((int) sp.getPosition().getX() == getRealX(x) && (int) sp.getPosition().getY() == getRealY(y)) {
                targetPacket = sp;
                break;
            }
        }

        if (targetPacket != null) {
            inventory.add(targetPacket.getPlant());
            droppedSeedPackets.remove(targetPacket);
            System.out.printf("Collected %s Seed Packet!\n", targetPacket.getPlant().getName());
        } else {
            System.out.println("No Seed Packet found on the ground at this position.");
        }
    }

    public void plantFromInventory(int inventoryIndex, int x, int y) {
        if (inventoryIndex < 0 || inventoryIndex >= inventory.size()) {
            System.out.println("Invalid seed packet index!");
            return;
        }

        Tile targetTile = getTileByPosition(x, y);

        if (targetTile != null && targetTile.isArable() && targetTile.getPlants().isEmpty()) {
            BattlePlant plantToPlant = inventory.remove(inventoryIndex);
            plantToPlant.setRow(y);
            plantToPlant.setColumn(x);
            plantToPlant.setPosition(new Position(getRealX(x), getRealY(y)));

            this.gamePlants.add(plantToPlant);
            targetTile.addPlant(plantToPlant);

            System.out.printf("Planted %s at (%d, %d) from Seed Packet.\n",
                    plantToPlant.getName(), x, y);
        } else {
            System.out.println("Cannot plant at this tile!");
        }
    }

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;

        if (!isSeted) {
            setupJars();
            isSeted = true;
        }

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
                glowingAward(this);
                Position zPos = Position.getRowAndColumn(zombie.getPosition());
                System.out.printf("Zombie of type %s is dead at (%d, %d)\n",
                        zombie.getName(), (int) zPos.getX(), (int) zPos.getY());
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

        Iterator<DroppedSeedPacket> spIter = droppedSeedPackets.iterator();
        while (spIter.hasNext()) {
            DroppedSeedPacket sp = spIter.next();
            sp.update();
            if (sp.isExpired()) {
                System.out.printf("Seed Packet for %s at (%d, %d) disappeared!\n",
                        sp.getPlant().getName(), (int) sp.getPosition().getX(), (int) sp.getPosition().getY());
                spIter.remove();
            }
        }

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

        z = gameZombies.iterator();
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
        boolean allJarsBroken = jars.stream().allMatch(Jar::isBroken);
        return allJarsBroken && gameZombies.isEmpty();
    }

    public ArrayList<Jar> getJars() {
        return jars;
    }

    public ArrayList<BattlePlant> getInventory() {
        return inventory;
    }

    public ArrayList<DroppedSeedPacket> getDroppedSeedPackets() {
        return droppedSeedPackets;
    }
}