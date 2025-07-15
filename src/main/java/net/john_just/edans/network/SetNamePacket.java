package net.john_just.edans.network;

import net.john_just.edans.EdansMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Клиент → сервер: игрок ввёл имя персонажа */
public record SetNamePacket(String name) implements CustomPacketPayload {

    /* ---------- обязательные поля ---------- */

    /** Уникальный идентификатор пакета */
    public static final Type<SetNamePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(EdansMod.MOD_ID, "set_name"));

    /** Кодек: один UTF-стринг (max 32 767) */
    public static final StreamCodec<FriendlyByteBuf, SetNamePacket> STREAM_CODEC =
            StreamCodec.of(
                    // encode
                    (buf, pkt) -> buf.writeUtf(pkt.name(), 32_767),
                    // decode
                    buf -> new SetNamePacket(buf.readUtf(32_767))
            );

    /* ---------- реализация интерфейса ---------- */
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
