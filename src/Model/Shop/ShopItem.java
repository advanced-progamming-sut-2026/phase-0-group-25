package src.Model.Shop;

import src.Enums.ShopItemType;
import src.Enums.WalletType;

public class ShopItem {
    private int id;
    private String name;
    private ShopItemType type;
    private int price;
    private WalletType currency;
    private int unitSize;
    private int maxTotal;
    private String description;

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