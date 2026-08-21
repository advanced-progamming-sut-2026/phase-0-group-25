package com.test1.PlantsVsZombies.src.Model.GamePlayType;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Model.Mower;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.*;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Dynamite;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.Tile;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.Wave.FinalWave;
import com.test1.PlantsVsZombies.src.Model.Wave.Wave;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class Simple extends GamePlay {

    public Simple(ChapterType chapterType, int level, int difficulty, User thisUser,
                  ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        super(chapterType, level, difficulty, thisUser, plants, zombies, boosted);
    }

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;
        timeToSpawn = Math.max(timeToSpawn - 1, 0);

        if (!settedThePlants) {
            if (chapterType == ChapterType.FROSTBITE_CAVES) {
                String thisPName1 = plants.get(0).getName();
                String thisPName2 = plants.get(1).getName();
                Position position1 = new Position(2, 2);
                Position position2 = new Position(3, 5);
                BattlePlant thisP1 = PlantFactory.createBattlePlant(thisPName1, getLevelOfPlant(thisPName1), position1);
                BattlePlant thisP2 = PlantFactory.createBattlePlant(thisPName2, getLevelOfPlant(thisPName2), position2);
                this.planting(thisP1, position1);
                this.planting(thisP2, position2);

                for (BattlePlant p : this.gamePlants) {
                    p.setFrozen(true);
                }
            }
            this.settedThePlants = true;

            planting(PlantFactory.createBattlePlant(PlantType.GOLD_BLOOM.getName(), 1,
                new Position(1, 2)), new Position(4, 5));
            planting(PlantFactory.createBattlePlant(PlantType.HYPNO_SHROOM.getName(), 1,
                new Position(1, 2)), new Position(7, 5));
            planting(PlantFactory.createBattlePlant(PlantType.HYPNO_SHROOM.getName(), 1,
                new Position(1, 2)), new Position(7, 4));
            planting(PlantFactory.createBattlePlant(PlantType.HYPNO_SHROOM.getName(), 1,
                new Position(1, 2)), new Position(7, 3));
            planting(PlantFactory.createBattlePlant(PlantType.HYPNO_SHROOM.getName(), 1,
                new Position(1, 2)), new Position(7, 2));
            planting(PlantFactory.createBattlePlant(PlantType.HYPNO_SHROOM.getName(), 1,
                new Position(1, 2)), new Position(7, 1));


        }

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

            if (!zombie.isAlive()) {
                killAward(this.thisUser);
                glowingAward(this);
                Position zPos = Position.getRowAndColumn(zombie.getPosition());
                System.out.printf("Zombie of type %s is dead at (%d, %d)\n",
                    zombie.getName(), (int) zPos.getX(), (int) zPos.getY());

                addKilledZombieCost(zombie.getWaveNum(), zombie.getCost());
                z.remove();
            } else {
                if (zombie.getCurrentHP() > 0) {
                    zombie.update();
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
        Iterator<Dynamite> dy = dynamites.iterator();
        while (dy.hasNext()) {
            Dynamite thisDynamite = dy.next();

            thisDynamite.update();
        }

        for (BattlePlant battlePlant : plants) {
            battlePlant.setCurrentCoolDown(Math.max(battlePlant.getCurrentCoolDown() - 1, 0));
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
                        positionOfZ = new Position(spawnX - 200, getRealY(spawnY));
                    } else {
                        positionOfZ = new Position(spawnX, getRealY(spawnY));
                    }
                    Zombie newZombie = ZombieFactory.createZombie("DEFAULT", positionOfZ);
                    System.out.printf("Zombie %s spawned at wave %d in lane %d which costed %d.\n",
                        nameOfZ, thisWave.getWaveNum(), spawnY, newZombie.getCost());

                    newZombie.setWaveNum(thisWave.getWaveNum());
                    this.gameZombies.add(newZombie);
                    thisWave.addZombieToSpawned(newZombie);
                }
                if (!thisWave.isReadyForNextWave()) {
                    break;
                }
            }
        }

        // Checking if the end of the game (Losing) + Activate Mowers :
        for (Zombie zombie : gameZombies) {
            if (!zombie.isAlive()) continue;

            int zRow = zombie.getRow();
            float zX = (float) zombie.getPosition().getX();

            Mower currentMower = mowers.stream()
                .filter(m -> m.getRow() == zRow)
                .findFirst()
                .orElse(null);

            if (currentMower != null) {
                if (!currentMower.isUsed()) {
                    if (zX <= currentMower.getX() + 40) {
                        System.out.println("Lawn mower triggered in row: " + zRow);
                        currentMower.trigger();
                    }
                } else if (currentMower.isDone() && zX <= 390) {
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
}
