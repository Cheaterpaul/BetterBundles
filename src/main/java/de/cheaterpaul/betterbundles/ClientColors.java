package de.cheaterpaul.betterbundles;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Collection;

@OnlyIn(Dist.CLIENT)
public class ClientColors {

    public static void registerMethod(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientColors::registerColors);
        Collection<Item> items = Lists.newArrayList(BetterBundlesMod.BUNDLE_COPPER,BetterBundlesMod.BUNDLE_IRON,BetterBundlesMod.BUNDLE_SILVER,BetterBundlesMod.BUNDLE_GOLD,BetterBundlesMod.BUNDLE_DIAMOND,BetterBundlesMod.BUNDLE_NETHERITE);
        items.forEach(item -> ItemProperties.register(item, new ResourceLocation("filled"), (p_174625_, p_174626_, p_174627_, p_174628_) -> BundleItem.getFullnessDisplay(p_174625_)));
    }

    private static void registerColors(FMLClientSetupEvent event) {
        Minecraft.getInstance().getItemColors().register((stack, layer) -> {
            return layer > 0 ? -1 : ((DyeableLeatherItem) stack.getItem()).hasCustomColor(stack)?((DyeableLeatherItem) stack.getItem()).getColor(stack):0xff7246;
        }, BetterBundlesMod.BUNDLE);
    }
}
