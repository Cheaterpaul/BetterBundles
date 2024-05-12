package de.cheaterpaul.betterbundles;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class ClientColors {

    public static void registerMethod(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientColors::registerColors);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientColors::registerItemProperties);

    }

    private static void registerItemProperties(FMLClientSetupEvent event) {
        Stream.of(BetterBundlesMod.BUNDLE_COPPER,BetterBundlesMod.BUNDLE_IRON,BetterBundlesMod.BUNDLE_SILVER,BetterBundlesMod.BUNDLE_GOLD,BetterBundlesMod.BUNDLE_DIAMOND,BetterBundlesMod.BUNDLE_NETHERITE).map(RegistryObject::get).forEach(item-> {
            ItemProperties.register(item, new ResourceLocation("filled"), (p_174625_, p_174626_, p_174627_, p_174628_) -> BundleItem.getFullnessDisplay(p_174625_));
        });
    }

    private static void registerColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, layer) -> {
            return layer > 0 ? -1 : ((DyeableLeatherItem) stack.getItem()).hasCustomColor(stack)?((DyeableLeatherItem) stack.getItem()).getColor(stack):0xff7246;
        }, BetterBundlesMod.BUNDLE.get());
    }
}
