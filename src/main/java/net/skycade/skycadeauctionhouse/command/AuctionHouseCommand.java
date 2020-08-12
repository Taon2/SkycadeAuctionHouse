package net.skycade.skycadeauctionhouse.command;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.NoConsole;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import net.skycade.SkycadeCore.utility.command.addons.SubCommand;
import net.skycade.skycadeauctionhouse.SkycadeAuctionHousePlugin;
import net.skycade.skycadeauctionhouse.data.Auction;
import net.skycade.skycadeauctionhouse.data.Config;
import net.skycade.skycadeauctionhouse.gui.ListedAuctionsGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

import static net.skycade.SkycadeCore.Localization.Global.PLAYERS_ONLY;
import static net.skycade.skycadeauctionhouse.util.Messages.*;

public class AuctionHouseCommand extends SkycadeCommand {

    private SkycadeAuctionHousePlugin plugin;

    public AuctionHouseCommand(SkycadeAuctionHousePlugin plugin) {
        super("auctionhouse", Collections.singletonList("ah"));
        this.plugin = plugin;

        addSubCommands(
                new Sell()
        );
    }

    @NoConsole
    @Override
    @Permissible("auctionhouse.use")
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            PLAYERS_ONLY.msg(sender);
            return;
        }

        if (sender.hasPermission("auctionhouse.use")) {
            new ListedAuctionsGUI((Player) sender, 1).open((Player) sender);
        }
    }

    @SubCommand
    @NoConsole
    @Permissible("auctionhouse.sell")
    public class Sell extends SkycadeCommand {

        Sell() {
            super("sell");
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {
            if (args.length < 1) {
                NEED_PRICE.msg(sender);
                return;
            }

            double cost = Double.parseDouble(args[0]);

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

            if (plugin.getAuctionsManager().getActiveAuctions(player.getUniqueId()).size() >= Config.getMaxAuctions()) {
                MAX_AUCTIONS.msg(sender);
                return;
            }

            Bukkit.getLogger().info(itemStack.toString());
            Auction auction = new Auction(player.getUniqueId(), itemStack, cost);
            plugin.getAuctionsManager().addAuction(auction);
            player.getInventory().remove(itemStack);
            player.updateInventory();
            ITEM_LISTED.msg(player, "%amount%", Integer.toString(itemStack.getAmount()),
                    "%item%", itemStack.hasItemMeta() ?
                            itemStack.getItemMeta().getDisplayName() :
                            itemStack.getType().name(),
                    "%price%", Double.toString(cost));
        }
    }
}
