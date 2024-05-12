package de.cheaterpaul.betterbundles;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import de.cheaterpaul.betterbundles.mixin.ShapedRecipeMixin;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class NBTShapedRecipe extends ShapedRecipe {


    public NBTShapedRecipe(String p_272759_, CraftingBookCategory p_273506_, ShapedRecipePattern p_310709_, ItemStack p_272852_) {
        super(p_272759_, p_273506_, p_310709_, p_272852_);
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull CraftingContainer p_44169_, RegistryAccess registryAccess) {
        ItemStack result = super.assemble(p_44169_, registryAccess);
        for (int i = 0; i < p_44169_.getHeight(); i++) {
            for (int i1 = 0; i1 < p_44169_.getWidth(); i1++) {
                ItemStack t = p_44169_.getItem(i* p_44169_.getWidth() + i1);
                if (t.getItem() instanceof BundleItem) {
                    if (!t.getOrCreateTag().contains("Items")) {
                        //noinspection ConstantConditions
                        t.getTag().put("Items", new ListTag());
                    }
                    //noinspection ConstantConditions
                    result.getOrCreateTag().put("Items",t.getTag().getList("Items", 10));
                    return result;
                }
            }
        }
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BetterBundlesMod.RECIPE_SERIALIZER.get();
    }

    private static NBTShapedRecipe migrate(ShapedRecipe recipe) {
        return new NBTShapedRecipe(recipe.getGroup(), recipe.category(), ((ShapedRecipeMixin) recipe).getPattern(), ((ShapedRecipeMixin) recipe).getResult());
    }

    public static class Serializer extends ShapedRecipe.Serializer {

        public static final Codec<ShapedRecipe> CODEC = ShapedRecipe.Serializer.CODEC.xmap(NBTShapedRecipe::migrate, s -> s);

        @Override
        public Codec<ShapedRecipe> codec() {
            return CODEC;
        }

        @Override
        public ShapedRecipe fromNetwork(FriendlyByteBuf p_44234_) {
            var recipe = super.fromNetwork(p_44234_);
            if (recipe == null) return null;
            return migrate(recipe);
        }

        @Override
        public void toNetwork(FriendlyByteBuf p_44227_, ShapedRecipe p_44228_) {
            super.toNetwork(p_44227_, p_44228_);
        }
    }

    public static class Builder {


        private final ShapedRecipeBuilder builder;

        public Builder(ShapedRecipeBuilder builder) {
            this.builder = builder;
        }

        public void save(RecipeOutput p_298334_) {
            this.save(p_298334_, BuiltInRegistries.ITEM.getKey(builder.getResult().asItem()));
        }

        public void save(RecipeOutput p_298334_, ResourceLocation p_126142_) {
            builder.save(new RecipeOutput() {
                @Override
                public void accept(ResourceLocation resourceLocation, Recipe<?> recipe, @Nullable ResourceLocation resourceLocation1, @Nullable JsonElement jsonElement) {
                    p_298334_.accept(resourceLocation, migrate((ShapedRecipe) recipe), resourceLocation1, jsonElement);
                }

                @Override
                public Advancement.Builder advancement() {
                    return p_298334_.advancement();
                }
            }, p_126142_);
        }
    }
}
