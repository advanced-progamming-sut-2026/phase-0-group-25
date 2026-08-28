package com.test1.PlantsVsZombies.src.Network;

/**
 * Where the client looks for the server, and what port GameServer binds
 * to. Defaults to running both on the same machine (the normal case for
 * this project), but either can be overridden without recompiling, e.g.
 * -Dpvz.server.host=192.168.1.20 -Dpvz.server.port=5454
 */
public class NetworkConfig {
    public static final String SERVER_HOST = System.getProperty("pvz.server.host", "localhost");
    public static final int SERVER_PORT = Integer.parseInt(System.getProperty("pvz.server.port", "5454"));

    private NetworkConfig() {
    }
}
