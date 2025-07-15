package net.john_just.edans.chat.role;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RoleManager {
    private static final Map<UUID, String> roles = new HashMap<>();

    public static String getRole(ServerPlayer player) {
        return roles.getOrDefault(player.getUUID(), "Игрок");
    }

    public static void setRole(UUID uuid, String role) {
        roles.put(uuid, role);
    }

    public static Style getStyle(String role) {
        return switch (role) {
            case "Бог" -> Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE).withBold(true);
            case "Админ" -> Style.EMPTY.withColor(ChatFormatting.RED);
            default -> Style.EMPTY.withColor(ChatFormatting.GRAY);
        };
    }
}