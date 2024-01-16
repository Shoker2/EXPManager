package org.ts2.expmanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class EXPManager extends JavaPlugin {
    public static EXPManager instance;
    @Override
    public void onEnable() {
        this.instance = this;
        File file = new File("plugins/EXPManager");
        file.mkdir();
        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(new Events(), this);

        SetMaxLevelCommand.init();
        AddToMaxLevelCommand.init();
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    public static String color(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> color(List<String> lore){
        List<String> clore = new ArrayList<>();
        for(String s : lore){
            clore.add(color(s));
        }
        return clore;
    }
}
