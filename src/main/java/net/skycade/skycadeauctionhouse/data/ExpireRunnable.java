package net.skycade.skycadeauctionhouse.data;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static net.skycade.skycadeauctionhouse.util.Messages.AUCTION_EXPIRED;
import static net.skycade.skycadeauctionhouse.util.Messages.CHECK_EXPIRED;

public class ExpireRunnable extends BukkitRunnable {

    private AuctionsManager auctionsManager;

    // Ricky's amazing variable name
    private List<Integer> listOfAuctionIdsWhoseOwnersIHaveAlreadyNotifiedOfTheRelativeExpiryAndWontMessageAgain = new ArrayList<>();

    public ExpireRunnable(AuctionsManager auctionsManager) {
        this.auctionsManager = auctionsManager;
    }

    @Override
    public void run() {
        for (Auction auction : auctionsManager.getAllAuctions()) {
            // only bother if the auction is expired and player hasn't been told
            if (!auction.isActive() &&
                    !listOfAuctionIdsWhoseOwnersIHaveAlreadyNotifiedOfTheRelativeExpiryAndWontMessageAgain.contains(auction.getAuctionId())) {
                // message player if online, or send event to skyblock in case theyre online
                if (Bukkit.getOfflinePlayer(auction.getAuctionedBy()) != null
                        && Bukkit.getOfflinePlayer(auction.getAuctionedBy()).isOnline()
                        && Bukkit.getPlayer(auction.getAuctionedBy()) != null) {
                    AUCTION_EXPIRED.msg(Bukkit.getPlayer(auction.getAuctionedBy()),
                            "%amount%", Integer.toString(auction.getItemStack().getAmount()),
                            "%item%", auction.getItemStack().hasItemMeta() ?
                                    auction.getItemStack().getItemMeta().getDisplayName() :
                                    auction.getItemStack().getType().name());
                    CHECK_EXPIRED.msg(Bukkit.getPlayer(auction.getAuctionedBy()));
                }

                // dont message about this auction again
                listOfAuctionIdsWhoseOwnersIHaveAlreadyNotifiedOfTheRelativeExpiryAndWontMessageAgain.add(auction.getAuctionId());
            }
        }
    }
}
