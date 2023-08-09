package com.robertx22.age_of_exile.database.data.spells.spell_classes;

import com.robertx22.age_of_exile.database.data.spells.components.EntityActivation;
import com.robertx22.age_of_exile.database.data.spells.entities.EntitySavedSpellData;
import com.robertx22.age_of_exile.database.data.value_calc.LevelProvider;
import com.robertx22.age_of_exile.uncommon.datasaving.Load;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class SpellCtx {

    // the entity the effect came from, player summons fireball. fireball hits enemy, dmg comes from fireball
    public Entity sourceEntity;

    public Level world;
    public LivingEntity caster;

    public LivingEntity target;

    public BlockPos pos;
    public Vec3 vecPos;

    public final EntityActivation activation;

    public final LevelProvider levelProvider;

    public EntitySavedSpellData calculatedSpellData;

    public boolean isCastedFromTotem = false;

    private SpellCtx(EntityActivation act, Entity sourceEntity, LivingEntity caster, LivingEntity target, BlockPos pos, Vec3 vec, EntitySavedSpellData calculatedSpellData) {
        this.sourceEntity = sourceEntity;
        this.caster = caster;
        this.target = target;
        this.pos = pos;
        this.calculatedSpellData = calculatedSpellData;
        this.world = caster.level();
        this.vecPos = vec;
        this.activation = act;

        this.levelProvider = new LevelProvider(caster, calculatedSpellData.getSpell());

    }

    public static SpellCtx onCast(LivingEntity caster, EntitySavedSpellData data) {
        Objects.requireNonNull(caster);
        Objects.requireNonNull(data);
        return new SpellCtx(EntityActivation.ON_CAST, caster, caster, caster, caster.blockPosition(), caster.position(), data);
    }

    public static SpellCtx onHit(LivingEntity caster, Entity sourceEntity, LivingEntity target, EntitySavedSpellData data) {
        Objects.requireNonNull(caster);
        Objects.requireNonNull(sourceEntity);
        Objects.requireNonNull(data);

        Load.spells(caster)
                .onSpellHitTarget(sourceEntity, target);
        return new SpellCtx(EntityActivation.ON_HIT, sourceEntity, caster, target, target.blockPosition(), target.position(), data);
    }

    public static SpellCtx onTotemCastSpell(LivingEntity caster, Entity sourceEntity, EntitySavedSpellData data) {
        Objects.requireNonNull(caster);
        Objects.requireNonNull(sourceEntity);
        Objects.requireNonNull(data);

        var c = new SpellCtx(EntityActivation.ON_CAST, sourceEntity, caster, caster, sourceEntity.blockPosition(), sourceEntity.position(), data);
        c.isCastedFromTotem = true;
        return c;
    }

    public static SpellCtx onEntityHit(SpellCtx ctx, LivingEntity target) {
        Objects.requireNonNull(ctx);
        Objects.requireNonNull(target);
        return new SpellCtx(EntityActivation.PER_ENTITY_HIT, ctx.sourceEntity, ctx.caster, target, target.blockPosition(), target.position(), ctx.calculatedSpellData);
    }

    public static SpellCtx onExpire(LivingEntity caster, Entity sourceEntity, EntitySavedSpellData data) {
        Objects.requireNonNull(caster);
        Objects.requireNonNull(sourceEntity);
        Objects.requireNonNull(data);
        LivingEntity target = sourceEntity instanceof LivingEntity ? (LivingEntity) sourceEntity : null;
        return new SpellCtx(EntityActivation.ON_EXPIRE, sourceEntity, caster, target, sourceEntity.blockPosition(), sourceEntity.position(), data);
    }

    public static SpellCtx onTick(LivingEntity caster, Entity sourceEntity, EntitySavedSpellData data) {
        Objects.requireNonNull(caster);
        Objects.requireNonNull(sourceEntity);
        Objects.requireNonNull(data);
        LivingEntity target = sourceEntity instanceof LivingEntity ? (LivingEntity) sourceEntity : null;
        return new SpellCtx(EntityActivation.ON_TICK, sourceEntity, caster, target, sourceEntity.blockPosition(), sourceEntity.position(), data);
    }

}
