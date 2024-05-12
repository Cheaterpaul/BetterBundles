package de.cheaterpaul.betterbundles;

import com.google.common.collect.Sets;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod("betterbundles")
public class BetterBundlesMod
{
    public static final String MODID = "betterbundles";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    public static final DeferredRegister<Item> ITEMS_VANILLA = DeferredRegister.create(Registries.ITEM, "minecraft");
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPE = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);

    public static final RegistryObject<Item> BUNDLE = ITEMS_VANILLA.register("bundle", () -> new SizedBundleItem(pt(), 64));
    public static final RegistryObject<Item> BUNDLE_COPPER = ITEMS.register("copper_bundle", () -> new SizedBundleItem(pt(), 96));
    public static final RegistryObject<Item> BUNDLE_IRON = ITEMS.register("iron_bundle", () -> new SizedBundleItem(pt(), 128));
    public static final RegistryObject<Item> BUNDLE_SILVER = ITEMS.register("silver_bundle", () -> new SizedBundleItem(pt(), 192));
    public static final RegistryObject<Item> BUNDLE_GOLD = ITEMS.register("gold_bundle", () -> new SizedBundleItem(pt(), 256));
    public static final RegistryObject<Item> BUNDLE_DIAMOND = ITEMS.register("diamond_bundle", () -> new SizedBundleItem(pt(), 512));
    public static final RegistryObject<Item> BUNDLE_NETHERITE = ITEMS.register("netherite_bundle", () -> new SizedBundleItem(pt().fireResistant(), 512));
    public static final RegistryObject<RecipeType<NBTShapedRecipe>> RECIPE = RECIPE_TYPE.register("nbt_bundle_recipe", () -> RecipeType.simple(new ResourceLocation(MODID,"nbt_bundle_recipe")));
    public static final RegistryObject<RecipeSerializer<?>> RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("nbt_bundle_recipe", NBTShapedRecipe.Serializer::new);
    public static final TagKey<Item> COPPER_TAG = ItemTags.create(new ResourceLocation("forge", "ingots/copper"));
    public static final TagKey<Item> SILVER_TAG = ItemTags.create(new ResourceLocation("forge", "ingots/silver"));
    public static final TagKey<Item> BUNDLE_TAG = ItemTags.create(new ResourceLocation("forge", "bundles"));
    public static final IConditionSerializer<?> CONFIG_CONDITION = CraftingHelper.register(new ConfigCondition.Serializer());


    private static Item.Properties pt() {
        return new Item.Properties().stacksTo(1);
    }

    public BetterBundlesMod() {
        MinecraftForge.EVENT_BUS.register(this);
        Config.register();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        ITEMS_VANILLA.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        RECIPE_TYPE.register(modEventBus);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientColors::registerMethod);
        modEventBus.addListener(this::createCreativeTab);
    }

    private void createCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(BUNDLE);
            event.accept(BUNDLE_COPPER);
            event.accept(BUNDLE_IRON);
            event.accept(BUNDLE_SILVER);
            event.accept(BUNDLE_GOLD);
            event.accept(BUNDLE_DIAMOND);
            event.accept(BUNDLE_NETHERITE);
        }
    }
}
