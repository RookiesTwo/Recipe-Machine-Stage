package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.irons_spell_books;

import dev.behindthescenery.sdmrecipemachinestages.compat.RMSScrollForgeRules;
import dev.behindthescenery.sdmrecipemachinestages.neoforge.compat.ScrollForgePacketData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.gui.scroll_forge.ScrollForgeMenu;
import io.redspace.ironsspellbooks.network.ScrollForgeSelectSpellPacket;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScrollForgeSelectSpellPacket.class)
public abstract class ScrollForgeSelectSpellPacketMixin implements ScrollForgePacketData {
    @Shadow @Final private BlockPos pos;
    @Shadow @Final private String spellId;

    @Override @Unique
    public BlockPos rms$getPosition() { return pos; }

    @Override @Unique
    public String rms$getSpellId() { return spellId; }

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true, remap = false)
    private static void rms$selectForSender(ScrollForgeSelectSpellPacket packet, IPayloadContext context, CallbackInfo ci) {
        var data = (ScrollForgePacketData) packet;
        BlockPos pos = data.rms$getPosition();
        String spellId = data.rms$getSpellId();
        context.enqueueWork(() -> {
            var player = context.player();
            if (player.containerMenu instanceof ScrollForgeMenu menu
                    && menu.blockEntity.getBlockPos().equals(pos) && menu.stillValid(player)) {
                // Use the sender's menu rather than the block entity's most recently opened menu.
                menu.setRecipeSpell(RMSScrollForgeRules.isUnlocked(player, spellId)
                        ? SpellRegistry.getSpell(spellId) : SpellRegistry.none());
            }
        });
        ci.cancel();
    }
}
