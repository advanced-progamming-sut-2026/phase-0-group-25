package Menu;

import Enums.MenuType;
import View.ViewInterfaces.BaseView;
import View.ViewInterfaces.CollectionMenuView;

public class CollectionMenu extends Menu{
    private final CollectionMenuView collectionMenuView;

    public CollectionMenu(CollectionMenuView collectionMenuView) {
        super(MenuType.Game);
        this.collectionMenuView = collectionMenuView;
    }

    public void upgradePlant(){

    }
    public void purchasePlant(){

    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return collectionMenuView;
    }
}
