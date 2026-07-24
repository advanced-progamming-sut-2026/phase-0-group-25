package src.Model.MiniGames.VasebreakerGame;

import src.Enums.ChapterType;
import src.Model.GamePlayType.GamePlay;
import src.Model.Mower;
import src.Model.PlantsAndZombies.*;
import src.Model.PlantsAndZombies.Projectiles.Projectile;
import src.Model.Tile;
import src.Model.User.User;

import java.util.ArrayList;
import java.util.Iterator;

public class VaseBreaker extends GamePlay {

    private ArrayList<Jar> jars = new ArrayList<>();
    private ArrayList<DroppedSeedPacket> droppedSeedPackets = new ArrayList<>();
    private ArrayList<BattlePlant> inventory = new ArrayList<>();

    public VaseBreaker(ChapterType chapterType, int level, int difficulty, User thisUser,
                       ArrayList<String> availablePlants, ArrayList<String> availableZombies) {
        super(chapterType, level, difficulty, thisUser, availablePlants, availableZombies);
        setupJars();
    }

    private void setupJars() {
        for (int y = 1; y <= 5; y++) {
            for (int x = 5; x <= 9; x++) {
                Position pos = new Position(x, y);

                if (x == 9 && y == 3) {
                    Zombie gargantuar = ZombieFactory.create("Gargantuar", pos);
                    jars.add(new GargantuarJar(pos, gargantuar));
                } else if ((x + y) % 3 == 0) {
                    BattlePlant plant = PlantFactory.create("Peashooter");
                    jars.add(new PlantJar(pos, plant));
                } else {
                    jars.add(new SimpleJar(pos, content));
                }
            }
        }
    }

    @Override
    public void sunMaker() {
        // خورشید از آسمان نمی‌افتد؛ این متد خالی می‌ماند.
    }

    public void breakJar(int x, int y) {
        Jar targetJar = null;
        for (Jar j : jars) {
            if ((int) j.getPosition().getX() == x && (int) j.getPosition().getY() == y && !j.isBroken()) {
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
            z.setPosition(new Position(x, y));
            gameZombies.add(z);
            System.out.printf("A %s emerged from the jar at (%d, %d)!\n", z.getName(), x, y);
        } else if (content instanceof BattlePlant) {
            BattlePlant plant = (BattlePlant) content;

            droppedSeedPackets.add(new DroppedSeedPacket(plant, new Position(x, y), 100));
            System.out.printf("A Seed Packet for %s dropped on ground at (%d, %d)!\n", plant.getName(), x, y);
        } else {
            System.out.println("The jar was empty!");
        }
    }

    public void collectSeedPacket(int x, int y) {
        DroppedSeedPacket targetPacket = null;
        for (DroppedSeedPacket sp : droppedSeedPackets) {
            if ((int) sp.getPosition().getX() == x && (int) sp.getPosition().getY() == y) {
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

    public void plantFromInventory(int inventoryIndex, Position targetPosition) {
        if (inventoryIndex < 0 || inventoryIndex >= inventory.size()) {
            System.out.println("Invalid seed packet index!");
            return;
        }

        Tile targetTile = tiles.stream()
                .filter(t -> t.getPosition().equals(targetPosition))
                .findFirst()
                .orElse(null);

        if (targetTile != null && targetTile.isArable() && targetTile.getPlants().isEmpty()) {
            BattlePlant plantToPlant = inventory.remove(inventoryIndex);
            plantToPlant.setRow((int) targetPosition.getY());
            plantToPlant.setColumn((int) targetPosition.getX());

            this.gamePlants.add(plantToPlant);
            targetTile.addPlant(plantToPlant);

            System.out.printf("Planted %s at (%d, %d) from Seed Packet.\n",
                    plantToPlant.getName(), (int) targetPosition.getX(), (int) targetPosition.getY());
        } else {
            System.out.println("Cannot plant at this tile!");
        }
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
                glowingAward(this);
                z.remove();
            } else {
                zombie.update();
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

        int xLimit = mowers.get(0).getX();
        for (Zombie zombie : gameZombies) {
            int yOfz = (int) zombie.getPosition().getY();
            int xOfz = (int) zombie.getPosition().getX();
            Mower thisMower = mowers.stream().filter(p -> p.getY() == yOfz).findFirst().orElse(null);

            if (thisMower != null && xOfz <= xLimit) {
                if (!thisMower.isUsed()) {
                    System.out.println("Lawn mower at row " + yOfz + " triggered!");
                    thisMower.killZombies(this);
                } else {
                    System.out.println("Zombie reached your house! YOU LOST!!!");
                    this.isPaused = true;
                    return;
                }
            }
        }

        if (checkWinCondition()) {
            System.out.println("CONGRATULATIONS! You broke all jars and defeated all zombies!");
            this.isPaused = true;
        }
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