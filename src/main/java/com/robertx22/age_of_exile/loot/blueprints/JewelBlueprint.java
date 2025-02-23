package com.robertx22.age_of_exile.loot.blueprints;

import com.robertx22.age_of_exile.database.data.affixes.Affix;
import com.robertx22.age_of_exile.database.registry.ExileDB;
import com.robertx22.age_of_exile.loot.LootInfo;
import com.robertx22.age_of_exile.saveclasses.jewel.CraftedUniqueJewelData;
import com.robertx22.age_of_exile.saveclasses.jewel.JewelItemData;
import com.robertx22.age_of_exile.saveclasses.jewel.StatsWhileUnderAuraData;
import com.robertx22.age_of_exile.uncommon.datasaving.StackSaving;
import com.robertx22.age_of_exile.uncommon.enumclasses.PlayStyle;
import com.robertx22.age_of_exile.uncommon.interfaces.data_items.IRarity;
import com.robertx22.library_of_exile.utils.RandomUtils;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class JewelBlueprint extends RarityItemBlueprint {


    public JewelBlueprint(LootInfo info) {
        super(info);
        this.rarity.chanceForHigherRarity = 50;
        this.rarity.canRollUnique = false;
    }

    public boolean isEye = false;
    public int auraAffixes = 0;

    @Override
    public ItemStack generate() {

        JewelItemData data = createData();

        ItemStack stack = data.getItem().getDefaultInstance();

        StackSaving.JEWEL.saveTo(stack, data);

        return stack;

    }

    public JewelItemData createData() {
        JewelItemData data = new JewelItemData();

        PlayStyle style = RandomUtils.randomFromList(Arrays.stream(PlayStyle.values()).toList());

        if (isEye) {
            data.style = PlayStyle.INT.id;
            data.rar = IRarity.UNIQUE_ID;

            data.uniq = new CraftedUniqueJewelData();
            data.uniq.id = CraftedUniqueJewelData.WATCHER_EYE;

            while (data.auraStats.size() < this.auraAffixes) {
                var affix = ExileDB.Affixes().getFilterWrapped(x -> x.type == Affix.Type.watcher_eye).random();

                if (data.auraStats.stream().noneMatch(x -> x.affix.equals(affix.GUID()))) {
                    data.auraStats.add(new StatsWhileUnderAuraData(affix, this.level.get()));
                }
            }

        } else {
            data.rar = this.rarity.get().GUID();
            data.style = style.id;
        }

        data.lvl = info.level;

        data.generateAffixes();

        return data;
    }


}
