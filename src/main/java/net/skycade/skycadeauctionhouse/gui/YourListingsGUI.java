package net.skycade.skycadeauctionhouse.gui;

import net.md_5.bungee.api.ChatColor;
import net.skycade.SkycadeCore.guis.dynamicnew.DynamicGui;
import net.skycade.SkycadeCore.utility.CoreUtil;
import net.skycade.SkycadeCore.utility.ItemBuilder;
import net.skycade.skycadeauctionhouse.SkycadeAuctionHousePlugin;
import net.skycade.skycadeauctionhouse.data.Auction;
import net.skycade.skycadeauctionhouse.data.AuctionsManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.skycade.skycadeauctionhouse.SkycadeAuctionHousePlugin.v18;
import static net.skycade.skycadeauctionhouse.util.Messages.*;

public class YourListingsGUI extends DynamicGui {

    private static final ItemStack BACK = new ItemBuilder(Material.ARROW)
            .setDisplayName(ChatColor.GOLD + "Go Back")
            .build();
    private static final ItemStack WHAT_IS_THIS_PAGE = new ItemBuilder(Material.BOOK)
            .setDisplayName(ChatColor.GOLD + "What Is This Page?")
            .addToLore(ChatColor.GREEN + "These are your current listings, all of")
            .addToLore(ChatColor.GREEN + "the items you currently have listed on")
            .addToLore(ChatColor.GREEN + "the auction house are displayed here.")
            .addToLore(ChatColor.GREEN + "You can cancel and view your listing's")
            .addToLore(ChatColor.GREEN + "expire time here.")
            .build();

    private final DecimalFormat df = new DecimalFormat("###,###,###,###.##");

    public YourListingsGUI(Player player) {
        super(ChatColor.RED + "" + ChatColor.BOLD + "Your Listings", 6);

        AuctionsManager auctionsManager = SkycadeAuctionHousePlugin.getInstance().getAuctionsManager();

        List<Auction> playerAuctions = auctionsManager.getPlayerAuctions(player.getUniqueId());
        playerAuctions.sort(Comparator.comparing(Auction::getAuctionedOn).reversed());

        playerAuctions.forEach(auction -> addItemInteraction(p -> {
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
                    lore.add(ChatColor.RED + "Click here to cancel and reclaim this item.");
                    lore.add("");
                    lore.add(ChatColor.BLUE + "Price: "  + ChatColor.GOLD + "$" + df.format(auction.getCost()));
                    lore.add(ChatColor.BLUE + "Expire: " + (auction.isActive() ?
                                    ChatColor.GOLD + CoreUtil.niceFormat( (int)
                                    (auction.getExpiresOn() - System.currentTimeMillis()) / 1000, true)
                                    : ChatColor.RED + "Expired"));
                    lore.add("");
                    lore.add(ChatColor.GRAY + "------------------------------");

                    meta.setLore(lore);
                    item.setItemMeta(meta);

                    return item;
                },
                (p, ev) -> {
                    if (p.hasPermission("auctionhouse.cancel")) {
                        if (player.getInventory().firstEmpty() == -1) {
                            NO_INVENTORY_SPACE.msg(p);
                            if (v18)
                                p.playSound(p.getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1f, 1f);
                            else
                                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1f, 1f);
                            return;
                        }

                        boolean unlisted = SkycadeAuctionHousePlugin.getInstance().getAuctionsManager().unlistAuction(auction.getAuctionId());

                        if (unlisted) {
                            player.getInventory().addItem(auction.getItemStack());
                            player.updateInventory();
                            auction.remove();
                            ITEM_REMOVED.msg(p);
                        } else {
                            ITEM_DOES_NOT_EXIST.msg(p);
                            if (v18)
                                p.playSound(p.getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1f, 1f);
                            else
                                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1f, 1f);
                        }

                        new YourListingsGUI(p).open(p);
                    }
                }));

        setItemInteraction(45, new ItemBuilder(BACK).build(),
                (p, ev) -> {
                    new ListedAuctionsGUI(player, 1).open(p);
                });

        setItem(53, new ItemBuilder(WHAT_IS_THIS_PAGE).build());
    }
}
