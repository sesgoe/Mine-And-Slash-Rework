package com.robertx22.age_of_exile.database.data.stats.types.generated;

import com.robertx22.age_of_exile.database.data.stats.Stat;
import com.robertx22.age_of_exile.database.data.stats.StatScaling;
import com.robertx22.age_of_exile.database.data.stats.effects.base.BaseDamageEffect;
import com.robertx22.age_of_exile.database.data.stats.layers.StatLayers;
import com.robertx22.age_of_exile.database.data.stats.priority.StatPriority;
import com.robertx22.age_of_exile.database.data.stats.types.ElementalStat;
import com.robertx22.age_of_exile.mmorpg.SlashRef;
import com.robertx22.age_of_exile.saveclasses.unit.StatData;
import com.robertx22.age_of_exile.uncommon.MathHelper;
import com.robertx22.age_of_exile.uncommon.effectdatas.DamageEvent;
import com.robertx22.age_of_exile.uncommon.effectdatas.rework.EventData;
import com.robertx22.age_of_exile.uncommon.enumclasses.AttackType;
import com.robertx22.age_of_exile.uncommon.enumclasses.Elements;
import com.robertx22.age_of_exile.uncommon.interfaces.EffectSides;
import com.robertx22.age_of_exile.uncommon.wrappers.MapWrapper;

import java.util.List;

public class PhysicalToElement extends ElementalStat {

    public static MapWrapper<Elements, PhysicalToElement> MAP = new MapWrapper();

    @Override
    public List<Stat> generateAllPossibleStatVariations() {
        List<Stat> list = super.generateAllPossibleStatVariations();
        list.forEach(x -> MAP.put(x.getElement(), (PhysicalToElement) x));
        return list;

    }


    public PhysicalToElement(Elements element) {
        super(element);
        this.scaling = StatScaling.NONE;

        this.statEffect = new Effect();

        this.min = 0;
        this.max = 100;
    }

    @Override
    public Stat newGeneratedInstance(Elements element) {
        return new PhysicalToElement(element);
    }

    @Override
    public String GUID() {
        return "phys_to_" + this.getElement().guidName;
    }

    @Override
    public String locDescForLangFile() {
        return "Turns % of phys atk dmg into ele";
    }

    @Override
    public String locDescLangFileGUID() {
        return SlashRef.MODID + ".stat_desc." + "turn_phys_to_ele";
    }

    @Override
    public boolean IsPercent() {
        return true;
    }

    private class Effect extends BaseDamageEffect {

        @Override
        public StatPriority GetPriority() {
            return StatPriority.Damage.DAMAGE_LAYERS;
        }

        @Override
        public EffectSides Side() {
            return EffectSides.Source;
        }

        @Override
        public DamageEvent activate(DamageEvent effect, StatData data, Stat stat) {
            float dmg = effect.data.getNumber(EventData.NUMBER).number * data.getValue() / 100F;
            dmg = MathHelper.clamp(dmg, 0, effect.data.getNumber());
            if (dmg > 0) {
                effect.addBonusEleDmg(stat.getElement(), dmg);
                effect.getLayer(StatLayers.Offensive.DAMAGE_CONVERSION, EventData.NUMBER, Side()).reduce(dmg);
            }
            return effect;
        }

        @Override
        public boolean canActivate(DamageEvent effect, StatData data, Stat stat) {
            return effect.GetElement() == Elements.Physical && effect.getAttackType().equals(AttackType.hit);
        }

    }

    @Override
    public String locNameForLangFile() {
        return "Physical to " + this.getElement()
                .dmgName + " Damage";
    }

}


