package dev.behindthescenery.sdmrecipemachinestages.neoforge.compat;

import net.minecraft.core.BlockPos;

/** Access packet data without referencing a Mixin implementation at runtime. */
public interface ScrollForgePacketData {
    BlockPos rms$getPosition();
    String rms$getSpellId();
}
