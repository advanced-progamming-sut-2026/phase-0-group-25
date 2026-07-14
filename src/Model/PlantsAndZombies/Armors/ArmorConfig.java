package src.Model.PlantsAndZombies.Armors;

public enum ArmorConfig {
    CONE("cone", 370, false),
    BUCKET("bucket", 1100, true),
    BRICK("brick", 2200, false),
    SHOULDER_ARMOR("shoulder armor", 1600, true),
    CROWN("crown", 1600, true),
    NEWSPAPER("newspaper", 800, false);

    private final String type;
    private final int baseHP;
    private final boolean isMetallic;

    ArmorConfig(String type, int baseHP, boolean isMetallic) {
        this.type = type;
        this.baseHP = baseHP;
        this.isMetallic = isMetallic;
    }

    public Armor createArmor() {
        return new Armor(this.type, this.baseHP, this.isMetallic);
    }
}
