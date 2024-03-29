package net.skycade.skycadeauctionhouse.command;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.NoConsole;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import net.skycade.SkycadeCore.utility.command.addons.SubCommand;
import net.skycade.skycadeauctionhouse.SkycadeAuctionHousePlugin;
import net.skycade.skycadeauctionhouse.data.Auction;
import net.skycade.skycadeauctionhouse.data.Config;
import net.skycade.skycadeauctionhouse.event.AuctionCreateEvent;
import net.skycade.skycadeauctionhouse.gui.ListedAuctionsGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

import static net.skycade.skycadeauctionhouse.util.Messages.*;

@NoConsole
@Permissible("auctionhouse.use")
public class AuctionHouseCommand extends SkycadeCommand {

    private SkycadeAuctionHousePlugin plugin;

    public AuctionHouseCommand(SkycadeAuctionHousePlugin plugin) {
        super("auctionhouse", Collections.singletonList("ah"));
        this.plugin = plugin;

        addSubCommands(
                new Sell()
        );
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        new ListedAuctionsGUI((Player) sender, 1).open((Player) sender);
    }

    @SubCommand
    @NoConsole
    @Permissible("auctionhouse.sell")
    public class Sell extends SkycadeCommand {

        Sell() {
            super("sell");
        }

        @Override
        @SuppressWarnings("deprecation") // ricky you told me to put this here dont you dare get mad
        public void onCommand(CommandSender sender, String[] args) {
            if (args.length < 1) {
                NEED_PRICE.msg(sender);
                return;
            }

            double cost;
            try {
                cost = Double.parseDouble(args[0]);
            } catch(NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Please enter a number.");
                return;
            }

            if (cost > Config.getMaxSellPrice()) {
                MAX_PRICE.msg(sender, "%cost%", Double.toString(Config.getMaxSellPrice()));
                return;
            }

            Player player = (Player) sender;

            if (player.getItemInHand() == null
                    || Config.getBlacklistedMaterials().contains(player.getItemInHand().getType())) {
                INVALID_ITEM.msg(sender);
                return;
            }

            ItemStack itemStack = player.getItemInHand();

            if (plugin.getAuctionsManager().getPlayerAuctions(player.getUniqueId()).size() >= Config.getMaxAuctions()) {
                MAX_AUCTIONS.msg(sender);
                return;
            }

            Auction auction = new Auction(player.getUniqueId(), itemStack, cost);
            plugin.getAuctionsManager().createAuction(auction);
            player.getInventory().setItemInHand(new ItemStack(Material.AIR));
            player.updateInventory();
            ITEM_LISTED.msg(player, "%amount%", Integer.toString(itemStack.getAmount()),
                    "%item%", itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() ?
                            itemStack.getItemMeta().getDisplayName() :
                            itemStack.getType().name(),
                    "%price%", Double.toString(cost));

            // for skyblock sync
            AuctionCreateEvent createEvent = new AuctionCreateEvent(auction);
            Bukkit.getPluginManager().callEvent(createEvent);
        }
    }
}
