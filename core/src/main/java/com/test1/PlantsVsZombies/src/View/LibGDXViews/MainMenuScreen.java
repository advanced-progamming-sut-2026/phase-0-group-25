package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Menu.MainMenu;
import com.test1.PlantsVsZombies.src.Menu.MenuManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.MainMenuView;

public class MainMenuScreen extends AbstractScreen implements MainMenuView {
    private MainMenu menuController;

    public MainMenuScreen() {
    }

    public void setMenuController(MainMenu menuController) {
        this.menuController = menuController;
    }

    @Override
    public void show() {
        super.show();

        Label title = new Label("MAIN MENU", skin);
        TextButton logoutButton = new TextButton("Logout", skin);

        logoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuManager.getInstance().changeMenu(MenuType.Signup);
            }
        });

        rootTable.add(title).padBottom(20).row();
        rootTable.add(logoutButton);
    }

    @Override public void showError(String error) {}
    @Override public void showCurrentMenu() {}
}
