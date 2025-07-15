// net/john_just/edans/network/NetworkInit.java
package net.john_just.edans.network;

import net.john_just.edans.ClientCache;
import net.john_just.edans.EdansMod;
import net.john_just.edans.data.PlayerProfileManager;
import net.john_just.edans.data.PlayerProfile;
import net.john_just.edans.ui.journal.JournalScreen;
import net.john_just.edans.ui.screens.NameScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(
        modid = EdansMod.MOD_ID,
        bus   = EventBusSubscriber.Bus.MOD)   // ← ОБЯЗАТЕЛЬНО указываем MOD!
public final class NetworkInit {

    public static final String NET_VERSION = "29w2d";

    /** Здесь регистрируем все CustomPacketPayload мода */
    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent evt) {
        // версия сети — любая строка; меняется при изменениях протокола
        PayloadRegistrar reg = evt.registrar(NET_VERSION);

        /* ---------- server → client ---------- */
        reg.playToClient(
                RequestNamePacket.TYPE,
                RequestNamePacket.STREAM_CODEC,
                (pkt, ctx) -> ctx.enqueueWork(() ->
                        Minecraft.getInstance().setScreen(new NameScreen()))
        );

        /* ---------- client → server ---------- */
        reg.playToServer(
                SetNamePacket.TYPE,
                SetNamePacket.STREAM_CODEC,
                (pkt, ctx) -> ctx.enqueueWork(() -> {
                    Player p = ctx.player();
                    if (!(p instanceof ServerPlayer player)) return;

                    // Сохранение имени в профиль
                    PlayerProfile prof = PlayerProfileManager.get(player);
                    prof.characterName = pkt.name();
                    prof.save(player.getServer());

                    // Отправка подтверждения игроку
                    PacketDistributor.sendToPlayer(player, NameAcceptedPacket.OK);
                })
        );

        reg.playToClient(
                NameAcceptedPacket.TYPE,
                NameAcceptedPacket.STREAM_CODEC,
                (pkt, ctx) -> ctx.enqueueWork(() ->
                        Minecraft.getInstance()
                                .gui.getChat()
                                .addMessage(Component.literal("Имя сохранено! // net/john_just/edans/network/NetworkInit.java playToClient")))
        );

        // NetworkInit.onRegisterPayloads
        reg.playToClient(
                ProfileSyncPacket.TYPE,        // 1-й аргумент Type
                ProfileSyncPacket.CODEC,       // 2-й StreamCodec
                (pkt, ctx) -> ctx.enqueueWork(() -> {
                    ClientCache.profileLocal = pkt;         // сохраняем данные
                    // если открыт дневник – перерисуем профиль
                    if (Minecraft.getInstance().screen instanceof JournalScreen js)
                        js.refreshProfileTab();
                })
        );


    }
}
