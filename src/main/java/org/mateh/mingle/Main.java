package org.mateh.mingle;

import org.bukkit.plugin.java.JavaPlugin;
import org.mateh.mingle.managers.GameManager;

public final class Main extends JavaPlugin {

    private GameManager gameManager;

    @Override
    public void onEnable() {
        gameManager = new GameManager(this);
        getCommand("mingle").setExecutor(gameManager);
    }

    @Override
    public void onDisable() {
    }
}
