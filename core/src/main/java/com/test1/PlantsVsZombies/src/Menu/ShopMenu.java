package com.test1.PlantsVsZombies.src.Menu;

import com.test1.PlantsVsZombies.src.Enums.Command;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Model.Shop.ShopManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.ShopMenuView;

import java.util.regex.Matcher;

public class ShopMenu extends Menu {
    private final ShopMenuView shopMenuView;
    private final ShopManager shopManager;

    public ShopMenu(ShopMenuView shopMenuView) {
        super(MenuType.GreenHouse);
        this.shopMenuView = shopMenuView;
        this.shopManager = ShopManager.getInstance();
    }

    @Override
    public void handleSpecificCommands(String input) {
        Matcher matcher;

        if ((matcher = getMatcher(input, Command.ShopList)) != null) {
            shopMenuView.showShopList(shopManager.getPermanentItems());
            return;
        }

        if ((matcher = getMatcher(input, Command.ShopDaily)) != null) {
            shopMenuView.showDailyOffer(shopManager.getDailyOffer());
            return;
        }

        if ((matcher = getMatcher(input, Command.ShopBuy)) != null) {
            int itemId = Integer.parseInt(matcher.group("itemId"));
            int count = Integer.parseInt(matcher.group("count"));
            String plantType = matcher.group("plantType");
            String error = shopManager.purchaseItem(itemId, count, plantType);
            if (error == null) {
                shopMenuView.showPurchaseSuccess();
            } else {
                getView().showError(error);
            }
            return;
        }

        getView().showError("Invalid command format for this menu state.");
    }

    @Override
    public BaseView getView() {
        return shopMenuView;
    }
}
