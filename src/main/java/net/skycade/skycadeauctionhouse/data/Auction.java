package net.skycade.skycadeauctionhouse.data;

import net.skycade.skycadeauctionhouse.SkycadeAuctionHousePlugin;
import net.skycade.skycadeauctionhouse.event.AuctionRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Auction {
    private int auctionId;
    private UUID auctionedBy;
    private long auctionedOn;
    private long expiresOn;
    private ItemStack itemStack;
    private double cost;
    private boolean itemsClaimed;

    public Auction(UUID auctionedBy, ItemStack itemStack, double cost) {
        this.auctionId = ThreadLocalRandom.current().nextInt();
        this.auctionedBy = auctionedBy;
        this.auctionedOn = System.currentTimeMillis();
        this.expiresOn = System.currentTimeMillis() + Config.getListingDuration();
        this.itemStack = itemStack;
        this.cost = cost;
        this.itemsClaimed = false;
    }

    public Auction(int auctionId, UUID auctionedBy, long auctionedOn, long expiresOn, ItemStack itemStack, double cost, boolean itemsClaimed) {
        this.auctionId = auctionId;
        this.auctionedBy = auctionedBy;
        this.auctionedOn = auctionedOn;
        this.expiresOn = expiresOn;
        this.itemStack = itemStack;
        this.cost = cost;
        this.itemsClaimed = itemsClaimed;
    }

    public int getAuctionId() {
        return auctionId;
    }

    public UUID getAuctionedBy() {
        return auctionedBy;
    }

    public long getAuctionedOn() {
        return auctionedOn;
    }

    public long getExpiresOn() {
        return expiresOn;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public double getCost() {
        return cost;
    }

    public boolean isActive() {
        return auctionedOn + Config.getListingDuration() > System.currentTimeMillis() && !itemsClaimed;
    }

    public boolean areItemsClaimed() {
        return itemsClaimed;
    }

    public void remove() {
        this.itemsClaimed = true;
        SkycadeAuctionHousePlugin.getInstance().getAuctionsManager().persistAuction(auctionId);
        SkycadeAuctionHousePlugin.getInstance().getAuctionsManager().unlistAuction(auctionId);

        // for skyblock sync
        AuctionRemoveEvent removeEvent = new AuctionRemoveEvent(auctionId);
        Bukkit.getPluginManager().callEvent(removeEvent);
    }
}
