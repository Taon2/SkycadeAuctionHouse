package net.skycade.skycadeauctionhouse.data;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Auction {
    private int auctionId;
    private UUID auctionedBy;
    private long auctionedOn;
    private ItemStack itemStack;
    private double cost;
    private boolean isActive;
    private boolean itemsClaimed;

    public Auction(UUID auctionedBy, ItemStack itemStack, double cost) {
        this.auctionId = ThreadLocalRandom.current().nextInt();
        this.auctionedBy = auctionedBy;
        this.auctionedOn = System.currentTimeMillis();
        this.itemStack = itemStack;
        this.cost = cost;
        this.isActive = true;
        this.itemsClaimed = false;
    }

    public Auction(int auctionId, UUID auctionedBy, long auctionedOn, ItemStack itemStack, double cost, boolean isActive, boolean itemsClaimed) {
        this.auctionId = auctionId;
        this.auctionedBy = auctionedBy;
        this.auctionedOn = auctionedOn;
        this.itemStack = itemStack;
        this.cost = cost;
        this.isActive = isActive;
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

    public boolean areItemsClaimed() {
        return itemsClaimed;
    }

    public void setAreItemsClaimed(boolean itemsClaimed) {
        this.itemsClaimed = itemsClaimed;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
