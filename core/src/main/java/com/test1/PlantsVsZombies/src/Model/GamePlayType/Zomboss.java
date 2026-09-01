package com.test1.PlantsVsZombies.src.Model.GamePlayType;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Model.Mower;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Dynamite;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Tile;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class Zomboss extends ConveyorBelt {

    public Zomboss(ChapterType chapterType, int level, int difficulty, User thisUser,
                   ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        super(chapterType, level, difficulty, thisUser, plants, zombies, boosted);


        this.allWaves.clear();
        this.gameZombies.clear();
        this.mySuns = 0;
        setLevelObjectives("Defeat Dr. Zomboss!");
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


        Iterator<BattlePlant> bp = gamePlants.iterator();
        while (bp.hasNext()) {
            BattlePlant plant = bp.next();
            if (plant.isAlive() && plant.getCurrentHP() > 0) {
                plant.update();
                plant.setCooldown(0);
            } else {
                Tile currentTile = getTileByPosition(plant.getColumn(), plant.getRow());
                if (currentTile != null) {
                    currentTile.getPlants().removeIf(p -> p == plant);
                }
                bp.remove();
            }
        }


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

        updateZombieTiles();


        Iterator<Projectile> pj = projectiles.iterator();
        while (pj.hasNext()) {
            Projectile p = pj.next();
            if (p.isActive()) p.update();
            else pj.remove();
        }

        for (Dynamite d : dynamites) d.update();


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
                }
            }
        }


        if (checkingTheEndOfTheGame()) {
            onWin();
            this.isPaused = true;
            endGame(true);
        }
    }
}
