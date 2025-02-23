package com.robertx22.age_of_exile.vanilla_mc.packets;

import com.robertx22.age_of_exile.mmorpg.SlashRef;
import com.robertx22.age_of_exile.uncommon.utilityclasses.ClientOnly;
import com.robertx22.library_of_exile.main.MyPacket;
import com.robertx22.library_of_exile.packets.ExilePacketContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class TotemAnimationPacket extends MyPacket<TotemAnimationPacket> {
    ItemStack stack;

    public TotemAnimationPacket(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return new ResourceLocation(SlashRef.MODID, "totemanim");
    }

    @Override
    public void loadFromData(FriendlyByteBuf tag) {
        stack = tag.readItem();

        if (stack == null) {
            stack = ItemStack.EMPTY;
        }
    }

    @Override
    public void saveToData(FriendlyByteBuf tag) {
        if (stack != null) {
            tag.writeItem(stack);
        }
    }

    @Override
    public void onReceived(ExilePacketContext ctx) {
        ClientOnly.totemAnimWithItem(stack);
    }

    @Override
    public MyPacket<TotemAnimationPacket> newInstance() {
        return new TotemAnimationPacket();
    }

    public TotemAnimationPacket() {
    }

}