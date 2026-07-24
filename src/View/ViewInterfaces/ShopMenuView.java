package src.View.ViewInterfaces;

import src.Model.Shop.DailyOffer;
import src.Model.Shop.ShopItem;

import java.util.List;

public interface ShopMenuView extends BaseView {
    void showShopList(List<ShopItem> items);

    void showDailyOffer(DailyOffer offer);

    void showPurchaseSuccess();
}