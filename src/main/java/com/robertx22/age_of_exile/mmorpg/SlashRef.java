package com.robertx22.age_of_exile.mmorpg;

import net.minecraft.resources.ResourceLocation;

public class SlashRef {

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

    public static ResourceLocation guiId(String id) {
        return new ResourceLocation(SlashRef.MODID, "textures/gui/" + id + ".png");
    }

    public static final String MODID = "mmorpg";
    public static final String MOD_NAME = "Mine and Slash";

}
