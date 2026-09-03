package com.test1.PlantsVsZombies.src.Model.Wave;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;

import java.util.*;

public class Wave {
    protected Queue<Zombie> pendingZombies;
    protected ArrayList<Zombie> spawnedZombie = new ArrayList<>();
    protected int totalZombies;
    private final int waveNum;
    private Boolean isStarted = false;
    private final double waveCost;
    private double initialTotalHealth;
    private final Random random = new Random();

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
                if (z.getCost() <= currentBudget) {
                    affordable.add(z);
                }
            }

            if (affordable.isEmpty()) break;


            Zombie pickedZombie = pickWeightedZombie(affordable);
            tempZombies.add(pickedZombie);

            initialTotalHealth += pickedZombie.getZombieStats().getBaseHP();
            currentBudget -= pickedZombie.getCost();
        }


        structureWaveOrder(tempZombies);

        this.pendingZombies.addAll(tempZombies);
        this.totalZombies = this.pendingZombies.size();
    }


    private Zombie pickWeightedZombie(List<Zombie> affordable) {
        double totalWeight = 0;
        double[] weights = new double[affordable.size()];

        for (int i = 0; i < affordable.size(); i++) {
            Zombie z = affordable.get(i);
            double cost = z.getCost();
            double weight;

            if (cost <= 100) {
                weight = Math.max(15.0, 70.0 - (waveNum * 12.0));
            } else if (cost <= 250) {
                weight = 20.0 + (waveNum * 10.0);
            } else {
                weight = (waveNum <= 1) ? 2.0 : (waveNum * 15.0);
            }

            weights[i] = weight;
            totalWeight += weight;
        }

        double randomValue = random.nextDouble() * totalWeight;
        double cumulative = 0;
        for (int i = 0; i < affordable.size(); i++) {
            cumulative += weights[i];
            if (randomValue <= cumulative) {
                return affordable.get(i);
            }
        }

        return affordable.get(0);
    }

    private void structureWaveOrder(List<Zombie> list) {
        if (list.size() <= 2) return;


        list.sort(Comparator.comparingDouble(Zombie::getCost));


        int scoutCount = Math.min(2, Math.max(1, list.size() / 4));
        List<Zombie> scouts = new ArrayList<>(list.subList(0, scoutCount));
        List<Zombie> mainAssault = new ArrayList<>(list.subList(scoutCount, list.size()));


        Collections.shuffle(mainAssault, random);

        list.clear();
        list.addAll(scouts);
        list.addAll(mainAssault);
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
