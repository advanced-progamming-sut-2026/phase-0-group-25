package src.Model.Shop;

import src.Enums.PlantType;

import java.time.LocalDate;

public class DailyOffer {
    private PlantType plantType;
    private int price;
    private int seedPacketCount;
    private LocalDate date;

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