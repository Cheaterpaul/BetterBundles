package de.cheaterpaul.betterbundles;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.tags.ItemTagsProvider;
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
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@Mod("betterbundles")
public class BetterBundlesMod
{
    public static final String MODID = "betterbundles";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    public static final DeferredRegister<Item> ITEMS_VANILLA = DeferredRegister.create(Registries.ITEM, "minecraft");
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPE = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);
    public static final DeferredRegister<Codec<? extends ICondition>> CONDITIONS = DeferredRegister.create(ForgeRegistries.Keys.CONDITION_SERIALIZERS, MODID);

    public static final RegistryObject<Item> BUNDLE = ITEMS_VANILLA.register("bundle", () -> new SizedBundleItem(pt(), 64));
    public static final RegistryObject<Item> BUNDLE_COPPER = ITEMS.register("copper_bundle", () -> new SizedBundleItem(pt(), 96));
    public static final RegistryObject<Item> BUNDLE_IRON = ITEMS.register("iron_bundle", () -> new SizedBundleItem(pt(), 128));
    public static final RegistryObject<Item> BUNDLE_SILVER = ITEMS.register("silver_bundle", () -> new SizedBundleItem(pt(), 192));
    public static final RegistryObject<Item> BUNDLE_GOLD = ITEMS.register("gold_bundle", () -> new SizedBundleItem(pt(), 256));
    public static final RegistryObject<Item> BUNDLE_DIAMOND = ITEMS.register("diamond_bundle", () -> new SizedBundleItem(pt(), 512));
    public static final RegistryObject<Item> BUNDLE_NETHERITE = ITEMS.register("netherite_bundle", () -> new SizedBundleItem(pt().fireResistant(), 512));
    public static final RegistryObject<RecipeType<NBTShapedRecipe>> RECIPE = RECIPE_TYPE.register("nbt_bundle_recipe", () -> RecipeType.simple(new ResourceLocation(MODID,"nbt_bundle_recipe")));
    public static final RegistryObject<RecipeSerializer<?>> RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("nbt_bundle_recipe", NBTShapedRecipe.Serializer::new);
    public static final RegistryObject<Codec<ConfigCondition>> CONFIG_CONDITION = CONDITIONS.register("config", () -> ConfigCondition.CODEC);
    public static final TagKey<Item> COPPER_TAG = ItemTags.create(new ResourceLocation("forge", "ingots/copper"));
    public static final TagKey<Item> SILVER_TAG = ItemTags.create(new ResourceLocation("forge", "ingots/silver"));
    public static final TagKey<Item> IRON_TAG = ItemTags.create(new ResourceLocation("forge", "ingots/iron"));
    public static final TagKey<Item> GOLD_TAG = ItemTags.create(new ResourceLocation("forge", "ingots/gold"));
    public static final TagKey<Item> NETHERITE_TAG = ItemTags.create(new ResourceLocation("forge", "ingots/netherite"));
    public static final TagKey<Item> DIAMOND_TAG = ItemTags.create(new ResourceLocation("forge", "gems/diamond"));
    public static final TagKey<Item> BUNDLE_TAG = ItemTags.create(new ResourceLocation("forge", "bundles"));


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
        CONDITIONS.register(modEventBus);
        modEventBus.addListener(this::s);
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

    private void s(GatherDataEvent event) {
        BlockTags blockTags = new BlockTags(event.getGenerator().getPackOutput(), event.getLookupProvider(), MODID, event.getExistingFileHelper());
        event.getGenerator().addProvider(event.includeServer(), blockTags);
        event.getGenerator().addProvider(event.includeServer(), new TagProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), blockTags.contentsGetter(), event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new Recipes(event.getGenerator().getPackOutput()));
    }

    public static class Recipes extends RecipeProvider {

        public Recipes(PackOutput p_248933_) {
            super(p_248933_);
        }

        @Override
        protected void buildRecipes(RecipeOutput recipeOutput) {
            ConditionalRecipe.builder().condition(new NotCondition(new ConfigCondition("bundle"))).recipe(outpur -> ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BUNDLE.get()).define('#', Items.RABBIT_HIDE).define('-', Items.STRING).pattern("-#-").pattern("# #").pattern("###").unlockedBy("has_string", has(Items.STRING)).save(outpur)).save(recipeOutput, BUNDLE.getId());
            ConditionalRecipe.builder().condition(new NotCondition(new ConfigCondition("enhanced_bundle"))).recipe(output -> new NBTShapedRecipe.Builder(ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BUNDLE_COPPER.get()).define('X', COPPER_TAG).define('Y', BUNDLE.get()).pattern("XXX").pattern("XYX").pattern("XXX").unlockedBy("has_copper", has(COPPER_TAG))).save(output)).save(recipeOutput, BUNDLE_COPPER.getId());
//            ConditionalRecipe.builder().condition(new NotCondition(new ConfigCondition("enhanced_bundle"))).recipe(output -> new NBTShapedRecipe.Builder(ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BUNDLE_IRON.get()).define('X', IRON_TAG).define('Y', BUNDLE_COPPER.get()).pattern("XXX").pattern("XYX").pattern("XXX").unlockedBy("has_iron", has(IRON_TAG))).save(output)).save(recipeOutput, BUNDLE_IRON.getId().withSuffix("_copper"));
//            ConditionalRecipe.builder().condition(new NotCondition(new ConfigCondition("enhanced_bundle"))).recipe(output -> new NBTShapedRecipe.Builder(ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BUNDLE_IRON.get()).define('X', IRON_TAG).define('Y', BUNDLE.get()).pattern("XXX").pattern("XYX").pattern("XXX").unlockedBy("has_iron", has(IRON_TAG))).save(output)).save(recipeOutput, BUNDLE_IRON.getId());
//            ConditionalRecipe.builder().condition(new NotCondition(new ConfigCondition("enhanced_bundle"))).recipe(output -> new NBTShapedRecipe.Builder(ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BUNDLE_SILVER.get()).define('X', SILVER_TAG).define('Y', BUNDLE.get()).pattern("XXX").pattern("XYX").pattern("XXX").unlockedBy("has_iron", has(SILVER_TAG))).save(output)).save(recipeOutput, BUNDLE_SILVER.getId());
//            ConditionalRecipe.builder().condition(new NotCondition(new ConfigCondition("enhanced_bundle"))).recipe(output -> new NBTShapedRecipe.Builder(ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BUNDLE_GOLD.get()).define('X', GOLD_TAG).define('Y', BUNDLE_IRON.get()).pattern("XXX").pattern("XYX").pattern("XXX").unlockedBy("has_gold", has(GOLD_TAG))).save(output)).save(recipeOutput, BUNDLE_GOLD.getId());
//            ConditionalRecipe.builder().condition(new NotCondition(new ConfigCondition("enhanced_bundle"))).recipe(output -> new NBTShapedRecipe.Builder(ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BUNDLE_GOLD.get()).define('X', GOLD_TAG).define('Y', BUNDLE_SILVER.get()).pattern("XXX").pattern("XYX").pattern("XXX").unlockedBy("has_gold", has(GOLD_TAG))).save(output)).save(recipeOutput, BUNDLE_GOLD.getId().withSuffix("_silver"));
//            ConditionalRecipe.builder().condition(new NotCondition(new ConfigCondition("enhanced_bundle"))).recipe(output -> new NBTShapedRecipe.Builder(ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BUNDLE_DIAMOND.get()).define('X', DIAMOND_TAG).define('Y', BUNDLE_GOLD.get()).pattern("XXX").pattern("XYX").pattern("XXX").unlockedBy("has_gold", has(DIAMOND_TAG))).save(output)).save(recipeOutput, BUNDLE_DIAMOND.getId());
//            ConditionalRecipe.builder().condition(new NotCondition(new ConfigCondition("enhanced_bundle"))).recipe(output -> new NBTShapedRecipe.Builder(ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BUNDLE_NETHERITE.get()).define('X', NETHERITE_TAG).define('Y', BUNDLE_NETHERITE.get()).pattern(" X ").pattern("XYX").pattern(" X ").unlockedBy("has_netherite", has(NETHERITE_TAG))).save(output)).save(recipeOutput, BUNDLE_NETHERITE.getId());
        }
    }

    public static class BlockTags extends BlockTagsProvider {

        public BlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, modId, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {

        }
    }

    public static class TagProvider extends ItemTagsProvider {

        public TagProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {
            super(p_275343_, p_275729_, p_275322_, MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(BUNDLE_TAG).add(BUNDLE.get(), BUNDLE_COPPER.get(), BUNDLE_IRON.get(), BUNDLE_SILVER.get(), BUNDLE_GOLD.get(), BUNDLE_DIAMOND.get(), BUNDLE_NETHERITE.get());
            tag(COPPER_TAG).add(Items.COPPER_INGOT);
            tag(SILVER_TAG);
        }
    }
}
