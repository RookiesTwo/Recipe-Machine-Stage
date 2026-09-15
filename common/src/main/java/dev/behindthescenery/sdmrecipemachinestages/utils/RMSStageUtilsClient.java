package dev.behindthescenery.sdmrecipemachinestages.utils;

import dev.behindthescenery.sdmrecipemachinestages.data.RecipeBlockType;
import com.alessandro.astages.api.holder.AClientHolder;
import com.alessandro.astages.api.util.AStagesClientUtils;

public class RMSStageUtilsClient {

    public static boolean isUnlocked(RecipeBlockType blockType) {
        return hasStage(blockType.stageId());
    }

    public static boolean hasStage(String stageId) {
        return AStagesClientUtils.hasStage(AClientHolder.serverAndPlayer(), stageId);
    }
}
