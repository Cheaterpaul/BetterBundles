package de.cheaterpaul.betterbundles;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ClientColors {

    public static void registerMethod(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientColors::registerColors);
    }

    private static void registerColors(FMLClientSetupEvent event) {
        Minecraft.getInstance().getItemColors().register((stack, layer) -> {
            return layer > 0 ? -1 : ((DyeableLeatherItem) stack.getItem()).hasCustomColor(stack)?((DyeableLeatherItem) stack.getItem()).getColor(stack):0xff7246;
        }, BetterBundlesMod.BUNDLE);
    }
}
