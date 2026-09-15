package dev.behindthescenery.sdmrecipemachinestages.neoforge.compat;

import com.alessandro.astages.api.event.sync.ClientSynchronizeServerStagesEvent;
import com.alessandro.astages.api.event.sync.ClientSynchronizeStagesEvent;
import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.SdmRecipeMachineStages;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/** Refresh recipe viewers after AStages has applied player or server stage changes. */
@EventBusSubscriber(modid = SdmRecipeMachineStages.MOD_ID, value = Dist.CLIENT)
public final class RMSAStagesClientEvents {

    private RMSAStagesClientEvents() { }

    @SubscribeEvent
    public static void onPlayerStagesSync(ClientSynchronizeStagesEvent event) {
        Minecraft.getInstance().execute(RMSMain::onStageSync);
    }

    @SubscribeEvent
    public static void onServerStagesSync(ClientSynchronizeServerStagesEvent event) {
        Minecraft.getInstance().execute(RMSMain::onStageSync);
    }
}
