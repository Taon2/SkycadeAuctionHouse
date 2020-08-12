package net.skycade.skycadeauctionhouse.util;

import net.skycade.SkycadeCore.Localization;

public class Messages {
    public static Localization.Message NEED_PRICE = new Localization.Message("command.need-price", "&cYou must input a price. &b/ah sell &a<price>");
    public static Localization.Message MAX_PRICE = new Localization.Message("command.max-price", "&cMaximum item cost is $%cost%");
    public static Localization.Message INVALID_ITEM = new Localization.Message("command.invalid-item", "&cThat item cannot be sold.");
    public static Localization.Message MAX_AUCTIONS = new Localization.Message("command.max-auctions", "&cYou already have the maximum amount of auctions.");
    public static Localization.Message ITEM_LISTED = new Localization.Message("command.item-listed", "&6You listed &a%amount% &6of &a%item% &6for &a$%price%&6.");

    public static Localization.Message CANNOT_PURCHASE_OWN = new Localization.Message("purchase.cannot_purchase_own", "&cYou can not purchase your own item.");
    public static Localization.Message NO_INVENTORY_SPACE = new Localization.Message("purchase.no-inventory-space", "&cYou have no space for this item.");
    public static Localization.Message CANNOT_AFFORD = new Localization.Message("purchase.cannot-afford", "&cYou can not afford that item.");
    public static Localization.Message YOU_PURCHASED = new Localization.Message("purchase.you-purchased", "&6You bought &a%amount% &6of &a%item% &6for &a$%price% &6from &a%player%.");
    public static Localization.Message SOMEONE_PURCHASED = new Localization.Message("purchase.someone-purchased", "&a%player% &6purchased your auction of &a%amount% &a%item% &6for &a$%price%&6.");
    public static Localization.Message ITEM_DOES_NOT_EXIST = new Localization.Message("purchase.does-not-exist", "&cThat item is no longer being sold.");

    public static Localization.Message ITEM_REMOVED = new Localization.Message("auction.item-removed", "&6That item has been successfully removed.");
    public static Localization.Message CHECK_EXPIRED = new Localization.Message("auction.check-expired", "&eRetrieve cancelled and expired items from &a/ah expired&6.");
    public static Localization.Message ITEM_RETURNED = new Localization.Message("auction.item-returned", "&6That item has been returned.");

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
                ITEM_RETURNED
        );
    }
}
