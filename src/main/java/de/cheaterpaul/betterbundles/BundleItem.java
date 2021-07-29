package de.cheaterpaul.betterbundles;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;

public class BundleItem extends net.minecraft.world.item.BundleItem implements DyeableLeatherItem {

    public BundleItem() {
        super(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS));
    }
}
