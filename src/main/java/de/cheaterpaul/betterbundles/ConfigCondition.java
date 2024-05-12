package de.cheaterpaul.betterbundles;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

public record ConfigCondition(String option) implements ICondition {

    public static final Codec<ConfigCondition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("option").forGetter(ConfigCondition::option)
    ).apply(inst, ConfigCondition::new));
    private static final ResourceLocation ID = new ResourceLocation(BetterBundlesMod.MODID, "config");

    @Override
    public Codec<? extends ICondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(IContext context) {
        return switch (this.option) {
            case "enhanced_bundle" -> Config.COMMON.disableEnhancedBundleRecipes.get();
            case "bundle" -> Config.COMMON.disableBundleRecipe.get();
            default -> true;
        };
    }
}
