package net.skycade.skycadeauctionhouse.gui;

import net.md_5.bungee.api.ChatColor;
import net.skycade.SkycadeCore.guis.dynamicnew.DynamicGui;
import net.skycade.SkycadeCore.utility.CoreUtil;
import net.skycade.SkycadeCore.utility.ItemBuilder;
import net.skycade.SkycadeCore.utility.MojangUtil;
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

public class ListedAuctionsGUI extends DynamicGui {

    private static final ItemStack BACK = new ItemBuilder(Material.ARROW)
            .setDisplayName(ChatColor.GOLD + "Go Back")
            .build();
    private static final ItemStack NEXT = new ItemBuilder(Material.ARROW)
            .setDisplayName(ChatColor.GOLD + "Next")
            .build();

    public ListedAuctionsGUI(Player player, int page) {
        super(ChatColor.RED + "" + ChatColor.BOLD + "Auction House - Page " + page, 6);
        AuctionsManager auctionsManager = SkycadeAuctionHousePlugin.getInstance().getAuctionsManager();

        List<Auction> activeAuctions = auctionsManager.getActiveAuctions();
        activeAuctions.sort(Comparator.comparing(Auction::getAuctionedOn).reversed());

        activeAuctions.stream()
                .skip((page - 1) * 45)
                .limit(45)
                .forEach(auction -> addItemInteraction(p -> {
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

                            if (SkycadeAuctionHousePlugin.getInstance().getEconomy().getBalance(player) < auction.getCost()) {
                                lore.add(ChatColor.GOLD + "You can not afford this item.");
                            } else {
                                lore.add(ChatColor.GREEN + "Click here to purchase.");
                            }

                            lore.add("");
                            lore.add(ChatColor.BLUE + "Price: "  + ChatColor.GOLD + "$" + auction.getCost());
                            lore.add(ChatColor.BLUE + "Seller: " + ChatColor.GOLD + MojangUtil.get(auction.getAuctionedBy()).getName());
                            lore.add(ChatColor.BLUE + "Expire: " + ChatColor.GOLD + CoreUtil.niceFormat( (int)
                                    ((auction.getAuctionedOn() + Config.getListingDuration()) - System.currentTimeMillis()) / 1000, true));
                            lore.add("");
                            if (player.hasPermission("auctionhouse.cancel.others")) {
                                lore.add(ChatColor.RED + "Shift + Right Click to cancel.");
                            }
                            lore.add(ChatColor.GRAY + "------------------------------");

                            meta.setLore(lore);
                            item.setItemMeta(meta);

                            return item;
                        },
                        (p, ev) -> {
                            new ConfirmGUI(auction, p).open(p);
                        }));

        if (page > 1) {
            setItemInteraction(45, new ItemBuilder(BACK).build(),
                    (p, ev) -> {
                        new ListedAuctionsGUI(player, page -1).open(p);
                    });
        }

        if (activeAuctions.size() > page * 45) {
            setItemInteraction(53, new ItemBuilder(NEXT).build(),
                    (p, ev) -> {
                        new ListedAuctionsGUI(player, page + 1).open(p);
                    });
        }
    }
}
