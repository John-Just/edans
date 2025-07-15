package net.john_just.edans.network;

import net.john_just.edans.EdansMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/** Сервер → клиент: актуальные репутация, деньги и XP навыков */
public record ProfileSyncPacket(
        int reputation,
        int money,
        Map<String, Float> skills
) implements CustomPacketPayload {

    /*── обязательный TYPE ───────────────────────────────*/
    public static final Type<ProfileSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    EdansMod.MOD_ID, "profile_sync"));

    /*── StreamCodec (используется при регистрации) ──────*/
    public static final StreamCodec<FriendlyByteBuf, ProfileSyncPacket> CODEC =
            StreamCodec.of(
                    // encode
                    (buf, p) -> {
                        buf.writeInt(p.reputation());
                        buf.writeInt(p.money());
                        buf.writeVarInt(p.skills().size());
                        p.skills().forEach((k, v) -> {
                            buf.writeUtf(k);
                            buf.writeFloat(v);
                        });
                    },
                    // decode
                    buf -> {
                        int rep = buf.readInt();
                        int money = buf.readInt();
                        int size = buf.readVarInt();
                        Map<String, Float> map = new HashMap<>(size);
                        for (int i = 0; i < size; i++) {
                            map.put(buf.readUtf(32_767), buf.readFloat());
                        }
                        return new ProfileSyncPacket(rep, money, map);
                    });

    /*── реализация интерфейса ───────────────────────────*/
    public StreamCodec<FriendlyByteBuf, ?> codec() {   // сериализация
        return CODEC;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {   // идентификатор
        return TYPE;
    }
}
