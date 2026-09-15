package dev.behindthescenery.sdmrecipemachinestages.compat;

import dev.architectury.networking.NetworkManager;
import dev.behindthescenery.sdmrecipemachinestages.RMSMain;
import dev.behindthescenery.sdmrecipemachinestages.data.RMSContainer;
import dev.behindthescenery.sdmrecipemachinestages.data.SpellStageRules;
import dev.behindthescenery.sdmrecipemachinestages.network.SyncScrollForgeRulesS2C;
import dev.behindthescenery.sdmrecipemachinestages.utils.RMSUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public final class RMSScrollForgeRules {
    private static final SpellStageRules SERVER = new SpellStageRules();
    private static final SpellStageRules CLIENT = new SpellStageRules();
    private static long clientRevision;

    private RMSScrollForgeRules() { }

    public static void register(String spellId, String stage) {
        String id = ResourceLocation.parse(spellId).toString();
        SERVER.register(id, stage);
        if (!RMSContainer.Instance.isReloading()) syncAll();
    }

    public static void clearServer() {
        SERVER.replace(Map.of());
    }

    public static void receive(Map<String, String> rules) {
        CLIENT.replace(rules);
        clientStagesChanged();
    }

    public static void clientStagesChanged() {
        clientRevision++;
    }

    public static long clientRevision() {
        return clientRevision;
    }

    public static boolean isUnlocked(Player player, String spellId) {
        if (player == null) return false;
        if (player.level().isClientSide()) return isUnlockedClient(spellId);
        return SERVER.isUnlocked(spellId, stage -> RMSUtils.hasPlayerStage(player, stage));
    }

    public static boolean isUnlockedClient(String spellId) {
        return CLIENT.isUnlocked(spellId, RMSUtils::hasClientStage);
    }

    public static void sendTo(ServerPlayer player) {
        NetworkManager.sendToPlayer(player, new SyncScrollForgeRulesS2C(SERVER.snapshot()));
    }

    public static void syncAll() {
        var server = RMSMain.getServer();
        if (server != null) server.getPlayerList().getPlayers().forEach(RMSScrollForgeRules::sendTo);
    }
}
