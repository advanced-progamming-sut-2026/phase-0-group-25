package Model;

import Enums.GenderType;

import java.util.ArrayList;

public class User {
    private String userName;
    private String nickName;
    private String password;
    private String email;
    private GenderType genderType;
    private ArrayList<Chapter> unlockedChapters;
    private ArrayList<Model.PlantsAndZombies.Zombie> unlockedZombies;
    private ArrayList<Model.PlantsAndZombies.Plant> unlockedPlants;
}
