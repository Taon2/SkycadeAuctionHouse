package net.skycade.skycadeauctionhouse.gui;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.skycade.SkycadeCore.guis.dynamicnew.DynamicGui;
import net.skycade.SkycadeCore.utility.ItemBuilder;
import net.skycade.SkycadeCore.utility.MojangUtil;
import net.skycade.skycadeauctionhouse.SkycadeAuctionHousePlugin;
import net.skycade.skycadeauctionhouse.data.Auction;
import net.skycade.skycadeauctionhouse.event.AuctionMessagePlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

import static net.skycade.skycadeauctionhouse.SkycadeAuctionHousePlugin.v18;
import static net.skycade.skycadeauctionhouse.util.Messages.*;

class ConfirmGUI extends DynamicGui {

    private static final ItemStack CONFIRM = new ItemBuilder(Material.STAINED_GLASS)
            .setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Confirm Purchase")
            .setData((byte) 5)
            .build();
    private static final ItemStack DECLINE = new ItemBuilder(Material.STAINED_GLASS)
            .setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Decline Purchase")
            .setData((byte) 14)
            .build();

    private final DecimalFormat df = new DecimalFormat("###,###,###,###.##");

    ConfirmGUI(Auction auction, Player player) {
        super(ChatColor.RED + "" + ChatColor.BOLD + "Confirm Purchase", 3);

        setItemInteraction(11, new ItemBuilder(CONFIRM).build(),
                (p, ev) -> {
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

                    boolean unlisted = SkycadeAuctionHousePlugin.getInstance().getAuctionsManager().unlistAuction(auction.getAuctionId());

                    if (unlisted) {
                        YOU_PURCHASED.msg(p,
                                "%amount%", Integer.toString(auction.getItemStack().getAmount()),
                                "%item%", auction.getItemStack().hasItemMeta() ?
                                        auction.getItemStack().getItemMeta().hasDisplayName() ?
                                                auction.getItemStack().getItemMeta().getDisplayName() :
                                        auction.getItemStack().getType().name() : auction.getItemStack().getType().name(),
                                "%price%", df.format(auction.getCost()),
                                "%player%", MojangUtil.get(auction.getAuctionedBy()).getName());

                        if (Bukkit.getOfflinePlayer(auction.getAuctionedBy()).isOnline()) {
                            SOMEONE_PURCHASED.msg(Bukkit.getPlayer(auction.getAuctionedBy()),
                                    "%player%", p.getName(),
                                    "%amount%", Integer.toString(auction.getItemStack().getAmount()),
                                    "%item%", auction.getItemStack().hasItemMeta() ?
                                            auction.getItemStack().getItemMeta().getDisplayName() :
                                            auction.getItemStack().getType().name(),
                                    "%price%", df.format(auction.getCost()));
                        } else {
                            // for skyblock messaging
                            AuctionMessagePlayerEvent messagePlayerEvent = new AuctionMessagePlayerEvent(auction.getAuctionedBy(),
                                    SOMEONE_PURCHASED.getMessage().replace("%player%", p.getName())
                                            .replace("%amount%", Integer.toString(auction.getItemStack().getAmount()))
                                            .replace("%item%", auction.getItemStack().hasItemMeta() ?
                                                    auction.getItemStack().getItemMeta().getDisplayName() :
                                                    auction.getItemStack().getType().name())
                                            .replace("%price%", df.format(auction.getCost())));
                            Bukkit.getPluginManager().callEvent(messagePlayerEvent);
                        }

                        if (v18)
                            p.playSound(p.getLocation(), Sound.valueOf("ORB_PICKUP"), 1f, 1f);
                        else
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

                        p.getInventory().addItem(auction.getItemStack());
                        SkycadeAuctionHousePlugin.getInstance().getEconomy().withdrawPlayer(p, auction.getCost());
                        SkycadeAuctionHousePlugin.getInstance().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(auction.getAuctionedBy()), auction.getCost());
                    } else {
                        ITEM_DOES_NOT_EXIST.msg(p);
                        if (v18)
                            p.playSound(p.getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1f, 1f);
                        else
                            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1f, 1f);
                        auction.remove();
                        new ListedAuctionsGUI(player, 1).open(p);
                        return;
                    }

                    auction.remove();

                    new ListedAuctionsGUI(player, 1).open(p);
                });

        setItemInteraction(15, new ItemBuilder(DECLINE).build(),
                (p, ev) -> {
                    new ListedAuctionsGUI(player, 1).open(p);
                });
    }
}
