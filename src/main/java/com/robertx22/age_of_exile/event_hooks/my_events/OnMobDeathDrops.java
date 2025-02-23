package com.robertx22.age_of_exile.event_hooks.my_events;

import com.robertx22.age_of_exile.capability.entity.EntityData;
import com.robertx22.age_of_exile.config.forge.ServerContainer;
import com.robertx22.age_of_exile.database.data.EntityConfig;
import com.robertx22.age_of_exile.database.data.league.LeagueStructure;
import com.robertx22.age_of_exile.database.data.stats.types.misc.BonusExp;
import com.robertx22.age_of_exile.database.registry.ExileDB;
import com.robertx22.age_of_exile.loot.LootInfo;
import com.robertx22.age_of_exile.loot.LootUtils;
import com.robertx22.age_of_exile.loot.MasterLootGen;
import com.robertx22.age_of_exile.maps.MapData;
import com.robertx22.age_of_exile.uncommon.datasaving.Load;
import com.robertx22.age_of_exile.uncommon.utilityclasses.LevelUtils;
import com.robertx22.age_of_exile.uncommon.utilityclasses.TeamUtils;
import com.robertx22.age_of_exile.uncommon.utilityclasses.WorldUtils;
import com.robertx22.library_of_exile.components.EntityInfoComponent;
import com.robertx22.library_of_exile.events.base.EventConsumer;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class OnMobDeathDrops extends EventConsumer<ExileEvents.OnMobDeath> {

    @Override
    public void accept(ExileEvents.OnMobDeath onMobDeath) {
        LivingEntity mobKilled = onMobDeath.mob;

        try {

            if (mobKilled.level().isClientSide) {
                return;
            }

            if (!(mobKilled instanceof Player)) {

                EntityData mobKilledData = Load.Unit(mobKilled);


                // todo doesnt work
                LivingEntity killerEntity = EntityInfoComponent.get(mobKilled)
                        .getDamageStats()
                        .getHighestDamager((ServerLevel) mobKilled.level());

                if (killerEntity == null) {
                    try {
                        if (mobKilled.getLastDamageSource()
                                .getEntity() instanceof Player) {
                            killerEntity = (LivingEntity) mobKilled.getLastDamageSource()
                                    .getEntity();
                        }
                    } catch (Exception e) {
                    }
                }

                if (killerEntity == null) {
                    if (EntityInfoComponent.get(mobKilled)
                            .getDamageStats()
                            .getEnviroOrMobDmg() < mobKilled.getMaxHealth() / 2F) {
                        killerEntity = onMobDeath.killer;
                    }
                }

                if (killerEntity instanceof ServerPlayer) {

                    ServerPlayer player = (ServerPlayer) killerEntity;
                    EntityData playerData = Load.Unit(player);

                    EntityConfig config = ExileDB.getEntityConfig(mobKilled, mobKilledData);

                    float loot_multi = (float) config.loot_multi;
                    float exp_multi = (float) config.exp_multi;

                    if (loot_multi > 0) {
                        MasterLootGen.genAndDrop(mobKilled, player);

                        if (WorldUtils.isDungeonWorld(mobKilled.level())) {
                            var map = Load.mapAt(mobKilled.level(), mobKilled.blockPosition());
                            if (map != null) {
                                Load.player(player).prophecy.onKillMobInMap(player, mobKilled);

                                // map.trySpawnMechanic(mobKilled.level(), mobKilled.blockPosition());

                                var mech = LeagueStructure.getMechanicFromPosition((ServerLevel) player.level(), mobKilled.blockPosition());
                                if (!mech.isEmpty()) {
                                    var info = LootInfo.ofMobKilled(player, mobKilled);
                                    mech.onKillMob(map, info);
                                }

                            }
                        }

                    }
                    if (exp_multi > 0) {
                        GiveExp(mobKilled, player, playerData, mobKilledData, exp_multi);
                    }


                }

            }

        } catch (

                Exception e) {
            e.printStackTrace();
        }

    }

    private static void GiveExp(LivingEntity victim, Player killer, EntityData killerData, EntityData mobData, float multi) {

        float exp = LevelUtils.getBaseExpMobReward(mobData.getLevel());

        if (exp < 1) {
            exp++;
        }


        exp *= LootUtils.getMobHealthBasedLootMulti(victim);
        exp *= mobData.getMobRarity().expMulti();
        exp *= multi;
        exp *= ServerContainer.get().EXP_GAIN_MULTI.get();
        exp *= ExileDB.getDimensionConfig(victim.level()).exp_multi;
        exp *= killerData.getUnit().getCalculatedStat(BonusExp.getInstance()).getMultiplier();
        exp *= Load.player(killer).favor.getLootExpMulti();

        if (WorldUtils.isMapWorldClass(victim.level())) {
            MapData map = Load.mapAt(victim.level(), victim.blockPosition());
            if (map != null) {
                exp *= map.map.getExpMulti();
            }
        }


        exp = ExileEvents.MOB_EXP_DROP.callEvents(new ExileEvents.OnMobExpDrop(victim, exp)).exp;


        if ((int) exp > 0) {

            List<Player> list = TeamUtils.getOnlineTeamMembersInRange(killer);

            int members = list.size() - 1;
            if (members > 4) {
                members = 4;
            }

            float teamMulti = 1 + (0.2F * members);

            exp *= teamMulti;

            exp /= list.size();

            for (Player player : list) {
                int splitExp = (int) (exp * LootUtils.getLevelDistancePunishmentMulti(mobData.getLevel(), Load.Unit(player).getLevel()));

                if (splitExp > 0) {
                    Load.Unit(player).GiveExp(player, (int) splitExp);
                }
            }

        }
    }

}
