package com.test1.PlantsVsZombies.src.Model.Wave;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;

import java.util.*;

public class Wave {
    protected Queue<Zombie> pendingZombies;
    protected ArrayList<Zombie> spawnedZombie = new ArrayList<>();
    protected int totalZombies;
    private int waveNum;
    private Boolean isStarted = false;
    private double waveCost;
    private double initialTotalHealth;
    private Random random = new Random();

    public Wave(int waveCost, int waveNum) {
        this.waveCost = waveCost;
        this.pendingZombies = new LinkedList<>();
        this.initialTotalHealth = 0;
        this.waveNum = waveNum;
    }

    public void zombieMaker(ArrayList<Zombie> usableZombies) {
        double currentBudget = waveCost;
        List<Zombie> tempZombies = new ArrayList<>();

        while (currentBudget > 0) {
            ArrayList<Zombie> affordable = new ArrayList<>();
            for (Zombie z : usableZombies) {
                double zombieCost = z.getCost();
                if (zombieCost <= currentBudget) {
                    affordable.add(z);
                }
            }

            if (affordable.isEmpty()) break;

            Zombie templateZombie = affordable.get(random.nextInt(affordable.size()));
            Zombie newZombie = templateZombie;

            tempZombies.add(newZombie);

            initialTotalHealth += newZombie.getZombieStats().getBaseHP();
            currentBudget -= newZombie.getCost();
        }

        tempZombies.sort((z1, z2) -> {
            double cost1 = z1.getCost();
            double cost2 = z2.getCost();
            return Double.compare(cost1, cost2);
        });

        this.pendingZombies.addAll(tempZombies);
        this.totalZombies = this.pendingZombies.size();
    }

    public boolean isReadyForNextWave() {
        if (pendingZombies.isEmpty()) return true;

        double damageAmount = 0;
        for (Zombie z : spawnedZombie) {
            if (!z.isAlive() || z.getCurrentHP() == 0) {
                damageAmount += z.getZombieStats().getBaseHP();
            } else {
                damageAmount += z.getZombieStats().getBaseHP() - z.getCurrentHP();
            }
        }

        return damageAmount >= (initialTotalHealth * 0.75);
    }

    public Boolean getStarted() {
        return isStarted;
    }

    public void setStarted(Boolean started) {
        isStarted = started;
    }

    public int getWaveNum() {
        return this.waveNum;
    }

    public boolean hasZombiesLeftToSpawn() {
        return !pendingZombies.isEmpty();
    }

    public Zombie spawnNextZombie() {
        return pendingZombies.poll();
    }

    public void addZombieToSpawned(Zombie thisZombie) {
        this.spawnedZombie.add(thisZombie);
    }
}
