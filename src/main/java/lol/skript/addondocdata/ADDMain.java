package lol.skript.addondocdata;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class ADDMain extends JavaPlugin {

    private static ADDMain instance;
    public static ADDMain getInstance() {
        return instance;
    }

    public static Logger logger() {
        return instance.getLogger();
    }


    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        this.getCommand("addondocdata").setExecutor(new ADDCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
