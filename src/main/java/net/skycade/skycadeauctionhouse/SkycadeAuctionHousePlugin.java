package net.skycade.skycadeauctionhouse;

import net.milkbowl.vault.economy.Economy;
import net.skycade.SkycadeCore.SkycadePlugin;
import net.skycade.skycadeauctionhouse.command.AuctionHouseCommand;
import net.skycade.skycadeauctionhouse.data.AuctionsManager;
import net.skycade.skycadeauctionhouse.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class SkycadeAuctionHousePlugin extends SkycadePlugin {

    public static boolean v18;

    private static SkycadeAuctionHousePlugin instance;

    private AuctionsManager auctionsManager;

    public static SkycadeAuctionHousePlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        defaults();
        instance = this;

        v18 = Bukkit.getServer().getClass().getPackage().getName().contains("1_8");

        auctionsManager = new AuctionsManager();

        new AuctionHouseCommand(this);

        Messages.init();
    }

    @Override
    public void onDisable() {

    }

    public Economy getEconomy() {
        RegisteredServiceProvider<Economy> registration = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (registration == null) throw new RuntimeException("vault not loaded");
        return registration.getProvider();
    }

    public AuctionsManager getAuctionsManager() {
        return auctionsManager;
    }

    private void defaults() {
        Map<String, Object> defaults = new TreeMap<>();

        defaults.put("maxSellPrice", 100000000);
        defaults.put("maxAuctions", 4);
        defaults.put("blacklistedMaterials",
                Arrays.asList(
                        Material.BARRIER.toString(),
                        Material.BEDROCK.toString(),
                        Material.COMMAND.toString(),
                        Material.AIR.toString()));
        defaults.put("listingDuration", 604800000); // 7d
        defaults.put("expiredDuration", 1209600000); // 14d

        setConfigDefaults(defaults);
        loadDefaultConfig();
    }
}
