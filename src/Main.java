package src;

import src.Enums.GenderType;
import src.Menu.MenuManager;
import src.Model.PlantsAndZombies.GameDataLoader;
import src.Model.PlantsAndZombies.PlantStats;
import src.Model.User.User;
import src.Model.User.UsersManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
//        MenuManager menuManager = MenuManager.getInstance();
//        menuManager.startAppLoop();
        User user = new User("amir", "asdf", "asdfsa", "asdfasf", GenderType.Male);
        UsersManager usersManager = new UsersManager();
//        List<User> users = usersManager.readUsers();
//        users.add(user);
        usersManager.writeUsers();
//        User user = users.get(0);

//        users.add(user);
//        usersManager.writeUsers(users);


    }
}