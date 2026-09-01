package com.test1.PlantsVsZombies.src.Model.GamePlayType;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Model.Mower;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.*;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Dynamite;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.Tile;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class ZombossGamePlay extends ConveyorBelt {

    private Zomboss gameZomboss;

    public ZombossGamePlay(ChapterType chapterType, int level, int difficulty, User thisUser,
                           ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        super(chapterType, level, difficulty, thisUser, plants, zombies, boosted);

        this.allWaves.clear();
        this.gameZombies.clear();
        this.mySuns = 0;
        setLevelObjectives("Defeat Dr. Zomboss!");

        spawnZomboss();
    }

    private void spawnZomboss() {
        int gridX = 9;
        int gridY = 3;

        Position zombossRealPosition = new Position(getRealX(gridX), getRealY(gridY));
        String zombossName = getNameOfZomboss(this.chapterType);

        Zomboss zomboss = ZombieFactory.createZomboss(zombossName, zombossRealPosition);

        if (zomboss != null) {
            zomboss.setRow(gridY);
            zomboss.setColumn(gridX);
            zomboss.setPosition(zombossRealPosition);
            zomboss.setAlive(true);

            this.gameZomboss = zomboss;
            this.gameZombies.add(zomboss);
        }
    }

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;

        conveyorTimer++;
        if (conveyorTimer >= SPAWN_INTERVAL_TICKS) {
            conveyorTimer = 0;
            spawnNewCard();
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
            if (!zombie.isAlive() || zombie.getCurrentHP() <= 0) {
                killAward(this.thisUser);
                if (zombie.isHalated()) glowingAward(zombie.getPosition());
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


        Iterator<Projectile> pj = projectiles.iterator();
        while (pj.hasNext()) {
            Projectile p = pj.next();
            if (p.isActive()) p.update();
            else pj.remove();
        }


        Iterator<Dynamite> dy = dynamites.iterator();
        while (dy.hasNext()) {
            Dynamite thisDynamite = dy.next();
            thisDynamite.update();
        }


        for (Zombie zombie : gameZombies) {
            if (!zombie.isAlive()) continue;
            int zRow = zombie.getRow();
            float zX = (float) zombie.getPosition().getX();

            Mower mower = mowers.stream().filter(m -> m.getRow() == zRow).findFirst().orElse(null);
            if (mower != null) {
                if (!mower.isUsed() && zX <= mower.getX() + 40) {
                    mower.trigger();
                } else if (mower.isDone() && zX <= 390) {
                    UsersManager.getInstance().addGamesPlayed();
                    this.isPaused = true;
                    endGame(false);
                    return;
                }
            }
        }


        if (gameZomboss != null && (!gameZomboss.isAlive() || gameZomboss.getCurrentHP() <= 0)) {
            onWin();
            this.isPaused = true;
            endGame(true);
        }
    }

    private String getNameOfZomboss(ChapterType chapterType) {
        if (chapterType == ChapterType.ANCIENT_EGYPT) {
            return "EGYPT";
        } else if (chapterType == ChapterType.DARK_AGE) {
            return "DARK";
        } else if (chapterType == ChapterType.FROSTBITE_CAVES) {
            return "ICE";
        } else {
            return "BEACH";
        }
    }

    public Zomboss getGameZomboss() {
        return gameZomboss;
    }
}
