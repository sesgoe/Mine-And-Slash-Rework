package com.robertx22.age_of_exile.tags;

import com.robertx22.age_of_exile.tags.all.*;

public class ModTags {

    public static EffectTags EFFECT = new EffectTags();
    public static MobTags MOB = new MobTags();
    public static SpellTags SPELL = new SpellTags();
    public static SlotTags GEAR_SLOT = new SlotTags();
    public static ElementTags ELEMENT = new ElementTags();
    public static DungeonTags DUNGEON = new DungeonTags();

    public static void init() {

        EffectTags.init();
        MobTags.init();
        SlotTags.init();
        SpellTags.init();
        ElementTags.init();
        DungeonTags.init();

    }


}
