package Menu;

import Enums.MenuType;
import View.ViewInterfaces.BaseView;
import View.ViewInterfaces.ShopMenuView;

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
