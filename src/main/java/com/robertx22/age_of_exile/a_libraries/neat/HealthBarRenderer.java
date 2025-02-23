package com.robertx22.age_of_exile.a_libraries.neat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.robertx22.age_of_exile.database.data.mob_affixes.MobAffix;
import com.robertx22.age_of_exile.database.registry.ExileDB;
import com.robertx22.age_of_exile.uncommon.datasaving.Load;
import com.robertx22.age_of_exile.uncommon.utilityclasses.HealthUtils;
import com.robertx22.library_of_exile.utils.CLOC;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Quaternionf;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HealthBarRenderer {

    private static final DecimalFormat HEALTH_FORMAT = new DecimalFormat("#.##");

    private static Entity getEntityLookedAt(Entity e) {
        Entity foundEntity = null;
        final double finalDistance = 32;
        HitResult pos = raycast(e, finalDistance);
        Vec3 positionVector = e.getEyePosition();

        double distance = pos.getLocation().distanceTo(positionVector);

        Vec3 lookVector = e.getLookAngle();
        Vec3 reachVector = positionVector.add(lookVector.x * finalDistance, lookVector.y * finalDistance, lookVector.z * finalDistance);

        List<Entity> entitiesInBoundingBox = e.level().getEntities(e,
                e.getBoundingBox().inflate(lookVector.x * finalDistance, lookVector.y * finalDistance, lookVector.z * finalDistance)
                        .expandTowards(1F, 1F, 1F));
        double minDistance = distance;

        for (Entity entity : entitiesInBoundingBox) {
            Entity lookedEntity = null;
            if (entity.isPickable()) {
                AABB collisionBox = entity.getBoundingBoxForCulling();
                Optional<Vec3> interceptPosition = collisionBox.clip(positionVector, reachVector);

                if (collisionBox.contains(positionVector)) {
                    if (0.0D < minDistance || minDistance == 0.0D) {
                        lookedEntity = entity;
                        minDistance = 0.0D;
                    }
                } else if (interceptPosition.isPresent()) {
                    double distanceToEntity = positionVector.distanceTo(interceptPosition.get());

                    if (distanceToEntity < minDistance || minDistance == 0.0D) {
                        lookedEntity = entity;
                        minDistance = distanceToEntity;
                    }
                }
            }

            if (lookedEntity != null && minDistance < distance) {
                foundEntity = lookedEntity;
            }
        }

        return foundEntity;
    }

    private static HitResult raycast(Entity e, double len) {
        Vec3 origin = e.getEyePosition();
        Vec3 ray = e.getLookAngle();
        Vec3 next = origin.add(ray.normalize().scale(len));
        return e.level().clip(new ClipContext(origin, next, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, e));
    }

    private static ItemStack getIcon(LivingEntity entity, boolean boss) {
        if (boss) {
            return new ItemStack(Items.NETHER_STAR);
        }
        MobType type = entity.getMobType();
        if (type == MobType.ARTHROPOD) {
            return new ItemStack(Items.SPIDER_EYE);
        } else if (type == MobType.UNDEAD) {
            return new ItemStack(Items.ROTTEN_FLESH);
        } else {
            return ItemStack.EMPTY;
        }
    }

    private static int getColor(LivingEntity entity, boolean colorByType, boolean boss) {
        if (colorByType) {
            int r = 0;
            int g = 255;
            int b = 0;
            if (boss) {
                r = 128;
                g = 0;
                b = 128;
            }
            if (entity instanceof Monster) {
                r = 255;
                g = 0;
                b = 0;
            }
            return 0xff000000 | r << 16 | g << 8 | b;
        } else {
            float health = Mth.clamp(entity.getHealth(), 0.0F, entity.getMaxHealth());
            float hue = Math.max(0.0F, (health / entity.getMaxHealth()) / 3.0F - 0.07F);
            return Mth.hsvToRgb(hue, 1.0F, 1.0F);
        }
    }

    private static final TagKey<EntityType<?>> FORGE_BOSS_TAG =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge", "bosses"));

    private static final TagKey<EntityType<?>> FABRIC_BOSS_TAG =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("c", "bosses"));

    private static boolean isBoss(Entity entity) {
        return entity.getType().is(FORGE_BOSS_TAG) || entity.getType().is(FABRIC_BOSS_TAG);
    }

    private static boolean shouldShowPlate(LivingEntity living, Entity cameraEntity) {
        if (living == cameraEntity) {
            return false;
        }

        if ((!NeatConfig.instance.renderInF1() && !Minecraft.renderNames()) || !NeatConfig.draw) {
            return false;
        }
        
        var id = ForgeRegistries.ENTITY_TYPES.getKey(living.getType());
        if (NeatConfig.instance.blacklist().contains(id.toString())) {
            return false;
        }

        float distance = living.distanceTo(cameraEntity);
        if (distance > NeatConfig.instance.maxDistance()
                || !living.hasLineOfSight(cameraEntity)) {
            return false;
        }
        if (!NeatConfig.instance.showOnBosses() && isBoss(living)) {
            return false;
        }
        if (!NeatConfig.instance.showOnPlayers() && living instanceof Player) {
            return false;
        }
        if (!NeatConfig.instance.showFullHealth() && living.getHealth() >= living.getMaxHealth()) {
            return false;
        }
        if (NeatConfig.instance.showOnlyFocused() && getEntityLookedAt(cameraEntity) != living) {
            return false;
        }

        boolean visible = true;
        if (cameraEntity instanceof Player cameraPlayer) {
            visible = !living.isInvisibleTo(cameraPlayer);
        }
        Team livingTeam = living.getTeam();
        Team cameraTeam = cameraEntity.getTeam();
        if (livingTeam != null) {
            return switch (livingTeam.getNameTagVisibility()) {
                case ALWAYS -> visible;
                case NEVER -> false;
                case HIDE_FOR_OTHER_TEAMS ->
                        cameraTeam == null ? visible : livingTeam.isAlliedTo(cameraTeam) && (livingTeam.canSeeFriendlyInvisibles() || visible);
                case HIDE_FOR_OWN_TEAM -> cameraTeam == null ? visible : !livingTeam.isAlliedTo(cameraTeam) && visible;
            };
        }

        return visible;
    }

    static List<ItemStack> getIcons(Entity e) {

        if (e instanceof LivingEntity en) {
            return Load.Unit(en).getStatusEffectsData().exileMap.entrySet().stream()
                    .map(x -> new ItemStack(ExileDB.ExileEffects().get(x.getKey()).getEffectDisplayItem(), x.getValue().stacks)).collect(Collectors.toList());
        }
        return Arrays.asList();

    }

    public static void hookRender(Entity entity, PoseStack poseStack, MultiBufferSource buffers,
                                  Quaternionf cameraOrientation) {
        final Minecraft mc = Minecraft.getInstance();

        if (!(entity instanceof LivingEntity living) || (!living.getPassengers().isEmpty() && living.getPassengers().get(0) instanceof LivingEntity)) {
            // TODO handle mob stacks properly
            return;
        }

        if (!shouldShowPlate(living, mc.gameRenderer.getMainCamera().getEntity())) {
            return;
        }

        // Constants
        final int light = 0xF000F0;
        final float globalScale = 0.0267F;
        final float textScale = 0.5F;
        final int barHeight = NeatConfig.instance.barHeight();
        final boolean boss = isBoss(living);

        List<ItemStack> icons = getIcons(entity);

        String lvl = "Lvl " + Load.Unit(living).getLevel();

        String prefix = "";
        String suffix = "";

        for (MobAffix affix : Load.Unit(entity).getAffixData().getAffixes()) {

            if (affix.type.isPrefix()) {

                prefix += CLOC.translate(affix.locName());
            } else {

                suffix += CLOC.translate(affix.locName());

            }
        }

        String rar = I18n.get(Load.Unit(living).getMobRarity().locName().getString());

        ChatFormatting color = Load.Unit(living).getMobRarity().textFormatting();

        if (living instanceof Player) {
            rar = "";
            color = ChatFormatting.RED;
        }

        String name = ChatFormatting.YELLOW + lvl + " " + color + rar + " " + prefix + " " + living.getDisplayName().getString() + " " + suffix;

        final float nameLen = (mc.font.width(name) + (icons.size() * 5)) * textScale;
        final float halfSize = Math.max(NeatConfig.instance.plateSize(), nameLen / 2.0F + 10.0F);

        poseStack.pushPose();
        poseStack.translate(0, living.getBbHeight() + NeatConfig.instance.heightAbove(), 0);
        poseStack.mulPose(cameraOrientation);

        // Plate background, bars, and text operate with globalScale, but icons don't
        poseStack.pushPose();
        poseStack.scale(-globalScale, -globalScale, globalScale);

        // Background
        if (NeatConfig.instance.drawBackground()) {
            float padding = NeatConfig.instance.backgroundPadding();
            int bgHeight = NeatConfig.instance.backgroundHeight();
            VertexConsumer builder = buffers.getBuffer(NeatRenderType.BAR_TEXTURE_TYPE);
            builder.vertex(poseStack.last().pose(), -halfSize - padding, -bgHeight, 0.01F).color(0, 0, 0, 64).uv(0.0F, 0.0F).uv2(light).endVertex();
            builder.vertex(poseStack.last().pose(), -halfSize - padding, barHeight + padding, 0.01F).color(0, 0, 0, 64).uv(0.0F, 0.5F).uv2(light).endVertex();
            builder.vertex(poseStack.last().pose(), halfSize + padding, barHeight + padding, 0.01F).color(0, 0, 0, 64).uv(1.0F, 0.5F).uv2(light).endVertex();
            builder.vertex(poseStack.last().pose(), halfSize + padding, -bgHeight, 0.01F).color(0, 0, 0, 64).uv(1.0F, 0.0F).uv2(light).endVertex();
        }

        // Health Bar
        {
            int argb = getColor(living, NeatConfig.instance.colorByType(), boss);
            int r = (argb >> 16) & 0xFF;
            int g = (argb >> 8) & 0xFF;
            int b = argb & 0xFF;
            // There are scenarios in vanilla where the current health
            // can temporarily exceed the max health.
            float maxHealth = Math.max(living.getHealth(), living.getMaxHealth());
            float healthHalfSize = halfSize * (living.getHealth() / maxHealth);

            VertexConsumer builder = buffers.getBuffer(NeatRenderType.BAR_TEXTURE_TYPE);
            builder.vertex(poseStack.last().pose(), -halfSize, 0, 0.001F).color(r, g, b, 127).uv(0.0F, 0.75F).uv2(light).endVertex();
            builder.vertex(poseStack.last().pose(), -halfSize, barHeight, 0.001F).color(r, g, b, 127).uv(0.0F, 1.0F).uv2(light).endVertex();
            builder.vertex(poseStack.last().pose(), -halfSize + 2 * healthHalfSize, barHeight, 0.001F).color(r, g, b, 127).uv(1.0F, 1.0F).uv2(light).endVertex();
            builder.vertex(poseStack.last().pose(), -halfSize + 2 * healthHalfSize, 0, 0.001F).color(r, g, b, 127).uv(1.0F, 0.75F).uv2(light).endVertex();

            // Blank part of the bar
            if (healthHalfSize < halfSize) {
                builder.vertex(poseStack.last().pose(), -halfSize + 2 * healthHalfSize, 0, 0.001F).color(0, 0, 0, 127).uv(0.0F, 0.5F).uv2(light).endVertex();
                builder.vertex(poseStack.last().pose(), -halfSize + 2 * healthHalfSize, barHeight, 0.001F).color(0, 0, 0, 127).uv(0.0F, 0.75F).uv2(light).endVertex();
                builder.vertex(poseStack.last().pose(), halfSize, barHeight, 0.001F).color(0, 0, 0, 127).uv(1.0F, 0.75F).uv2(light).endVertex();
                builder.vertex(poseStack.last().pose(), halfSize, 0, 0.001F).color(0, 0, 0, 127).uv(1.0F, 0.5F).uv2(light).endVertex();
            }
        }

        // Text
        {
            final int white = 0xFFFFFF;
            final int black = 0;

            // Name
            {
                poseStack.pushPose();
                poseStack.translate(-halfSize, -4.5F, 0F);
                poseStack.scale(textScale, textScale, textScale);
                mc.font.drawInBatch(name, 0, 0, white, false, poseStack.last().pose(), buffers, Font.DisplayMode.NORMAL, black, light);
                poseStack.popPose();
            }

            // Health values (and debug ID)
            {
                final float healthValueTextScale = 0.75F * textScale;
                poseStack.pushPose();
                poseStack.translate(-halfSize, -4.5F, 0F);
                poseStack.scale(healthValueTextScale, healthValueTextScale, healthValueTextScale);

                int h = NeatConfig.instance.hpTextHeight();

                if (NeatConfig.instance.showCurrentHP()) {
                    String hpStr = HEALTH_FORMAT.format(HealthUtils.getCurrentHealth(living));
                    mc.font.drawInBatch(hpStr, 2, h, white, false, poseStack.last().pose(), buffers, Font.DisplayMode.NORMAL, black, light);
                }
                if (NeatConfig.instance.showMaxHP()) {
                    String maxHpStr = ChatFormatting.BOLD + HEALTH_FORMAT.format(HealthUtils.getMaxHealth(living));
                    mc.font.drawInBatch(maxHpStr, (int) (halfSize / healthValueTextScale * 2) - mc.font.width(maxHpStr) - 2, h, white, false, poseStack.last().pose(), buffers, Font.DisplayMode.NORMAL, black, light);
                }
                if (NeatConfig.instance.showPercentage()) {
                    String percStr = (int) (100 * living.getHealth() / living.getMaxHealth()) + "%";
                    mc.font.drawInBatch(percStr, (int) (halfSize / healthValueTextScale) - mc.font.width(percStr) / 2.0F, h, white, false, poseStack.last().pose(), buffers, Font.DisplayMode.NORMAL, black, light);
                }
                if (NeatConfig.instance.enableDebugInfo() && mc.options.renderDebug) {
                    var id = ForgeRegistries.ENTITY_TYPES.getKey(living.getType());
                    mc.font.drawInBatch("ID: \"" + id + "\"", 0, h + 16, white, false, poseStack.last().pose(), buffers, Font.DisplayMode.NORMAL, black, light);
                }
                poseStack.popPose();
            }
        }

        poseStack.popPose(); // Remove globalScale

        // Icons
        {
            final float zBump = -0.1F;
            poseStack.pushPose();

            float iconOffset = 2.85F;
            float zShift = 0F;

            // todo
            for (ItemStack icon : icons) {
                renderIcon(living.level(), icon, poseStack, buffers, globalScale, halfSize, iconOffset, zShift);
                iconOffset += 5F;
                zShift += zBump;
            }


            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private static void renderIcon(Level level, ItemStack icon, PoseStack poseStack,
                                   MultiBufferSource buffers, float globalScale, float halfSize, float leftShift, float zShift) {
        if (!icon.isEmpty()) {
            final float iconScale = 0.12F;
            poseStack.pushPose();
            // halfSize and co. are units operating under the assumption of globalScale,
            // but in the icon rendering section we don't use globalScale, so we need
            // to manually multiply it in to ensure the units line up.
            float dx = (halfSize - leftShift) * globalScale;
            float dy = 3F * globalScale;
            float dz = zShift * globalScale;
            // Need to negate X due to our rotation below
            poseStack.translate(-dx, dy, dz);
            poseStack.scale(iconScale, iconScale, iconScale);
            poseStack.mulPose(Axis.YP.rotationDegrees(180F));


            Minecraft.getInstance().getItemRenderer()
                    .renderStatic(icon, ItemDisplayContext.NONE, 0xF000F0, OverlayTexture.NO_OVERLAY, poseStack, buffers, level, 0);
            poseStack.popPose();
        }
    }
}