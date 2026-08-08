package com.test1.PlantsVsZombies.src.Enums;

public enum WalletType {
    COIN("coin"),
    DIAMOND("diamond");

    private final String name;

    WalletType(String name) {
        this.name = name;
    }

    public static WalletType getByName(String name) {
        for (WalletType walletType : values()) {
            if (walletType.name.equalsIgnoreCase(name)) {
                return walletType;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
}
