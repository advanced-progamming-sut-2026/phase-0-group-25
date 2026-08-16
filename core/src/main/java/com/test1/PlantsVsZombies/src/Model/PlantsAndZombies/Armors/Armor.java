package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Armors;

import java.util.List;

public class Armor {
    private String type;
    private int baseHP;
    private int currentHP;
    private boolean isMetallic;
    private List<String> animations;

    public Armor(String type, int baseHP, boolean isMetallic, List<String> animations) {
        this.type = type;
        this.baseHP = baseHP;
        this.currentHP = baseHP;
        this.isMetallic = isMetallic;
        this.animations = animations;
    }


    public int takeDamage(int damage) {
        if (currentHP >= damage) {
            currentHP -= damage;
            return 0;
        } else {
            int leftoverDamage = damage - currentHP;
            stripArmor();
            return leftoverDamage;
        }
    }

    public void stripArmor() {
        this.currentHP = 0;
    }

    public boolean isDisarmed() {
        return (currentHP <= 0);
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public String getType() {
        return type;
    }

    public boolean isMetallic() {
        return isMetallic;
    }

    public List<String> getAnimations() {
        return animations;
    }

    public String getCurrentAnimation() {
        float HPRatio = (float) this.currentHP / this.baseHP;

        if (HPRatio >= 0.67) {
            return this.animations.get(0);
        } else if (HPRatio >= 0.33) {
            return this.animations.get(1);
        } else {
            return this.animations.get(2);
        }
    }

    public static Armor findArmor(String name) {
        for (ArmorConfig armor : ArmorConfig.values()) {
            if (armor.getType().equals(name)) {
                return armor.createArmor();
            }
        }
        return null;
    }


}
