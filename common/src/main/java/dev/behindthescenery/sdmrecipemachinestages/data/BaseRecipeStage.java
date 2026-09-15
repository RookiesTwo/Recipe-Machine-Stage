package dev.behindthescenery.sdmrecipemachinestages.data;

import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.server.level.ServerPlayer;

public interface BaseRecipeStage {

    default boolean hasStage(ServerPlayer serverPlayer) {
        return RMSUtils.hasPlayerStage(serverPlayer, stageId());
    }

    String stageId();

}
