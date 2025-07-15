package net.john_just.edans.chat;

import net.john_just.edans.chat.role.RoleManager;
import net.john_just.edans.data.PlayerProfile;
import net.john_just.edans.data.PlayerProfileManager;
import net.john_just.edans.event.PlayerConnectionHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Objects;

public class ChatHandler {
    public static final ChatHandler INSTANCE = new ChatHandler(); // singleton

    private int chatRadius = 30;

    @SubscribeEvent
    public void onChat(ServerChatEvent event) {
        ServerPlayer sender = event.getPlayer();
        PlayerProfile senderProfile = PlayerProfileManager.get(sender);
        String msg = event.getMessage().getString();

        for (ServerPlayer receiver : sender.server.getPlayerList().getPlayers()) {
            if (receiver.level() != sender.level()) continue;

            double distance = receiver.position().distanceTo(sender.position());
            if (distance > chatRadius) continue;

            PlayerProfile receiverProfile = PlayerProfileManager.get(receiver);

            // Проверяем, знаком ли receiver с sender
            String displayName;
            if (receiver.getUUID().equals(sender.getUUID()) || receiverProfile.knownPlayers.contains(sender.getUUID().toString())) {
                // Показываем реальное имя
                displayName = senderProfile.characterName != null ? senderProfile.characterName : sender.getName().getString();
            } else {
                // Показываем "незнакомец"
                displayName = "незнакомец";
            }

            Component formatted = Component.literal("")
                    .append(displayName)
                    .append(": ")
                    .append(Component.literal(msg).withStyle(ChatFormatting.WHITE));

            receiver.sendSystemMessage(formatted);

        }

        event.setCanceled(true); // отключить стандартный глобальный чат
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        WelcomeService.sendWelcomeMessage(event);
    }

    // ГЕТТЕРЫ
    public int getChatRadius() {
        return chatRadius;
    }

    // СЕТТЕРЫ
    public void setChatRadius(int chatRadius) {
        this.chatRadius = chatRadius;
    }
}

