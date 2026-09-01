package com.test1.PlantsVsZombies.src.Model.GamePlayType;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Model.Mower;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.*;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Dynamite;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.Tile;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.Model.Wave.FinalWave;
import com.test1.PlantsVsZombies.src.Model.Wave.Wave;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.UIManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Simple extends GamePlay {

    public Simple(ChapterType chapterType, int level, int difficulty, User thisUser,
                  ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        super(chapterType, level, difficulty, thisUser, plants, zombies, boosted);
        setLevelObjectives("Zombies shouldn't reach the house.");
        //Zomboss zomboss = ZombieFactory.createZomboss("DARK", new Position(1700, 600));
        //this.gameZombies.add(zomboss);
    }

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;
        timeToSpawn = Math.max(timeToSpawn - 1, 0);

        if (!settedThePlants) {
            if (chapterType == ChapterType.FROSTBITE_CAVES) {
                String thisPName1 = "SUNFLOWER";
                String thisPName2 = "SUNFLOWER";
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
            UIManager.showToast("Level Started! Protect your lawn!", "IMAGE_UI_GENERIC_VTB");
        }

        if (this.chapterType != ChapterType.DARK_AGE) {
            sunMaker();
        }
        applyIcyWind();

        checkingSunMakers();

        pendingNewPlants.clear();
        updatingPlants = true;

        Iterator<BattlePlant> bp = gamePlants.iterator();
        while (bp.hasNext()) {
            BattlePlant plant = bp.next();

            if (plant.isAlive()) {
                plant.update();

                plant.setCooldown(Math.max(plant.getCooldown() - 1, 0));
            } else {
                Tile currentTile = getTileByPosition(plant.getColumn(), plant.getRow());

                if (currentTile != null) {
                    currentTile.getPlants().removeIf(p -> p == plant);
                }

                incrementLostPlants();
                bp.remove();
            }
        }

        updatingPlants = false;

        if (!pendingNewPlants.isEmpty()) {
            gamePlants.addAll(pendingNewPlants);
            pendingNewPlants.clear();
        }

        pendingNewZombies.clear();
        updatingZombies = true;

        Iterator<Zombie> z = gameZombies.iterator();
        while (z.hasNext()) {
            Zombie zombie = z.next();

            if (!zombie.isAlive()) {
                killAward(this.thisUser);

                addKilledZombieCost(zombie.getWaveNum(), zombie.getCost());
                z.remove();
            } else {
                zombie.update();
            }
        }

        updatingZombies = false;

        if (!pendingNewZombies.isEmpty()) {
            gameZombies.addAll(pendingNewZombies);
            pendingNewZombies.clear();
        }

        updateZombieTiles();
        updatePlantTiles();

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


        if (timeToSpawn == 0) {
            timeToSpawn = getRandomTime();
            for (Wave thisWave : allWaves) {
                if (thisWave.hasZombiesLeftToSpawn()) {
                    if (!thisWave.getStarted()) {
                        if (thisWave instanceof FinalWave) {
                            triggerNecromancy();
                            triggerLowTide();
                            UIManager.showToast("FINAL WAVE IS APPROACHING!", "IMAGE_UI_GENERIC_TIMER_RIBBON_RED");
                        } else {
                            UIManager.showToast("Wave " + thisWave.getWaveNum() + " has started!", "IMAGE_UI_GENERIC_VTB");
                        }
                        thisWave.setStarted(true);
                    }
                    String nameOfZ = thisWave.spawnNextZombie().getName();
                    Position positionOfZ;
                    int spawnY = getNextRandomY();

                    if (chapterType == ChapterType.ANCIENT_EGYPT && Math.random() <= 0.12) {
                        int targetCol = random.nextInt(3) + 5;
                        positionOfZ = new Position(getRealX(targetCol), getRealY(spawnY));
                        addSandstormEffect((float) positionOfZ.getX(), (float) positionOfZ.getY());
                        UIManager.showToast("Sandstorm Inbound! (Lane " + spawnY + ")", "IMAGE_UI_GENERIC_TIMER_RIBBON_RED");
                    } else {
                        positionOfZ = new Position(spawnX, getRealY(spawnY));
                    }

                    Zombie newZombie = ZombieFactory.createZombie(nameOfZ, positionOfZ);

                    newZombie.setWaveNum(thisWave.getWaveNum());
                    this.gameZombies.add(newZombie);
                    thisWave.addZombieToSpawned(newZombie);
                }
                if (!thisWave.isReadyForNextWave()) {
                    break;
                }
            }
        }


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
                        currentMower.trigger();
                    }
                } else if (currentMower.isDone() && zX <= 390) {
                    UsersManager.getInstance().addGamesPlayed();
                    endGame(false);
                }
            }
        }


        if (checkingTheEndOfTheGame()) {
            onWin();
            endGame(true);
        }
    }

    public void triggerNecromancy() {
        if (chapterType != ChapterType.DARK_AGE) return;

        boolean spawnedAny = false;
        for (Tile tile : tiles) {
            if (!tile.isArable() && tile.getHP() > 0 && tile.isNecromancy() && !tile.isNecromancyTriggered()) {
                int col = (int) tile.getPosition().getX();
                int row = (int) tile.getPosition().getY();

                Position spawnPos = new Position(getRealX(col), getRealY(row));
                Zombie zombie = ZombieFactory.createZombie("DEFAULT", spawnPos);
                zombie.setRow(row);
                zombie.setColumn(col);
                gameZombies.add(zombie);

                tile.setNecromancyTriggered(true);
                spawnedAny = true;
            }
        }

        if (spawnedAny) {
            UIManager.showToast("Necromancy! Zombies rising from tombs!", "IMAGE_UI_GENERIC_TIMER_RIBBON_RED");
        }
    }
}
