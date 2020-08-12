package net.skycade.skycadeauctionhouse.data;

import net.skycade.skycadeauctionhouse.SkycadeAuctionHousePlugin;
import net.skycade.SkycadeCore.CoreSettings;
import net.skycade.SkycadeCore.utility.CoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class AuctionsManager {

    private Map<Integer, Auction> currentAuctions = new HashMap<>();

    public AuctionsManager() {
        loadAuctions();
    }

    public List<Auction> getActiveAuctions() {
        return currentAuctions.values().stream().filter(Auction::isActive).collect(Collectors.toList());
    }

    public Auction getAuction(int auctionId) {
        return currentAuctions.get(auctionId);
    }

    public List<Auction> getActiveAuctions(UUID uuid) {
        return currentAuctions.values().stream().filter(auction ->
                auction.isActive() &&
                auction.getAuctionedBy() == uuid).collect(Collectors.toList());
    }

    public List<Auction> getExpiredUnclaimedAuctions(UUID uuid) {
        return currentAuctions.values().stream().filter(auction ->
                !auction.isActive() &&
                !auction.areItemsClaimed() &&
                auction.getAuctionedBy() == uuid).collect(Collectors.toList());
    }

    public void addAuction(Auction auction) {
        currentAuctions.put(auction.getAuctionId(), auction);
        persistAuction(auction.getAuctionId());
    }

    private void loadAuctions() {
        Bukkit.getScheduler().runTaskAsynchronously(SkycadeAuctionHousePlugin.getInstance(), () -> {
            try (Connection connection = CoreSettings.getInstance().getConnection()) {
                String sql = "SELECT * FROM skycade_auctions WHERE " +
                        "areItemsReclaimed IS FALSE AND instance = ? AND season = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, CoreSettings.getInstance().getThisInstance());
                    statement.setString(2, CoreSettings.getInstance().getSeason());

                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        int auctionId = resultSet.getInt("auctionId");
                        UUID auctionedBy = UUID.fromString(resultSet.getString("auctionedBy"));
                        long auctionedOn = resultSet.getLong("auctionedOn");
                        ItemStack itemStack = CoreUtil.itemStackArrayFromBase64(resultSet.getString("itemStack"))[0];
                        double cost = resultSet.getDouble("cost");
                        boolean itemsReclaimed = resultSet.getBoolean("areItemsReclaimed");

                        currentAuctions.put(auctionId, new Auction(auctionId, auctionedBy, auctionedOn, itemStack, cost, itemsReclaimed));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void persistAuction(int auctionId) {
        Auction auction = getAuction(auctionId);

        Bukkit.getScheduler().runTaskAsynchronously(SkycadeAuctionHousePlugin.getInstance(), () -> {
            try (Connection connection = CoreSettings.getInstance().getConnection()) {
                String sql = "INSERT INTO skycade_auctions (`auctionId`, `auctionedBy`, `auctionedOn`, `itemStack`, " +
                        "`cost`, `areItemsReclaimed`, `instance`, `season`) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE areItemsReclaimed = VALUES(areItemsReclaimed)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, auction.getAuctionId());
                statement.setString(2, auction.getAuctionedBy().toString());
                statement.setLong(3, auction.getAuctionedOn());
                statement.setString(4, CoreUtil.itemStackArrayToBase64(new ItemStack[] {auction.getItemStack()}));
                statement.setDouble(5, auction.getCost());
                statement.setBoolean(6, auction.areItemsClaimed());
                statement.setString(7, CoreSettings.getInstance().getThisInstance());
                statement.setString(8, CoreSettings.getInstance().getSeason());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        if (!auction.isActive() && auction.areItemsClaimed()) {
            currentAuctions.remove(auctionId);
        }
    }

    public boolean unlistAuction(int auctionId) {
        try (Connection connection = CoreSettings.getInstance().getConnection()) {
            String sql = "INSERT INTO skycade_auctions_purchase_history (`auctionId`, `unlistedOn`, " +
                    "`instance`, `season`) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, auctionId);
            statement.setLong(2, System.currentTimeMillis());
            statement.setString(3, CoreSettings.getInstance().getThisInstance());
            statement.setString(4, CoreSettings.getInstance().getSeason());
            statement.executeUpdate();
        } catch (SQLException e) {
            return false;
        }

        return true;
    }
}
