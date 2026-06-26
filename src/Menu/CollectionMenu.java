package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.CollectionMenuView;

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
