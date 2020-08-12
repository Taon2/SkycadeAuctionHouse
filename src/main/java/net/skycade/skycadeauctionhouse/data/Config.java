package net.skycade.skycadeauctionhouse.data;

import net.skycade.skycadeauctionhouse.SkycadeAuctionHousePlugin;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public static List<Material> getBlacklistedMaterials() {
        List<Material> blacklist = new ArrayList<>();
        for (String s : SkycadeAuctionHousePlugin.getInstance().getConfig().getStringList("blacklistedMaterials")) {
            blacklist.add(Material.valueOf(s));
        }

        return blacklist;
    }

    public static double getMaxSellPrice() {
        return SkycadeAuctionHousePlugin.getInstance().getConfig().getDouble("maxSellPrice");
    }

    public static int getMaxAuctions() {
        return SkycadeAuctionHousePlugin.getInstance().getConfig().getInt("maxAuctions");
    }

    public static int getListingDuration() {
        return SkycadeAuctionHousePlugin.getInstance().getConfig().getInt("listingDuration");
    }

    public static int getExpiredDuration() {
        return SkycadeAuctionHousePlugin.getInstance().getConfig().getInt("expiredDuration");
    }
}
