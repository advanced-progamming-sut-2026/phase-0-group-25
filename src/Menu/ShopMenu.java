package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.ShopMenuView;

public class ShopMenu extends Menu{
    private final ShopMenuView shopMenuView;

    public ShopMenu(ShopMenuView shopMenuView) {
        super(MenuType.GreenHouse);
        this.shopMenuView = shopMenuView;
    }

    public void buyItem(){

    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return shopMenuView;
    }
}
