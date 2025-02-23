package com.robertx22.age_of_exile.uncommon.effectdatas.rework.action;

import com.robertx22.age_of_exile.database.data.stats.Stat;
import com.robertx22.age_of_exile.saveclasses.unit.ResourceType;
import com.robertx22.age_of_exile.saveclasses.unit.StatData;
import com.robertx22.age_of_exile.uncommon.effectdatas.EffectEvent;
import com.robertx22.age_of_exile.uncommon.effectdatas.EventBuilder;
import com.robertx22.age_of_exile.uncommon.effectdatas.RestoreResourceEvent;
import com.robertx22.age_of_exile.uncommon.effectdatas.rework.EventData;
import com.robertx22.age_of_exile.uncommon.effectdatas.rework.RestoreType;
import com.robertx22.age_of_exile.uncommon.effectdatas.rework.number_provider.NumberProvider;
import com.robertx22.age_of_exile.uncommon.interfaces.EffectSides;

public class RestoreResourceAction extends StatEffect {

    NumberProvider num_provider = new NumberProvider();
    ResourceType type = ResourceType.health;
    EffectSides side = EffectSides.Source;
    RestoreType restore_type = RestoreType.heal;

    public RestoreResourceAction(String id, NumberProvider num, ResourceType type, RestoreType restoreType) {
        super(id, "restore_resource");
        this.num_provider = num;
        this.type = type;
        this.restore_type = restoreType;
    }

    RestoreResourceAction() {
        super("", "restore_resource");
    }

    @Override
    public void activate(EffectEvent event, EffectSides statSource, StatData data, Stat stat) {

        float val = num_provider.getValue(event, event.getSide(statSource), data);
        val *= event.data.getNumber(EventData.ATTACK_COOLDOWN).number; // todo test

        if (this.restore_type == RestoreType.leech) {
            event.sourceData.leech.addLeech(type, val);
            return;
        }

        EventBuilder<RestoreResourceEvent> restore = EventBuilder.ofRestore(event.source, event.getSide(side), type, restore_type, val);
        if (event.data.isSpellEffect()) {
            restore.setSpell(event.getSpell());
        }
        restore.build().Activate();
    }

    @Override
    public Class<? extends StatEffect> getSerClass() {
        return RestoreResourceAction.class;
    }

}
