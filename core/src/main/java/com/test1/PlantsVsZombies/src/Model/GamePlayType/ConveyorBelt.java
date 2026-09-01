package com.test1.PlantsVsZombies.src.Model.GamePlayType;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
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

public class ConveyorBelt extends GamePlay {
    protected final ArrayList<ConveyorCard> conveyorCards = new ArrayList<>();
    protected final List<String> plantPool;
    protected int conveyorTimer = 0;
    protected final int SPAWN_INTERVAL_TICKS = 120;
    protected final int MAX_CARDS = 6;
    protected final float CARD_TOP_Y = 980f;
    protected final float CARD_HEIGHT = 105f;
    protected final float CARD_SPACING = 11f;

    public ConveyorBelt(ChapterType chapterType, int level, int difficulty, User thisUser,
                        ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        super(chapterType, level, difficulty, thisUser, plants, zombies, boosted);

        if (plants != null && plants.size() >= 3) {
            this.plantPool = new ArrayList<>(plants);
        } else {
            this.plantPool = new ArrayList<>(List.of(
                "PEASHOOTER", "WALL_NUT", "REPEATER", "BONK_CHOY", "SNOW_PEA", "POTATO_MINE"
            ));
        }
        this.mySuns = 0;

        spawnNewCard();
    }

    protected void spawnNewCard() {
        if (conveyorCards.size() >= MAX_CARDS) return;

        String randomPlant = plantPool.get(random.nextInt(plantPool.size()));
        BattlePlant plant = PlantFactory.createBattlePlant(randomPlant, getLevelOfPlant(randomPlant), new Position(0, 0));

        if (plant != null) {
            ConveyorCard card = new ConveyorCard(plant, 80f);
            conveyorCards.add(card);
            recalculateTargets();
        }
    }

    public void recalculateTargets() {
        for (int i = 0; i < conveyorCards.size(); i++) {
            float targetY = CARD_TOP_Y - (i * (CARD_HEIGHT + CARD_SPACING));
            conveyorCards.get(i).setTargetY(targetY);
        }
    }

    public void removeCard(ConveyorCard card) {
        conveyorCards.remove(card);
        recalculateTargets();
    }

    @Override
    public void planting(BattlePlant plant, Position position) {
        if (plant == null || position == null) return;
        int col = (int) position.getX();
        int row = (int) position.getY();
        Tile targetTile = getTileByPosition(col, row);

        if (targetTile != null && targetTile.isArable() && targetTile.getPlants().isEmpty()) {
            plant.setRow(row);
            plant.setColumn(col);
            plant.setPosition(new Position(getRealX(col), getRealY(row)));
            plant.setCurrentHP(plant.getPlantStats().getBaseHP());
            plant.setAlive(true);

            this.gamePlants.add(plant);
            targetTile.addPlant(plant);
        }
    }

    @Override
    public void sunMaker() {}

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;
        timeToSpawn = Math.max(timeToSpawn - 1, 0);

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


        if (timeToSpawn == 0) {
            timeToSpawn = getRandomTime();
            for (Wave thisWave : allWaves) {
                if (thisWave.hasZombiesLeftToSpawn()) {
                    if (!thisWave.getStarted()) {
                        if (thisWave instanceof FinalWave) {
                            UIManager.showToast("FINAL WAVE IS APPROACHING!", "IMAGE_UI_GENERIC_TIMER_RIBBON_RED");
                        } else {
                            UIManager.showToast("Wave " + thisWave.getWaveNum() + " has started!", "IMAGE_UI_GENERIC_VTB");
                        }
                        thisWave.setStarted(true);
                    }
                    String nameOfZ = thisWave.spawnNextZombie().getName();
                    int spawnY = getNextRandomY();
                    Position pos = new Position(spawnX, getRealY(spawnY));
                    Zombie newZombie = ZombieFactory.createZombie(nameOfZ, pos);
                    newZombie.setWaveNum(thisWave.getWaveNum());
                    this.gameZombies.add(newZombie);
                    thisWave.addZombieToSpawned(newZombie);
                }
                if (!thisWave.isReadyForNextWave()) break;
            }
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
                }
            }
        }

        if (checkingTheEndOfTheGame()) {
            onWin();
            this.isPaused = true;
            endGame(true);
        }
    }

    public ArrayList<ConveyorCard> getConveyorCards() { return conveyorCards; }
}
