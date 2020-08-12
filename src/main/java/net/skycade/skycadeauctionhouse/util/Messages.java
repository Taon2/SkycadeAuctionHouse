package net.skycade.skycadeauctionhouse.util;

import net.skycade.SkycadeCore.Localization;
import net.skycade.SkycadeCore.Localization.Message;

public class Messages {
    public static Message NEED_PRICE = new Message("command.need-price", "&cYou must input a price. &b/ah sell &a<price>");
    public static Message MAX_PRICE = new Message("command.max-price", "&cMaximum item cost is $%cost%");
    public static Message INVALID_ITEM = new Message("command.invalid-item", "&cThat item cannot be sold.");
    public static Message MAX_AUCTIONS = new Message("command.max-auctions", "&cYou already have the maximum amount of auctions.");
    public static Message ITEM_LISTED = new Message("command.item-listed", "&6You listed &a%amount% &6of &a%item% &6for &a$%price%&6.");

    public static Message CANNOT_PURCHASE_OWN = new Message("purchase.cannot_purchase_own", "&cYou can not purchase your own item.");
    public static Message NO_INVENTORY_SPACE = new Message("purchase.no-inventory-space", "&cYou have no space for this item.");
    public static Message CANNOT_AFFORD = new Message("purchase.cannot-afford", "&cYou can not afford that item.");
    public static Message YOU_PURCHASED = new Message("purchase.you-purchased", "&6You bought &a%amount% &6of &a%item% &6for &a$%price% &6from &a%player%.");
    public static Message SOMEONE_PURCHASED = new Message("purchase.someone-purchased", "&a%player% &6purchased your auction of &a%amount% &a%item% &6for &a$%price%&6.");
    public static Message SOMEONE_REMOVED = new Message("purchase.someone-removed", "&a%player% &6removed your auction of &a%amount% &a%item%&6.");
    public static Message ITEM_DOES_NOT_EXIST = new Message("purchase.does-not-exist", "&cThat item is no longer being sold.");

    public static Message AUCTION_EXPIRED = new Message("auction.auction-expired", "&6Your auction of &a%amount% &a%item%&6 has expired.");
    public static Message ITEM_REMOVED = new Message("auction.item-removed", "&6That item has been successfully removed.");
    public static Message CHECK_EXPIRED = new Message("auction.check-expired", "&eRetrieve cancelled and expired items from &a/ah expired&6.");
    public static Message ITEM_RETURNED = new Message("auction.item-returned", "&6That item has been returned.");

    public static void init() {
        Localization.getInstance().registerMessages("skycade.auction-house",
                NEED_PRICE,
                MAX_PRICE,
                INVALID_ITEM,
                MAX_AUCTIONS,
                CANNOT_AFFORD,
                YOU_PURCHASED,
                NO_INVENTORY_SPACE,
                ITEM_DOES_NOT_EXIST,
                ITEM_LISTED,
                CANNOT_PURCHASE_OWN,
                ITEM_REMOVED,
                CHECK_EXPIRED,
                ITEM_RETURNED,
                SOMEONE_REMOVED,
                AUCTION_EXPIRED
        );
    }
}
