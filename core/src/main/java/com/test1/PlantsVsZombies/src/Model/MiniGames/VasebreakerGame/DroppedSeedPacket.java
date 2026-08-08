package com.test1.PlantsVsZombies.src.Model.MiniGames.VasebreakerGame;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;

public class DroppedSeedPacket {
    private BattlePlant plant;
    private Position position;
    private int remainingTicks; // زمان باقی‌مانده قبل از ناپدید شدن

    public DroppedSeedPacket(BattlePlant plant, Position position, int disappearTicks) {
        this.plant = plant;
        this.position = position;
        this.remainingTicks = disappearTicks;
    }

    public void update() {
        if (remainingTicks > 0) {
            remainingTicks--;
        }
    }

    public boolean isExpired() {
        return remainingTicks <= 0;
    }

    public BattlePlant getPlant() {
        return plant;
    }

    public Position getPosition() {
        return position;
    }
}
