package net.skycade.skycadeauctionhouse.gui;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.skycade.SkycadeCore.guis.dynamicnew.DynamicGui;
import net.skycade.SkycadeCore.utility.CoreUtil;
import net.skycade.SkycadeCore.utility.ItemBuilder;
import net.skycade.SkycadeCore.utility.MojangUtil;
import net.skycade.skycadeauctionhouse.SkycadeAuctionHousePlugin;
import net.skycade.skycadeauctionhouse.data.Auction;
import net.skycade.skycadeauctionhouse.data.AuctionsManager;
import net.skycade.skycadeauctionhouse.event.AuctionMessagePlayerEvent;
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
import static net.skycade.skycadeauctionhouse.util.Messages.*;

public class ListedAuctionsGUI extends DynamicGui {

    private static final ItemStack BACK = new ItemBuilder(Material.ARROW)
            .setDisplayName(ChatColor.GOLD + "Go Back")
            .build();
    private static final ItemStack NEXT = new ItemBuilder(Material.ARROW)
            .setDisplayName(ChatColor.GOLD + "Next")
            .build();
    private static final ItemStack YOUR_AUCTIONS = new ItemBuilder(Material.DIAMOND)
            .setDisplayName(ChatColor.GOLD + "Your Listeings")
            .addToLore(ChatColor.GREEN + "Click here to manage all of the items you")
            .addToLore(ChatColor.GREEN + "are currently selling on the auction house.")
            .build();
    private static final ItemStack HOW_TO_SELL = new ItemBuilder(Material.EMERALD)
            .setDisplayName(ChatColor.GOLD + "How To Sell An Item")
            .addToLore(ChatColor.GREEN + "To list an item on the auction house, just hold")
            .addToLore(ChatColor.GREEN + "the item in your hand and type " + ChatColor.AQUA + "/ah sell <price>" + ChatColor.GREEN + ".")
            .build();
    private static final ItemStack WHAT_IS_THIS_PAGE = new ItemBuilder(Material.BOOK)
            .setDisplayName(ChatColor.GOLD + "What Is This Page?")
            .addToLore(ChatColor.GREEN + "This is the auction house. Here you can")
            .addToLore(ChatColor.GREEN + "list items for sale, and purchase items")
            .addToLore(ChatColor.GREEN + "that others have listed for sale.")
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
                                    (auction.getExpiresOn() - System.currentTimeMillis()) / 1000, true));
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
                            if (ev.isShiftClick() && ev.isRightClick() && p.hasPermission("auctionhouse.cancel.others")) {
                                auction.remove();
                                ITEM_REMOVED.msg(p);
                                if (Bukkit.getOfflinePlayer(auction.getAuctionedBy()).isOnline()) {
                                    SOMEONE_REMOVED.msg(Bukkit.getPlayer(auction.getAuctionedBy()),
                                            "%player%", p.getName(),
                                            "%amount%", Integer.toString(auction.getItemStack().getAmount()),
                                            "%item%", auction.getItemStack().hasItemMeta() ?
                                                    auction.getItemStack().getItemMeta().getDisplayName() :
                                                    auction.getItemStack().getType().name(),
                                            "%price%", Double.toString(auction.getCost()));
                                } else {
                                    // for skyblock messaging
                                    AuctionMessagePlayerEvent messagePlayerEvent = new AuctionMessagePlayerEvent(auction.getAuctionedBy(),
                                            SOMEONE_REMOVED.getMessage().replace("%player%", p.getName())
                                                    .replace("%amount%", Integer.toString(auction.getItemStack().getAmount()))
                                                    .replace("%item%", auction.getItemStack().hasItemMeta() ?
                                                            auction.getItemStack().getItemMeta().getDisplayName() :
                                                            auction.getItemStack().getType().name())
                                                    .replace("%price%", Double.toString(auction.getCost())));
                                    Bukkit.getPluginManager().callEvent(messagePlayerEvent);
                                }
                                new ListedAuctionsGUI(player, page).open(p);
                                return;
                            }

                            // check if it's their own auction
                            if (auction.getAuctionedBy().equals(player.getUniqueId())) {
                                CANNOT_PURCHASE_OWN.msg(player);
                                if (v18)
                                    p.playSound(p.getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1f, 1f);
                                else
                                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1f, 1f);
                                return;
                            }

                            // check balance
                            Economy economy = SkycadeAuctionHousePlugin.getInstance().getEconomy();
                            if (economy.getBalance(player) < auction.getCost()) {
                                CANNOT_AFFORD.msg(p);
                                if (v18)
                                    p.playSound(p.getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1f, 1f);
                                else
                                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1f, 1f);
                                new ListedAuctionsGUI(player, 1).open(p);
                                return;
                            }

                            // check inventory space
                            if (player.getInventory().firstEmpty() == -1) {
                                NO_INVENTORY_SPACE.msg(p);
                                if (v18)
                                    p.playSound(p.getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1f, 1f);
                                else
                                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1f, 1f);
                                new ListedAuctionsGUI(player, 1).open(p);
                                return;
                            }

                            new ConfirmGUI(auction, p).open(p);
                        }));

        if (page > 1) {
            setItemInteraction(48, new ItemBuilder(BACK).build(),
                    (p, ev) -> {
                        new ListedAuctionsGUI(player, page - 1).open(p);
                    });
        }

        if (activeAuctions.size() > page * 45) {
            setItemInteraction(50, new ItemBuilder(NEXT).build(),
                    (p, ev) -> {
                        new ListedAuctionsGUI(player, page + 1).open(p);
                    });
        }

        setItemInteraction(45, new ItemBuilder(YOUR_AUCTIONS)
                        .addToLore(auctionsManager.getPlayerAuctions(player.getUniqueId()).size() > 0 ?
                                ChatColor.BLUE + "Listings: " + ChatColor.GOLD + auctionsManager.getPlayerAuctions(player.getUniqueId()).size() :
                                ChatColor.RED + "You have no items listed.").build(),
                (p, ev) -> {
                    new YourListingsGUI(p).open(p);
                });

        setItem(52, new ItemBuilder(HOW_TO_SELL).build());

        setItem(53, new ItemBuilder(WHAT_IS_THIS_PAGE).build());
    }
}
