package net.skycade.skycadeauctionhouse.gui;

import net.md_5.bungee.api.ChatColor;
import net.skycade.SkycadeCore.guis.dynamicnew.DynamicGui;
import net.skycade.SkycadeCore.utility.CoreUtil;
import net.skycade.SkycadeCore.utility.ItemBuilder;
import net.skycade.skycadeauctionhouse.SkycadeAuctionHousePlugin;
import net.skycade.skycadeauctionhouse.data.Auction;
import net.skycade.skycadeauctionhouse.data.AuctionsManager;
import net.skycade.skycadeauctionhouse.data.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.skycade.skycadeauctionhouse.util.Messages.CHECK_EXPIRED;
import static net.skycade.skycadeauctionhouse.util.Messages.ITEM_REMOVED;

public class YourListingsGUI extends DynamicGui {

    private static final ItemStack BACK = new ItemBuilder(Material.ARROW)
            .setDisplayName(ChatColor.GOLD + "Go Back")
            .build();
    private static final ItemStack YOUR_EXPIRED = new ItemBuilder(Material.BUCKET)
            .setDisplayName(ChatColor.GOLD + "Cancelled / Expired Listings")
            .addToLore(ChatColor.GREEN + "Click here to view and retrieve any")
            .addToLore(ChatColor.GREEN + "items you have cancelled or that have expired.")
            .build();
    private static final ItemStack WHAT_IS_THIS_PAGE = new ItemBuilder(Material.BOOK)
            .setDisplayName(ChatColor.GOLD + "What Is This Page?")
            .addToLore(ChatColor.GREEN + "These are your current listings, all of")
            .addToLore(ChatColor.GREEN + "the items you currently have listed on")
            .addToLore(ChatColor.GREEN + "the auction house are displayed here.")
            .addToLore(ChatColor.GREEN + "You can cancel and view your listing's")
            .addToLore(ChatColor.GREEN + "expire time here.")
            .build();

    public YourListingsGUI(Player player) {
        super(ChatColor.RED + "" + ChatColor.BOLD + "Your Listings", 6);

        AuctionsManager auctionsManager = SkycadeAuctionHousePlugin.getInstance().getAuctionsManager();

        List<Auction> activeAuctions = auctionsManager.getActiveAuctions(player.getUniqueId());
        activeAuctions.sort(Comparator.comparing(Auction::getAuctionedOn).reversed());

        activeAuctions.forEach(auction -> addItemInteraction(p -> {
                    ItemStack item = auction.getItemStack().clone();
                    ItemMeta meta;

                    if (item.hasItemMeta())
                        meta = item.getItemMeta();
                    else
                        meta = Bukkit.getItemFactory().getItemMeta(item.getType());

                    List<String> lore = new ArrayList<>();
                    if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
                        lore.addAll(item.getItemMeta().getLore());
                    }

                    lore.add(ChatColor.GRAY + "------------------------------");
                    lore.add(ChatColor.RED + "Click here to cancel.");
                    lore.add("");
                    lore.add(ChatColor.BLUE + "Price: "  + ChatColor.GOLD + "$" + auction.getCost());
                    lore.add(ChatColor.BLUE + "Expire: " + ChatColor.GOLD + CoreUtil.niceFormat( (int)
                            ((auction.getAuctionedOn() + Config.getListingDuration()) - System.currentTimeMillis()) / 1000, true));
                    lore.add("");
                    lore.add(ChatColor.GRAY + "------------------------------");

                    meta.setLore(lore);
                    item.setItemMeta(meta);

                    return item;
                },
                (p, ev) -> {
                    if (p.hasPermission("auctionhouse.cancel")) {
                        auction.unlist();
                        ITEM_REMOVED.msg(p);
                        CHECK_EXPIRED.msg(p);
                        new YourListingsGUI(p).open(p);
                    }
                }));

        setItemInteraction(45, new ItemBuilder(BACK).build(),
                (p, ev) -> {
                    new ListedAuctionsGUI(player, 1).open(p);
                });

        setItemInteraction(46, new ItemBuilder(YOUR_EXPIRED)
                        .addToLore(auctionsManager.getExpiredUnclaimedAuctions(player.getUniqueId()).size() > 0 ?
                                ChatColor.BLUE + "Retrievable: " + ChatColor.GOLD + auctionsManager.getExpiredUnclaimedAuctions(player.getUniqueId()).size() :
                                ChatColor.RED + "You have no items to retrieve.").build(),
                (p, ev) -> {
                    new ExpiredListingsGUI(p, 1).open(p);
                });

        setItem(53, new ItemBuilder(WHAT_IS_THIS_PAGE).build());
    }
}
