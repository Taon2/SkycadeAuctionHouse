package net.skycade.skycadeauctionhouse.data;

import net.skycade.skycadeauctionhouse.SkycadeAuctionHousePlugin;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Auction {
    private int auctionId;
    private UUID auctionedBy;
    private long auctionedOn;
    private long unlistedOn;
    private ItemStack itemStack;
    private double cost;
    private boolean itemsClaimed;

    public Auction(UUID auctionedBy, ItemStack itemStack, double cost) {
        this.auctionId = ThreadLocalRandom.current().nextInt();
        this.auctionedBy = auctionedBy;
        this.auctionedOn = System.currentTimeMillis();
        this.unlistedOn = -1;
        this.itemStack = itemStack;
        this.cost = cost;
        this.itemsClaimed = false;
    }

    public Auction(int auctionId, UUID auctionedBy, long auctionedOn, long unlistedOn, ItemStack itemStack, double cost, boolean itemsClaimed) {
        this.auctionId = auctionId;
        this.auctionedBy = auctionedBy;
        this.auctionedOn = auctionedOn;
        this.unlistedOn = unlistedOn;
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

    public long getUnlistedOn() {
        return unlistedOn;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public double getCost() {
        return cost;
    }

    public boolean isActive() {
        return auctionedOn + Config.getListingDuration() > System.currentTimeMillis() && !areItemsReclaimable();
    }

    public boolean areItemsReclaimable() {
        return unlistedOn != -1 && auctionedOn + Config.getExpiredDuration() > System.currentTimeMillis();
    }

    public boolean areItemsClaimed() {
        return itemsClaimed;
    }

    public void unlist() {
        this.itemsClaimed = false;
        this.unlistedOn = System.currentTimeMillis();
        SkycadeAuctionHousePlugin.getInstance().getAuctionsManager().persistAuction(auctionId);
        SkycadeAuctionHousePlugin.getInstance().getAuctionsManager().unlistAuction(auctionId);
    }

    public void remove() {
        this.itemsClaimed = true;
        this.unlistedOn = System.currentTimeMillis();
        SkycadeAuctionHousePlugin.getInstance().getAuctionsManager().persistAuction(auctionId);
        SkycadeAuctionHousePlugin.getInstance().getAuctionsManager().unlistAuction(auctionId);
    }
}
