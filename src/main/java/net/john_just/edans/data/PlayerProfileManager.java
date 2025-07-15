package net.john_just.edans.data;

import net.john_just.edans.skill.PlayerSkills;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.john_just.edans.ClientCache;

public class PlayerProfileManager {
    private static final Map<UUID, PlayerProfile> profiles = new ConcurrentHashMap<>();

    public static PlayerProfile get(ServerPlayer player) {
        return profiles.get(player.getUUID());
    }

    public static void load(ServerPlayer player) {
        PlayerProfile profile = PlayerProfile.load(player);
        profiles.put(player.getUUID(), profile);
    }

    public static void save(ServerPlayer player) {
        PlayerProfile profile = profiles.get(player.getUUID());
        if (profile != null) {
            profile.save(player.getServer());
        }
    }

    public static void unload(ServerPlayer player) {
        save(player);
        profiles.remove(player.getUUID());
    }

    // PlayerProfileManager (client-only)
    @OnlyIn(Dist.CLIENT)
    public static PlayerProfile getClient() {
        var pkt = ClientCache.profileLocal;
        if (pkt == null) return null;

        // создаём «псевдо-профиль» только с теми полями, которые нужны вкладке
        PlayerProfile p = new PlayerProfile("local", Minecraft.getInstance().getUser().getName());
        p.characterName = Minecraft.getInstance().getUser().getName(); // или из NameScreen
        p.reputation    = pkt.reputation();
        p.money         = pkt.money();
        // навыки: из Map<String,Float>
        pkt.skills().forEach((k,v) -> p.skills.addXp(PlayerSkills.SkillType.valueOf(k), v));
        return p;
    }
}
