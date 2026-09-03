package com.test1.PlantsVsZombies.src.Model.Shop;

import com.test1.PlantsVsZombies.src.Enums.PlantType;

import java.time.LocalDate;

public class DailyOffer {
    private final PlantType plantType;
    private final int price;
    private final int seedPacketCount;
    private final LocalDate date;

    public DailyOffer(PlantType plantType, int price, int seedPacketCount, LocalDate date) {
        this.plantType = plantType;
        this.price = price;
        this.seedPacketCount = seedPacketCount;
        this.date = date;
    }


    public PlantType getPlantType() {
        return plantType;
    }

    public int getPrice() {
        return price;
    }

    public int getSeedPacketCount() {
        return seedPacketCount;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isValidForToday() {
        return date.equals(LocalDate.now());
    }
}
