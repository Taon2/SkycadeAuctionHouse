package net.skycade.skycadeauctionhouse.data;

import net.skycade.skycadeauctionhouse.SkycadeAuctionHousePlugin;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Auction {
    private int auctionId;
    private UUID auctionedBy;
    private long auctionedOn;
    private ItemStack itemStack;
    private double cost;
    private boolean itemsClaimed;

    public Auction(UUID auctionedBy, ItemStack itemStack, double cost) {
        this.auctionId = ThreadLocalRandom.current().nextInt();
        this.auctionedBy = auctionedBy;
        this.auctionedOn = System.currentTimeMillis();
        this.itemStack = itemStack;
        this.cost = cost;
        this.itemsClaimed = false;
    }

    public Auction(int auctionId, UUID auctionedBy, long auctionedOn, ItemStack itemStack, double cost, boolean itemsClaimed) {
        this.auctionId = auctionId;
        this.auctionedBy = auctionedBy;
        this.auctionedOn = auctionedOn;
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

    public ItemStack getItemStack() {
        return itemStack;
    }

    public double getCost() {
        return cost;
    }

    public boolean isActive() {
        return auctionedOn + Config.getListingDuration() > System.currentTimeMillis();
    }

    public boolean areItemsClaimed() {
        return itemsClaimed;
    }

    public void setAreItemsClaimed(boolean itemsClaimed) {
        this.itemsClaimed = itemsClaimed;
    }

    public void unlist() {
        this.itemsClaimed = false;
        SkycadeAuctionHousePlugin.getInstance().getAuctionsManager().persistAuction(this.getAuctionId());
        SkycadeAuctionHousePlugin.getInstance().getAuctionsManager().unlistAuction(this.getAuctionId());
    }

    public void remove() {
        this.itemsClaimed = true;
        SkycadeAuctionHousePlugin.getInstance().getAuctionsManager().persistAuction(this.getAuctionId());
        SkycadeAuctionHousePlugin.getInstance().getAuctionsManager().unlistAuction(this.getAuctionId());
    }
}
