package com.test1.PlantsVsZombies.src.Model.Shop;

import com.test1.PlantsVsZombies.src.Enums.ShopItemType;
import com.test1.PlantsVsZombies.src.Enums.WalletType;

public class ShopItem {
    private final int id;
    private final String name;
    private final ShopItemType type;
    private final int price;
    private final WalletType currency;
    private final int unitSize;
    private final int maxTotal;
    private final String description;

    public ShopItem(int id, String name, ShopItemType type, int price, WalletType currency,
                    int unitSize, int maxTotal, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.currency = currency;
        this.unitSize = unitSize;
        this.maxTotal = maxTotal;
        this.description = description;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ShopItemType getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public WalletType getCurrency() {
        return currency;
    }

    public int getUnitSize() {
        return unitSize;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public String getDescription() {
        return description;
    }
}
