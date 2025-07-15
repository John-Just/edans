package net.john_just.edans.event;

import net.john_just.edans.data.PlayerProfile;
import net.john_just.edans.data.PlayerProfileManager;
import net.john_just.edans.network.ProfileSyncPacket;
import net.john_just.edans.network.RequestNamePacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

public class PlayerConnectionHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        PlayerProfileManager.load(player);

        PlayerProfile profile = PlayerProfileManager.get(player);
        if (profile.characterName == null || profile.characterName.isBlank()) {
            PacketDistributor.sendToPlayer(player, RequestNamePacket.INSTANCE);
        }

        // превращаем XP-карту в Map<String,Float>
        Map<String, Float> xpMap = new HashMap<>();
        profile.skills.getSkills()               // ← Map<SkillType, Float>
                .forEach((type, xp) -> xpMap.put(type.name(), xp.getXp()));

        // шлём CustomPayload напрямую соединению игрока
        player.connection.send(new ProfileSyncPacket(
                profile.reputation,
                profile.money,
                xpMap));
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        PlayerProfileManager.unload(player);
    }
}
