package net.skycade.skycadeauctionhouse.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AuctionRemoveEvent extends Event {

    private static HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    private int auctionId;

    public AuctionRemoveEvent(int auctionId) {
        this.auctionId = auctionId;
    }

    public int getAuctionId() {
        return auctionId;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
