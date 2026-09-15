package dev.behindthescenery.sdmrecipemachinestages.neoforge.mixin.recipes.irons_spell_books;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.behindthescenery.sdmrecipemachinestages.compat.RMSScrollForgeRules;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.gui.scroll_forge.ScrollForgeMenu;
import io.redspace.ironsspellbooks.gui.scroll_forge.ScrollForgeScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScrollForgeScreen.class)
public abstract class ScrollForgeScreenMixin extends AbstractContainerScreen<ScrollForgeMenu> {
    @Shadow private AbstractSpell selectedSpell;
    @Shadow public abstract void generateSpellList();
    @Shadow private void setSelectedSpell(AbstractSpell spell) { throw new AssertionError(); }
    @Unique private long rms$lastRevision = -1;

    protected ScrollForgeScreenMixin(ScrollForgeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @WrapOperation(method = "generateSpellList", at = @At(value = "INVOKE",
            target = "Lio/redspace/ironsspellbooks/api/spells/AbstractSpell;isEnabled()Z"), remap = false)
    private boolean rms$showUnlockedSpell(AbstractSpell spell, Operation<Boolean> original) {
        return original.call(spell) && RMSScrollForgeRules.isUnlockedClient(spell.getSpellId());
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        long revision = RMSScrollForgeRules.clientRevision();
        if (rms$lastRevision != revision) {
            rms$lastRevision = revision;
            if (!RMSScrollForgeRules.isUnlockedClient(selectedSpell.getSpellId())) {
                setSelectedSpell(SpellRegistry.none());
            }
            generateSpellList();
        }
    }
}
