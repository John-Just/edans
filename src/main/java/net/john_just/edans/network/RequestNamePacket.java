package net.john_just.edans.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.john_just.edans.EdansMod;

/** Сервер → клиент: просто сигнал «открой экран имени» */
public enum RequestNamePacket implements CustomPacketPayload {
    INSTANCE;

    /* ==== обязательные поля/методы ==== */

    /**
     * Уникальный идентификатор пакета
     */
    public static final Type<RequestNamePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(EdansMod.MOD_ID, "req_name"));

    /**
     * Кодек: данных нет, поэтому unit-кодек
     */
    public static final StreamCodec<FriendlyByteBuf, RequestNamePacket> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {   // требуется контрактом
        return TYPE;
    }
}
