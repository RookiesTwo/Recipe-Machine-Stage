package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.irons_spell_books;

import dev.behindthescenery.sdmrecipemachinestages.compat.RMSScrollForgeRules;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.gui.scroll_forge.ScrollForgeMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScrollForgeMenu.class)
public abstract class ScrollForgeMenuMixin extends AbstractContainerMenu {
    @Shadow @Final private Slot resultSlot;
    @Shadow private AbstractSpell spellRecipeSelection;
    @Unique private Player rms$player;

    protected ScrollForgeMenuMixin(MenuType<?> type, int id) { super(type, id); }

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/level/block/entity/BlockEntity;)V", at = @At("RETURN"))
    private void rms$capturePlayer(int id, Inventory inventory, BlockEntity entity, CallbackInfo ci) {
        rms$player = inventory.player;
    }

    @Inject(method = "setupResultSlot", at = @At("HEAD"), cancellable = true, remap = false)
    private void rms$checkCrafting(AbstractSpell spell, CallbackInfo ci) {
        if (!RMSScrollForgeRules.isUnlocked(rms$player, spell.getSpellId())) {
            rms$clearResult();
            ci.cancel();
        }
    }

    @Unique
    private void rms$clearResult() {
        spellRecipeSelection = SpellRegistry.none();
        if (resultSlot.hasItem()) resultSlot.set(ItemStack.EMPTY);
    }

    @Unique
    private boolean rms$canTakeResult(Player player) {
        ItemStack result = resultSlot.getItem();
        if (!ISpellContainer.isSpellContainer(result)) return true;
        for (var spell : ISpellContainer.get(result).getAllSpells()) {
            if (!RMSScrollForgeRules.isUnlocked(player, spell.getSpell().getSpellId())) return false;
        }
        return true;
    }

    // The result inventory is shared by viewers. Check its actual spell, not only this menu's selection.
    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (!rms$canTakeResult(player)) rms$clearResult();
        super.clicked(slotId, button, clickType, player);
    }

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void rms$checkQuickMove(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        if (index == resultSlot.index && !rms$canTakeResult(player)) {
            rms$clearResult();
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Override
    public void broadcastChanges() {
        if (rms$player != null && !rms$player.level().isClientSide()
                && !RMSScrollForgeRules.isUnlocked(rms$player, spellRecipeSelection.getSpellId())) {
            rms$clearResult();
        }
        super.broadcastChanges();
    }
}
