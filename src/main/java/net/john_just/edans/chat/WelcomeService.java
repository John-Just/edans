package net.john_just.edans.chat;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class WelcomeService {

    public static void sendWelcomeMessage(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();

        // Пример с кастомным приветствием для определённого игрока
        String username = player.getGameProfile().getName();

        if (username.equalsIgnoreCase("Dev")) {
            player.sendSystemMessage(Component.literal("Добро пожаловать, разработчик!"));
        } else {
            player.sendSystemMessage(Component.literal("Добро пожаловать на сервер, " + username + "!"));
        }

        // Можно добавить лог, звуки, титры, статистику и т.д.
    }
}
