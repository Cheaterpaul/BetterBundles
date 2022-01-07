package de.cheaterpaul.betterbundles;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod("betterbundles")
public class BetterBundlesMod
{
    public static final String MODID = "betterbundles";
    public static final Item BUNDLE = new SizedBundleItem(pt(), 64).setRegistryName("minecraft","bundle");
    public static final Item BUNDLE_COPPER = new SizedBundleItem(pt(),96).setRegistryName(MODID,"copper_bundle");
    public static final Item BUNDLE_IRON = new SizedBundleItem(pt(),128).setRegistryName(MODID,"iron_bundle");
    public static final Item BUNDLE_SILVER = new SizedBundleItem(pt(),192).setRegistryName(MODID,"silver_bundle");
    public static final Item BUNDLE_GOLD = new SizedBundleItem(pt(),256).setRegistryName(MODID,"gold_bundle");
    public static final Item BUNDLE_DIAMOND = new SizedBundleItem(pt(),512).setRegistryName(MODID,"diamond_bundle");
    public static final Item BUNDLE_NETHERITE = new SizedBundleItem(pt().fireResistant(),512).setRegistryName(MODID,"netherite_bundle");
    public static final RecipeType<NBTShapedRecipe> RECIPE = RecipeType.register(MODID + ":nbt_bundle_recipe");
    public static final RecipeSerializer<?> RECIPE_SERIALIZER = new NBTShapedRecipe.Serializer().setRegistryName(MODID, "nbt_bundle_recipe");
    public static final Tag<Item> COPPER_TAG = ItemTags.createOptional(new ResourceLocation("forge", "ingots/copper"));
    public static final Tag<Item> SILVER_TAG = ItemTags.createOptional(new ResourceLocation("forge", "ingots/silver"));
    public static final Tag<Item> BUNDLE_TAG = ItemTags.createOptional(new ResourceLocation("forge", "bundles"));

    private static Item.Properties pt() {
        return new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS);
    }

    public BetterBundlesMod() {
        MinecraftForge.EVENT_BUS.register(this);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientColors::registerMethod);
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            event.getRegistry().register(BUNDLE);
            event.getRegistry().register(BUNDLE_COPPER);
            event.getRegistry().register(BUNDLE_IRON);
            event.getRegistry().register(BUNDLE_SILVER);
            event.getRegistry().register(BUNDLE_GOLD);
            event.getRegistry().register(BUNDLE_DIAMOND);
            event.getRegistry().register(BUNDLE_NETHERITE);
        }

        @SubscribeEvent
        public static void onRecipeSerialerRegistry(final RegistryEvent.Register<RecipeSerializer<?>> event) {
            event.getRegistry().register(RECIPE_SERIALIZER);
        }
    }
}
