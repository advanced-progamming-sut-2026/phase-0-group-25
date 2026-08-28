package com.test1.PlantsVsZombies.src.Network;


public class NetworkConfig {
    public static final String SERVER_HOST = System.getProperty("pvz.server.host", "localhost");
    public static final int SERVER_PORT = Integer.parseInt(System.getProperty("pvz.server.port", "5454"));

    private NetworkConfig() {
    }
}
