package com.test1.PlantsVsZombies.src.View.ViewInterfaces;

import com.test1.PlantsVsZombies.src.Model.Shop.DailyOffer;
import com.test1.PlantsVsZombies.src.Model.Shop.ShopItem;

import java.util.List;

public interface ShopMenuView extends BaseView {
    void showShopList(List<ShopItem> items);

    void showDailyOffer(DailyOffer offer);

    void showPurchaseSuccess();
}
