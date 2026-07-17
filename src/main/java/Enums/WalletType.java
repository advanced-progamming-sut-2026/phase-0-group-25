package Enums;

public enum WalletType {
    COIN("coin"),
    DIAMOND("diamond");

    private final String name;

    WalletType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static WalletType getByName(String name) {
        for (WalletType walletType : values()) {
            if (walletType.name.equalsIgnoreCase(name)) {
                return walletType;
            }
        }
        return null;
    }
}
