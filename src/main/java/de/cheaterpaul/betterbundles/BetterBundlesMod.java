package de.cheaterpaul.betterbundles;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod("betterbundles")
public class BetterBundlesMod
{
    public static final String MODID = "betterbundles";
    public static Item BUNDLE;
    public static Item BUNDLE_COPPER;
    public static Item BUNDLE_IRON ;
    public static Item BUNDLE_SILVER ;
    public static Item BUNDLE_GOLD;
    public static Item BUNDLE_DIAMOND ;
    public static Item BUNDLE_NETHERITE ;
    public static RecipeType<NBTShapedRecipe> RECIPE;
    public static RecipeSerializer<?> RECIPE_SERIALIZER;
    public static final TagKey<Item> COPPER_TAG = ItemTags.create(new ResourceLocation("forge", "ingots/copper"));
    public static final TagKey<Item> SILVER_TAG = ItemTags.create(new ResourceLocation("forge", "ingots/silver"));
    public static final TagKey<Item> BUNDLE_TAG = ItemTags.create(new ResourceLocation("forge", "bundles"));
    public static final IConditionSerializer<?> CONFIG_CONDITION = CraftingHelper.register(new ConfigCondition.Serializer());


    private static Item.Properties pt() {
        return new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS);
    }

    public BetterBundlesMod() {
        MinecraftForge.EVENT_BUS.register(this);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientColors::registerMethod);
        Config.register();
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            event.getRegistry().register(BUNDLE = new SizedBundleItem(pt(), 64).setRegistryName("minecraft","bundle"));
            event.getRegistry().register(BUNDLE_COPPER = new SizedBundleItem(pt(),96).setRegistryName(MODID,"copper_bundle"));
            event.getRegistry().register(BUNDLE_IRON= new SizedBundleItem(pt(),128).setRegistryName(MODID,"iron_bundle"));
            event.getRegistry().register(BUNDLE_SILVER= new SizedBundleItem(pt(),192).setRegistryName(MODID,"silver_bundle"));
            event.getRegistry().register(BUNDLE_GOLD = new SizedBundleItem(pt(),256).setRegistryName(MODID,"gold_bundle"));
            event.getRegistry().register(BUNDLE_DIAMOND= new SizedBundleItem(pt(),512).setRegistryName(MODID,"diamond_bundle"));
            event.getRegistry().register(BUNDLE_NETHERITE= new SizedBundleItem(pt().fireResistant(),512).setRegistryName(MODID,"netherite_bundle"));
        }

        @SubscribeEvent
        public static void onRecipeSerialerRegistry(final RegistryEvent.Register<RecipeSerializer<?>> event) {
            event.getRegistry().register(RECIPE_SERIALIZER = new NBTShapedRecipe.Serializer().setRegistryName(MODID, "nbt_bundle_recipe"));
            RECIPE = RecipeType.register(MODID + ":nbt_bundle_recipe");
        }
    }
}
