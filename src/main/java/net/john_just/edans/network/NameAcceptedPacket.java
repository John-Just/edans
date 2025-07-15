package net.john_just.edans.network;

import net.john_just.edans.EdansMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

/** Сервер ➜ клиент: подтверждение, что имя сохранено */
public enum NameAcceptedPacket implements CustomPacketPayload {
    OK;  // singleton

    public static final Type<NameAcceptedPacket> TYPE =
            new Type<>(fromNamespaceAndPath(EdansMod.MOD_ID, "name_ok"));

    public static final StreamCodec<FriendlyByteBuf, NameAcceptedPacket> STREAM_CODEC =
            StreamCodec.unit(OK);   // данных нет

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
