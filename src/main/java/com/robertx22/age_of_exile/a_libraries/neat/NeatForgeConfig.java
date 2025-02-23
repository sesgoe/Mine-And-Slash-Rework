package com.robertx22.age_of_exile.a_libraries.neat;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Locale;


public class NeatForgeConfig {
    public static void init() {
        Pair<ForgeNeatConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ForgeNeatConfig::new);
        NeatConfig.instance = specPair.getLeft();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, specPair.getRight(), defaultConfigName(ModConfig.Type.CLIENT, "mns_neat"));
    }

    private static String defaultConfigName(ModConfig.Type type, String modId) {
        // config file name would be "forge-client.toml" and "forge-server.toml"
        return String.format(Locale.ROOT, "%s-%s.toml", modId, type.extension());
    }

    public static void register() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (incoming, isNetwork) -> true));
        NeatForgeConfig.init();
    }

    private static class ForgeNeatConfig implements NeatConfig.ConfigAccess {
        private final ConfigValue<Integer> maxDistance;
        private final ConfigValue<Boolean> renderInF1;
        private final ConfigValue<Double> heightAbove;
        private final ConfigValue<Boolean> drawBackground;
        private final ConfigValue<Integer> backgroundPadding;
        private final ConfigValue<Integer> backgroundHeight;
        private final ConfigValue<Integer> barHeight;
        private final ConfigValue<Integer> plateSize;
        private final ConfigValue<Integer> plateSizeBoss;
        private final ConfigValue<Boolean> showAttributes;
        private final ConfigValue<Boolean> showArmor;
        private final ConfigValue<Boolean> groupArmor;
        private final ConfigValue<Boolean> colorByType;
        private final ConfigValue<Integer> hpTextHeight;
        private final ConfigValue<Boolean> showMaxHP;
        private final ConfigValue<Boolean> showCurrentHP;
        private final ConfigValue<Boolean> showPercentage;
        private final ConfigValue<Boolean> showOnPlayers;
        private final ConfigValue<Boolean> showOnBosses;
        private final ConfigValue<Boolean> showOnlyFocused;
        private final ConfigValue<Boolean> showFullHealth;
        private final ConfigValue<Boolean> enableDebugInfo;
        private final ConfigValue<List<? extends String>> blacklist;

        public ForgeNeatConfig(ForgeConfigSpec.Builder builder) {
            builder.push("general");

            maxDistance = builder.define("Max Distance", 12);
            renderInF1 = builder.define("Render with Interface Disabled (F1)", false);
            heightAbove = builder.define("Height Above Mob", 0.6);
            drawBackground = builder.define("Draw Background", true);
            backgroundPadding = builder.define("Background Padding", 2);
            backgroundHeight = builder.define("Background Height", 6);
            barHeight = builder.define("Health Bar Height", 4);
            plateSize = builder.define("Plate Size", 25);
            plateSizeBoss = builder.define("Plate Size (Boss)", 50);
            showAttributes = builder.define("Show Attributes", true);
            showArmor = builder.define("Show Armor", true);
            groupArmor = builder.define("Group Armor (condense 5 iron icons into 1 diamond icon)", true);
            colorByType = builder.define("Color Health Bar by Type (instead of health percentage)", false);
            hpTextHeight = builder.define("HP Text Height", 14);
            showMaxHP = builder.define("Show Max HP", true);
            showCurrentHP = builder.define("Show Current HP", true);
            showPercentage = builder.define("Show HP Percentage", true);
            showOnPlayers = builder.define("Display on Players", true);
            showOnBosses = builder.define("Display on Bosses", true);
            showOnlyFocused = builder.define("Only show the health bar for the entity looked at", false);
            showFullHealth = builder.define("Show entities with full health", true);
            enableDebugInfo = builder.define("Show Debug Info with F3", true);
            blacklist = builder.comment("Blacklist uses entity IDs, not their display names. Use F3 to see them in the Neat bar.")
                    .defineList("Blacklist", NeatConfig.DEFAULT_DISABLED, a -> true);

            builder.pop();
        }

        @Override
        public int maxDistance() {
            return maxDistance.get();
        }

        @Override
        public boolean renderInF1() {
            return renderInF1.get();
        }

        @Override
        public double heightAbove() {
            return heightAbove.get();
        }

        @Override
        public boolean drawBackground() {
            return drawBackground.get();
        }

        @Override
        public int backgroundPadding() {
            return backgroundPadding.get();
        }

        @Override
        public int backgroundHeight() {
            return backgroundHeight.get();
        }

        @Override
        public int barHeight() {
            return barHeight.get();
        }

        @Override
        public int plateSize() {
            return plateSize.get();
        }

        @Override
        public int plateSizeBoss() {
            return plateSizeBoss.get();
        }

        @Override
        public boolean showAttributes() {
            return showAttributes.get();
        }

        @Override
        public boolean showArmor() {
            return showArmor.get();
        }

        @Override
        public boolean groupArmor() {
            return groupArmor.get();
        }

        @Override
        public boolean colorByType() {
            return colorByType.get();
        }

        @Override
        public int hpTextHeight() {
            return hpTextHeight.get();
        }

        @Override
        public boolean showMaxHP() {
            return showMaxHP.get();
        }

        @Override
        public boolean showCurrentHP() {
            return showCurrentHP.get();
        }

        @Override
        public boolean showPercentage() {
            return showPercentage.get();
        }

        @Override
        public boolean showOnPlayers() {
            return showOnPlayers.get();
        }

        @Override
        public boolean showOnBosses() {
            return showOnBosses.get();
        }

        @Override
        public boolean showOnlyFocused() {
            return showOnlyFocused.get();
        }

        @Override
        public boolean showFullHealth() {
            return showFullHealth.get();
        }

        @Override
        public boolean enableDebugInfo() {
            return enableDebugInfo.get();
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<String> blacklist() {
            // Safe cast from List<? extends String> to List<String>, as String is final
            return (List<String>) blacklist.get();
        }
    }
}