package de.cheaterpaul.betterbundles;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class ConfigCondition implements ICondition {

    private static final ResourceLocation ID = new ResourceLocation(BetterBundlesMod.MODID, "config");

    private final String option;

    public ConfigCondition(String option) {
        this.option = option;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test() {
        return switch (this.option) {
            case "enhanced_bundle" -> Config.COMMON.disableEnhancedBundleRecipes.get();
            case "bundle" -> Config.COMMON.disableBundleRecipe.get();
            default -> true;
        };
    }

    public static class Serializer implements IConditionSerializer<ConfigCondition> {
        @Override
        public ResourceLocation getID() {
            return ID;
        }

        @Override
        public ConfigCondition read(JsonObject json) {
            return new ConfigCondition(json.get("option").getAsString());
        }

        @Override
        public void write(JsonObject json, ConfigCondition value) {
            json.addProperty("option", value.option);
        }
    }
}
