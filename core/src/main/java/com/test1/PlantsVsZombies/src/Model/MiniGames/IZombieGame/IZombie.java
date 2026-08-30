package com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.PlantFactory;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.ZombieFactory;
import com.test1.PlantsVsZombies.src.Model.Tile;
import com.test1.PlantsVsZombies.src.Model.User.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IZombie extends GamePlay {
    private final Faction myFaction;
    private final boolean isNetworkGame;
    private float matchTimeRemaining = 120.0f;
    private final List<Brain> brains = new ArrayList<>();
    private int zombieSunBudget = 1500;

    public IZombie(User thisUser, Faction myFaction, boolean isNetworkGame,
                   ArrayList<String> plantDeck, ArrayList<String> zombieDeck) {
        super(ChapterType.MINI_GAME, 1, 3, thisUser, plantDeck, zombieDeck, null);
        this.myFaction = myFaction;
        this.isNetworkGame = isNetworkGame;
        this.mySuns = 800;


        this.mowers.clear();
        for (int row = 1; row <= 5; row++) {
            brains.add(new Brain(row, 430f, getRealY(row) + 15f));
        }
    }

    @Override
    public void update() {
        if (isPaused || isGameOver()) return;


        matchTimeRemaining = Math.max(0f, matchTimeRemaining - 0.1f);


        sunMaker();
        checkingSunMakers();


        checkZombieBrainCollisions();


        for (int i = 0; i < projectiles.size(); i++) {
            projectiles.get(i).update();
        }


        Iterator<Zombie> it = gameZombies.iterator();
        while (it.hasNext()) {
            Zombie z = it.next();
            if (!z.isAlive() || z.getCurrentHP() <= 0) {
                it.remove();
            } else {
                z.update();
            }
        }


        for (BattlePlant p : gamePlants) {
            if (p.isAlive()) p.update();
        }

        updateZombieTiles();
        checkMatchEndConditions();
    }

    private void checkZombieBrainCollisions() {
        for (Zombie z : gameZombies) {
            if (!z.isAlive()) continue;
            for (Brain brain : brains) {
                if (!brain.isEaten() && brain.getRow() == z.getRow()) {
                    if (z.getPosition().getX() <= brain.getX() + 20) {
                        brain.setEaten(true);
                        z.setCurrentHP(0);
                        z.setAlive(false);
                    }
                }
            }
        }
    }

    private void checkMatchEndConditions() {

        boolean allBrainsEaten = brains.stream().allMatch(Brain::isEaten);
        if (allBrainsEaten) {
            endGame(myFaction == Faction.ZOMBIE);
            return;
        }


        if (matchTimeRemaining <= 0.0f) {
            endGame(myFaction == Faction.PLANT);
        }
    }

    public boolean spawnZombieAction(String zombieName, int row) {
        Zombie sample = ZombieFactory.createZombie(zombieName);
        if (sample == null) return false;
        int cost = sample.getCost();

        if (zombieSunBudget < cost) return false;

        zombieSunBudget -= cost;
        Position spawnPos = new Position(1800f, getRealY(row));
        Zombie newZombie = ZombieFactory.createZombie(zombieName, spawnPos);
        newZombie.setRow(row);
        newZombie.setColumn(9);
        gameZombies.add(newZombie);
        return true;
    }

    public boolean plantDefenseAction(BattlePlant plant, int col, int row) {
        Tile tile = getTileByPosition(col, row);
        if (tile == null || !tile.isArable() || !tile.getPlants().isEmpty()) return false;
        if (mySuns < plant.getPlantStats().getCost()) return false;

        mySuns -= plant.getPlantStats().getCost();
        Position pos = new Position(getRealX(col), getRealY(row));
        BattlePlant newPlant = PlantFactory.createBattlePlant(plant.getName(), getLevelOfPlant(plant.getName()), pos);
        newPlant.setRow(row);
        newPlant.setColumn(col);
        this.gamePlants.add(newPlant);
        tile.addPlant(newPlant);
        return true;
    }

    public float getMatchTimeRemaining() { return matchTimeRemaining; }
    public List<Brain> getBrains() { return brains; }
    public Faction getMyFaction() { return myFaction; }
    public int getZombieSunBudget() { return zombieSunBudget; }
    public boolean isNetworkGame() { return isNetworkGame; }
}
