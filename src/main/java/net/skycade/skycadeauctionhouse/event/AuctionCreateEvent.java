package net.skycade.skycadeauctionhouse.event;

import net.skycade.skycadeauctionhouse.data.Auction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AuctionCreateEvent extends Event {

    private static HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    private Auction auction;

    public AuctionCreateEvent(Auction auction) {
        this.auction = auction;
    }

    public Auction getAuction() {
        return auction;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
