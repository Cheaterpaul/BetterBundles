package de.cheaterpaul.betterbundles;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    public static final Common COMMON;
    private static final ForgeConfigSpec clientSpec;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        clientSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, clientSpec);
    }

    public static class Common {

        public final ForgeConfigSpec.BooleanValue disableBundleRecipe;
        public final ForgeConfigSpec.BooleanValue disableEnhancedBundleRecipes;

        public Common(ForgeConfigSpec.Builder builder) {
            disableBundleRecipe = builder.define("disableBundleRecipe", false);
            disableEnhancedBundleRecipes = builder.define("disableEnhancedBundleRecipes", false);
        }
    }
}
