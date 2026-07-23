package src.Model.Shop;

import src.Enums.PlantType;
import src.Enums.ShopItemType;
import src.Enums.WalletType;
import src.Model.User.User;
import src.Model.User.UserProgress;
import src.Model.User.UsersManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShopManager {
    private static ShopManager instance;
    private List<ShopItem> permanentItems;
    private DailyOffer dailyOffer;

    private ShopManager() {
        permanentItems = new ArrayList<>();
        initPermanentItems();
        generateDailyOffer();
    }

    public static ShopManager getInstance() {
        if (instance == null) {
            instance = new ShopManager();
        }
        return instance;
    }

    private void initPermanentItems() {
        // ID, Name, Type, Price, Currency, UnitSize, MaxTotal, Description
        permanentItems.add(new ShopItem(1, "Pot", ShopItemType.POT, 2000, WalletType.COIN, 1, 20,
                "Unlocks a greenhouse slot (max 20)"));
        permanentItems.add(new ShopItem(2, "Plant Food", ShopItemType.PLANT_FOOD, 3, WalletType.DIAMOND, 1, 3,
                "Used to boost plants (max 3 stored)"));
        permanentItems.add(new ShopItem(3, "Random Seed Packet", ShopItemType.RANDOM_SEED_PACKET, 1000, WalletType.COIN, 5, 0,
                "Gives 5 seed packets for a random unlocked plant"));
        permanentItems.add(new ShopItem(4, "Selective Seed Packet", ShopItemType.SELECTIVE_SEED_PACKET, 5, WalletType.DIAMOND, 10, 0,
                "Gives 10 seed packets for a chosen unlocked plant"));
        permanentItems.add(new ShopItem(5, "Currency Exchange", ShopItemType.CURRENCY_EXCHANGE, 5, WalletType.DIAMOND, 500, 0,
                "Exchange 5 gems for 500 coins"));
    }

    private void generateDailyOffer() {
        // Get all unlocked plants for the user
        User user = UsersManager.getInstance().getLoggedInUser();
        if (user == null) return;
        List<PlantType> unlocked = new ArrayList<>(user.getUserProgress().getUnlockedPlantsAndTheirLevels().keySet());
        if (unlocked.isEmpty()) {
            // fallback: use Sunflower if nothing else
            dailyOffer = new DailyOffer(PlantType.SUNFLOWER, 1600, 10, LocalDate.now());
            return;
        }
        PlantType randomPlant = unlocked.get(new Random().nextInt(unlocked.size()));
        dailyOffer = new DailyOffer(randomPlant, 1600, 10, LocalDate.now()); // base 2000, 20% off -> 1600
    }

    public List<ShopItem> getPermanentItems() {
        return permanentItems;
    }

    public DailyOffer getDailyOffer() {
        // Check if the stored daily offer is still valid for today; if not, regenerate
        if (dailyOffer == null || !dailyOffer.isValidForToday()) {
            generateDailyOffer();
        }
        return dailyOffer;
    }
// file: src/Model/Shop/ShopManager.java
// Replace the purchase logic with the following clean version

    public String purchaseItem(int itemId, int count, String plantTypeName) {
        User user = UsersManager.getInstance().getLoggedInUser();
        if (user == null) return "You must be logged in to purchase.";

        UserProgress progress = user.getUserProgress();
        UsersManager um = UsersManager.getInstance();

        // Daily offer
        if (itemId == 6) {
            return purchaseDailyOffer(um);
        }

        ShopItem item = permanentItems.stream().filter(i -> i.getId() == itemId).findFirst().orElse(null);
        if (item == null) return "Invalid item ID.";

        if (count <= 0) return "Count must be positive.";
        if (count % item.getUnitSize() != 0) {
            return "Count must be a multiple of " + item.getUnitSize() + ".";
        }

        int units = count / item.getUnitSize();
        int totalPrice = item.getPrice() * units;

        // Deduct currency using subtract methods
        if (item.getCurrency() == WalletType.COIN) {
            String error = um.subtractCoins(totalPrice);
            if (error != null) return error;
        } else {
            String error = um.subtractGems(totalPrice);
            if (error != null) return error;
        }

        // Apply item effects
        switch (item.getType()) {
            case POT:
                if (progress.getPotsCount() + units > item.getMaxTotal()) {
                    return "Cannot exceed " + item.getMaxTotal() + " pots.";
                }
                um.addPots(units);
                break;

            case PLANT_FOOD:
                if (progress.getPlantFoodCount() + units > item.getMaxTotal()) {
                    return "Cannot exceed " + item.getMaxTotal() + " plant food items.";
                }
                um.addPlantFood(units);
                break;

            case RANDOM_SEED_PACKET:
                List<PlantType> unlockedPlants = new ArrayList<>(progress.getUnlockedPlantsAndTheirLevels().keySet());
                if (unlockedPlants.isEmpty()) {
                    return "You have no unlocked plants to receive seeds for.";
                }
                PlantType randomPlant = unlockedPlants.get(new Random().nextInt(unlockedPlants.size()));
                um.addSeedPackets(randomPlant, count);
                break;

            case SELECTIVE_SEED_PACKET:
                if (plantTypeName == null || plantTypeName.trim().isEmpty()) {
                    return "You must specify a plant type with -t <plant> for selective seed packet.";
                }
                PlantType selected = PlantType.fromName(plantTypeName);
                if (selected == null) {
                    return "Invalid plant type: " + plantTypeName;
                }
                if (!progress.getUnlockedPlantsAndTheirLevels().containsKey(selected)) {
                    return "You have not unlocked " + selected.getName() + " yet.";
                }
                um.addSeedPackets(selected, count);
                break;

            case CURRENCY_EXCHANGE:
                // Exchange: subtract gems (5 per unit) and add coins (500 per unit)
                String gemError = um.subtractGems(5 * units);
                if (gemError != null) return gemError;
                um.addCoins(500 * units);
                break;

            default:
                return "Unknown item type.";
        }

        return null; // success
    }

    private String purchaseDailyOffer(UsersManager um) {
        if (um.isDailyOfferBoughtToday()) {
            return "Daily offer already purchased today.";
        }
        DailyOffer offer = getDailyOffer();
        if (offer == null) return "No daily offer available.";

        String error = um.subtractCoins(offer.getPrice());
        if (error != null) return error;

        um.addSeedPackets(offer.getPlantType(), offer.getSeedPacketCount());
        um.markDailyOfferPurchased();
        return null;
    }


}