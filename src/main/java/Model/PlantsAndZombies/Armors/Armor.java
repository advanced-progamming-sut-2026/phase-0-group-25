package Model.PlantsAndZombies.Armors;

public class Armor {
    private String type;
    private int currentHP;
    private boolean isMetallic;

    public Armor(String type, int baseHP, boolean isMetallic) {
        this.type = type;
        this.currentHP = baseHP;
        this.isMetallic = isMetallic;
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


}
