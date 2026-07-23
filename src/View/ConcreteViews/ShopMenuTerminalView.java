package src.View.ConcreteViews;

import src.Model.Shop.DailyOffer;
import src.Model.Shop.ShopItem;
import src.View.ViewInterfaces.ShopMenuView;

import java.util.List;

public class ShopMenuTerminalView extends AbstractTerminalView implements ShopMenuView {

    @Override
    public void showShopList(List<ShopItem> items) {
        System.out.println("=== Permanent Items ===");
        System.out.printf("%-4s %-22s %-10s %-8s %-6s %s\n", "ID", "Name", "Price", "Currency", "Unit", "Description");
        for (ShopItem item : items) {
            System.out.printf("%-4d %-22s %-10d %-8s %-6d %s\n",
                    item.getId(), item.getName(), item.getPrice(),
                    item.getCurrency().getName(), item.getUnitSize(), item.getDescription());
        }
        System.out.println("(Use 'shop buy -i <id> -n <count> [-t plant]' to purchase)");
    }

    @Override
    public void showDailyOffer(DailyOffer offer) {
        System.out.println("=== Daily Offer ===");
        if (offer == null) {
            System.out.println("No daily offer available.");
            return;
        }
        System.out.println("Plant: " + offer.getPlantType().getName());
        System.out.println("Seed Packets: " + offer.getSeedPacketCount());
        System.out.println("Discounted Price: " + offer.getPrice() + " coins (20% off)");
        System.out.println("Valid for today (" + offer.getDate() + ") - only one purchase allowed.");
        System.out.println("To buy: shop buy -i 6 -n 1");
    }

    @Override
    public void showPurchaseSuccess() {
        System.out.println("Purchase completed successfully!");
    }

    @Override
    public void showCurrentMenu() {
        System.out.println("shop menu");
    }
}