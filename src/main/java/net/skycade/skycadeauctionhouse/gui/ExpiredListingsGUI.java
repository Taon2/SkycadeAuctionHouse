package net.skycade.skycadeauctionhouse.gui;

import net.md_5.bungee.api.ChatColor;
import net.skycade.SkycadeCore.guis.dynamicnew.DynamicGui;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.skycade.skycadeauctionhouse.SkycadeAuctionHousePlugin.v18;
import static net.skycade.skycadeauctionhouse.util.Messages.ITEM_RETURNED;
import static net.skycade.skycadeauctionhouse.util.Messages.NO_INVENTORY_SPACE;

public class ExpiredListingsGUI extends DynamicGui {

    private static final ItemStack BACK = new ItemBuilder(Material.ARROW)
            .setDisplayName(ChatColor.GOLD + "Go Back")
            .build();
    private static final ItemStack NEXT = new ItemBuilder(Material.ARROW)
            .setDisplayName(ChatColor.GOLD + "Next")
            .build();
    private static final ItemStack WHAT_IS_THIS_PAGE = new ItemBuilder(Material.BOOK)
            .setDisplayName(ChatColor.GOLD + "What Is This Page?")
            .addToLore(ChatColor.GREEN + "This page shows all of your cancelled and")
            .addToLore(ChatColor.GREEN + "expired items. When a listing is cancelled")
            .addToLore(ChatColor.GREEN + "or expires you will be able to retrieve that")
            .addToLore(ChatColor.GREEN + "item from this menu.")
            .addToLore(ChatColor.GREEN + "Just click the item, and if you have enough")
            .addToLore(ChatColor.GREEN + "inventory space you will retrieve the item.")
            .build();

    public ExpiredListingsGUI(Player player, int page) {
        super(ChatColor.RED + "" + ChatColor.BOLD + "Cancelled/Expired Listings", 6);

        AuctionsManager auctionsManager = SkycadeAuctionHousePlugin.getInstance().getAuctionsManager();

        List<Auction> expiredUnclaimedAuctions = auctionsManager.getExpiredUnclaimedAuctions(player.getUniqueId());
        expiredUnclaimedAuctions.sort(Comparator.comparing(Auction::getAuctionedOn).reversed());

        expiredUnclaimedAuctions.stream()
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
                    lore.add(ChatColor.GREEN + "Click here to reclaim.");
                    lore.add(ChatColor.GRAY + "------------------------------");

                    meta.setLore(lore);
                    item.setItemMeta(meta);

                    return item;
                },
                (p, ev) -> {
                    if (player.getInventory().firstEmpty() == -1) {
                        NO_INVENTORY_SPACE.msg(p);
                        if (v18)
                            p.playSound(p.getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1f, 1f);
                        else
                            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1f, 1f);
                        return;
                    }

                    auction.remove();
                    ITEM_RETURNED.msg(p);
                    new ExpiredListingsGUI(p, 1).open(p);
                }));

        setItemInteraction(45, new ItemBuilder(BACK).build(),
                (p, ev) -> {
                    new ListedAuctionsGUI(player, 1).open(p);
                });

        if (page > 1) {
            setItemInteraction(48, new ItemBuilder(BACK).build(),
                    (p, ev) -> {
                        new ExpiredListingsGUI(player, page - 1).open(p);
                    });
        }

        if (expiredUnclaimedAuctions.size() > page * 45) {
            setItemInteraction(50, new ItemBuilder(NEXT).build(),
                    (p, ev) -> {
                        new ExpiredListingsGUI(player, page + 1).open(p);
                    });
        }

        setItem(53, new ItemBuilder(WHAT_IS_THIS_PAGE).build());
    }
}
