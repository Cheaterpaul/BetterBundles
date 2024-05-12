package de.cheaterpaul.betterbundles;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

import javax.annotation.Nonnull;

public class NBTShapedRecipe extends ShapedRecipe {

    public NBTShapedRecipe(ResourceLocation p_250963_, String p_250221_, CraftingBookCategory p_250716_, int p_251480_, int p_251980_, NonNullList<Ingredient> p_252150_, ItemStack p_248581_) {
        super(p_250963_, p_250221_, p_250716_, p_251480_, p_251980_, p_252150_, p_248581_);
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

    private static NBTShapedRecipe migrate(ShapedRecipe recipe) {
        return new NBTShapedRecipe(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getResultItem(null));
    }

    public static class Serializer extends ShapedRecipe.Serializer {
        @Nonnull
        @Override
        public NBTShapedRecipe fromJson(@Nonnull ResourceLocation p_44236_, @Nonnull JsonObject p_44237_) {
            return migrate(super.fromJson(p_44236_, p_44237_));
        }

        @Override
        public NBTShapedRecipe fromNetwork(@Nonnull ResourceLocation p_44239_, @Nonnull FriendlyByteBuf p_44240_) {
            ShapedRecipe r = super.fromNetwork(p_44239_, p_44240_);
            if (r == null) return null;
            return migrate(r);
        }
    }
}
