package src.Model;

import src.Enums.GenderType;
import src.Model.ChaptersAndLevels.Chapter;

import java.util.ArrayList;

public class User {
    private String userName;
    private String nickName;
    private String password;
    private String email;
    private GenderType genderType;
    private ArrayList<Chapter> unlockedChapters;
    private ArrayList<src.Model.PlantsAndZombies.Zombie> unlockedZombies;
    private ArrayList<src.Model.PlantsAndZombies.Plant> unlockedPlants;
    private int gemsCount;
    private int coinsCount;

}
