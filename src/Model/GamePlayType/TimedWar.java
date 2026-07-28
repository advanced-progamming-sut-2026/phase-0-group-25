package src.Model.GamePlayType;

import src.Enums.ChapterType;
import src.Model.Mower;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Position;
import src.Model.PlantsAndZombies.Projectiles.Dynamite;
import src.Model.PlantsAndZombies.Projectiles.Projectile;
import src.Model.PlantsAndZombies.Zombie;
import src.Model.PlantsAndZombies.ZombieFactory;
import src.Model.Tile;
import src.Model.User.User;
import src.Model.Wave.FinalWave;
import src.Model.Wave.Wave;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class TimedWar extends GamePlay {
    private int numOfDeadZombies = 0;

    public TimedWar(ChapterType chapterType, int level, int difficulty, User thisUser,
                    ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        super(chapterType, level, difficulty, thisUser, plants, zombies, boosted);
    }

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;
        timeToSpawn--;

        if (this.chapterType != ChapterType.DARK_AGE) {
            sunMaker();
        }
        applyIcyWind();

        checkingSunMakers();

        // Updating Zombies, Plant and Projectile and Dynamite (Deleting them if they're dead) :
        Iterator<BattlePlant> bp = gamePlants.iterator();
        while (bp.hasNext()) {
            BattlePlant plant = bp.next();

            if (plant.isAlive() && plant.getCurrentHP() > 0) {
                plant.update();
                // passing cooldown
                plant.setCooldown(Math.max(plant.getCooldown() - 1, 0));
            } else {
                Tile currentTile = getTileByPosition(plant.getColumn(), plant.getRow());

                if (currentTile != null) {
                    System.out.printf("Plant %s at (%d, %d) is destroyed.\n", plant.getName(), plant.getColumn(), plant.getRow());
                    currentTile.getPlants().removeIf(p -> p.getName().equals(plant.getName()));
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
                numOfDeadZombies += 1;
                Position zPos = Position.getRowAndColumn(zombie.getPosition());
                System.out.printf("Zombie of type %s is dead at (%d, %d)\n",
                        zombie.getName(), (int) zPos.getX(), (int) zPos.getY());
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
        Iterator<Dynamite> dy = dynamites.iterator();
        while (dy.hasNext()) {
            Dynamite thisDynamite = dy.next();

            thisDynamite.update();
        }

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
                        positionOfZ = new Position(spawnX + 200, getRealY(spawnY));
                    } else {
                        positionOfZ = new Position(spawnX, getRealY(spawnY));
                    }
                    Zombie newZombie = ZombieFactory.createZombie(nameOfZ, positionOfZ);
                    System.out.printf("Zombie %s spawned at wave %d in lane %d which costed %d.\n",
                            nameOfZ, thisWave.getWaveNum(), spawnY, newZombie.getCost());
                    this.gameZombies.add(newZombie);
                }
                if (!thisWave.isReadyForNextWave()) {
                    break;
                }
            }
        }

        // Checking if the end of the game (Losing) + Activate Mowers :
        int x = mowers.get(0).getX();
        for (Zombie zombie : gameZombies) {
            int yOfz = (int) zombie.getPosition().getY();
            int xOfz = (int) zombie.getPosition().getX();
            Mower thisMower = mowers.stream().filter(p -> p.getY() == yOfz).findFirst().get();

            if (xOfz <= x) {
                if (!thisMower.isUsed()) {
                    System.out.println("The lawn mower in the row" + yOfz + "is triggered and killed these zombies:");
                    thisMower.killZombies(this);
                } else {
                    System.out.println("The zombie ate your brain; LOSER!!!");
                    this.isPaused = true;
                }
            }
        }

        // Another condition for losing (in this game) :
        if (totalTicksPassed >= 150 && numOfDeadZombies < 10) {
            System.out.println("You must kill at least 10 zombies within 15 seconds!!");
            System.out.println("The zombie ate your brain; LOSER!!!");
            this.isPaused = true;
        }

        // Checking if the end of the game (Winning) :
        if (checkingTheEndOfTheGame()) {
            getLevelObject().completeLevel();
            System.out.println("Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz.");
            this.isPaused = true;
        }
    }
}
