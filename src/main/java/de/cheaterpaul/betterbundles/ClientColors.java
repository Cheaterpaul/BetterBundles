package de.cheaterpaul.betterbundles;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.DyeableLeatherItem;

import java.awt.*;

public class ClientColors {

    public static void registerColors() {
        Minecraft.getInstance().getItemColors().register((stack, layer) -> {
            return layer > 0 ? -1 : ((DyeableLeatherItem) stack.getItem()).hasCustomColor(stack)?((DyeableLeatherItem) stack.getItem()).getColor(stack):0xff7246;
        }, BetterBundlesMod.BUNDLE);
    }
}
