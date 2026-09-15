package dev.behindthescenery.sdmrecipemachinestages.network;

import dev.architectury.networking.NetworkManager;
import dev.behindthescenery.sdmrecipemachinestages.SdmRecipeMachineStages;
import dev.behindthescenery.sdmrecipemachinestages.compat.RMSScrollForgeRules;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/** A full snapshot also removes stale rules after reload or reconnect. */
public record SyncScrollForgeRulesS2C(Map<String, String> rules) implements CustomPacketPayload {
    public static final Type<SyncScrollForgeRulesS2C> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(SdmRecipeMachineStages.MOD_ID, "scroll_forge_rules"));
    public static final StreamCodec<ByteBuf, SyncScrollForgeRulesS2C> STREAM_CODEC =
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8)
                    .map(SyncScrollForgeRulesS2C::new, packet -> new HashMap<>(packet.rules()));

    public SyncScrollForgeRulesS2C {
        rules = Map.copyOf(rules);
    }

    public static void handle(SyncScrollForgeRulesS2C packet, NetworkManager.PacketContext context) {
        context.queue(() -> RMSScrollForgeRules.receive(packet.rules));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
