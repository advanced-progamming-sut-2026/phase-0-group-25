package com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Enums.ZombieType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.*;
import com.test1.PlantsVsZombies.src.Model.Tile;
import com.test1.PlantsVsZombies.src.Model.User.User;

import java.util.*;

public class IZombie extends GamePlay {
    public static final int MATCH_DURATION_SECONDS = 120;

    private final Faction myFaction;
    private final boolean isLocalCouchPlay;
    private final String opponentUsername;
    private final long roomSeed;
    private final long startTimeMillis;
    private final Brain[] brains = new Brain[5];
    private final Map<String, Integer> zombieDeck = new LinkedHashMap<>();
    private int zombieBrainPoints = 1500;
    private float matchTimeRemaining = MATCH_DURATION_SECONDS;
    private boolean matchOver = false;
    private boolean matchWon = false;

    public IZombie(ChapterType chapterType, int level, int difficulty, User thisUser,
                   Faction myFaction, boolean isLocalCouchPlay, String opponentUsername,
                   long roomSeed, long startTimeMillis) {
        super(chapterType, level, difficulty, thisUser, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
        this.myFaction = myFaction;
        this.isLocalCouchPlay = isLocalCouchPlay;
        this.opponentUsername = opponentUsername;
        this.roomSeed = roomSeed;
        this.startTimeMillis = startTimeMillis;

        this.mySuns = 800;

        this.mowers.clear();
        for (int i = 0; i < 5; i++) {
            int row = i + 1;
            brains[i] = new Brain(row, 430f, getRealY(row) + 15f);
        }

        zombieDeck.put(ZombieType.DEFAULT.getName(), 50);
        zombieDeck.put(ZombieType.CONE_HEAD.getName(), 75);
        zombieDeck.put(ZombieType.BUCKET_HEAD.getName(), 125);
        zombieDeck.put(ZombieType.NEWSPAPER.getName(), 100);

        this.plants.clear();
        this.plants.add(PlantFactory.createBattlePlant(PlantType.PEASHOOTER.getName(), 1));
        this.plants.add(PlantFactory.createBattlePlant(PlantType.SUNFLOWER.getName(), 1));
        this.plants.add(PlantFactory.createBattlePlant(PlantType.WALL_NUT.getName(), 1));
        this.plants.add(PlantFactory.createBattlePlant(PlantType.REPEATER.getName(), 1));
    }

    public IZombie(User currentUser, Faction myFaction, boolean isNetworkGame,
                   ArrayList<String> pDeck, ArrayList<String> zDeck) {
        this(
            ChapterType.MINI_GAME,
            1,
            (currentUser != null && currentUser.getUserProgress() != null)
                ? currentUser.getUserProgress().getGameDifficulty() : 1,
            currentUser,
            myFaction,
            !isNetworkGame,
            null,
            new java.util.Random().nextLong(),
            System.currentTimeMillis()
        );
    }

    @Override
    public String getLevelObjectives() {
        if (isLocalCouchPlay) {
            return "2-Player Local Battle!\n\nPlant (Mouse): Defend all 5 brains for 2 minutes!\nZombie (Keyboard 1-4, W/S, Space): Devour all 5 brains before time runs out!";
        } else if (myFaction == Faction.ZOMBIE) {
            return "You are the ZOMBIES!\n\nDeploy your undead horde and devour all 5 brains before the 2-minute timer expires!";
        } else {
            return "You are the PLANTS!\n\nBuild a solid defense line and protect all 5 brains for 2 minutes against the zombie onslaught!";
        }
    }

    @Override
    public void update() {
        if (isGameOver()) return;
        if (isLocalCouchPlay && isPaused) return;

        if (isLocalCouchPlay) {
            matchTimeRemaining = Math.max(0f, matchTimeRemaining - 0.1f);
        }

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
        boolean allBrainsEaten = true;
        for (Brain b : brains) {
            if (!b.isEaten()) {
                allBrainsEaten = false;
                break;
            }
        }
        if (allBrainsEaten) {
            endMatch(Faction.ZOMBIE);
            return;
        }

        if (getSecondsRemaining() <= 0) {
            endMatch(Faction.PLANT);
        }
    }

    public boolean placePlant(BattlePlant plant, int col, int row) {
        Tile tile = getTileByPosition(col, row);
        if (tile == null || !tile.isArable() || !tile.getPlants().isEmpty()) return false;
        if (mySuns < plant.getPlantStats().getCost()) return false;

        mySuns -= plant.getPlantStats().getCost();
        Position pos = new Position(getRealX(col), getRealY(row));
        BattlePlant newPlant = PlantFactory.createBattlePlant(plant.getName(), 1, pos);
        newPlant.setRow(row);
        newPlant.setColumn(col);
        this.gamePlants.add(newPlant);
        tile.addPlant(newPlant);
        return true;
    }

    public void applyRemotePlacePlant(String plantName, int col, int row) {
        Tile tile = getTileByPosition(col, row);
        if (tile == null) return;
        Position pos = new Position(getRealX(col), getRealY(row));
        BattlePlant newPlant = PlantFactory.createBattlePlant(plantName, 1, pos);
        newPlant.setRow(row);
        newPlant.setColumn(col);
        this.gamePlants.add(newPlant);
        tile.addPlant(newPlant);
    }

    public boolean spawnZombie(String zombieName, int row) {
        int cost = zombieDeck.getOrDefault(zombieName, 50);
        if (zombieBrainPoints < cost) return false;

        zombieBrainPoints -= cost;
        Position spawnPos = new Position(1800f, getRealY(row));
        Zombie newZombie = ZombieFactory.createZombie(zombieName, spawnPos);
        if (newZombie != null) {
            newZombie.setRow(row);
            newZombie.setColumn(9);
            this.gameZombies.add(newZombie);
            return true;
        }
        return false;
    }

    public void applyRemoteSpawnZombie(String zombieName, int row) {
        Position spawnPos = new Position(1800f, getRealY(row));
        Zombie newZombie = ZombieFactory.createZombie(zombieName, spawnPos);
        if (newZombie != null) {
            newZombie.setRow(row);
            newZombie.setColumn(9);
            this.gameZombies.add(newZombie);
        }
    }

    public void endMatch(Faction winnerFaction) {
        this.matchOver = true;
        this.matchWon = (this.myFaction == winnerFaction);
        this.isPaused = true;
    }

    @Override
    public boolean isGameOver() {
        return matchOver;
    }

    @Override
    public boolean hasWon() {
        return matchWon;
    }

    public int getSecondsRemaining() {
        if (isLocalCouchPlay) {
            return (int) Math.ceil(matchTimeRemaining);
        }

        long elapsedMs = Math.max(0, System.currentTimeMillis() - startTimeMillis);
        long remainingMs = (MATCH_DURATION_SECONDS * 1000L) - elapsedMs;
        return Math.max(0, (int) Math.ceil(remainingMs / 1000.0));
    }

    public Brain[] getBrains() {
        return brains;
    }

    public Map<String, Integer> getZombieDeck() {
        return zombieDeck;
    }

    public int getZombieBrainPoints() {
        return zombieBrainPoints;
    }

    public Faction getMyFaction() {
        return myFaction;
    }

    public boolean isLocalCouchPlay() {
        return isLocalCouchPlay;
    }

    public boolean isNetworkGame() {
        return !isLocalCouchPlay;
    }

    public String getOpponentUsername() {
        return opponentUsername;
    }

    public long getRoomSeed() {
        return roomSeed;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public boolean plantDefenseAction(BattlePlant plant, int col, int row) {
        return placePlant(plant, col, row);
    }

    public boolean spawnZombieAction(String zombieName, int row) {
        return spawnZombie(zombieName, row);
    }
}
