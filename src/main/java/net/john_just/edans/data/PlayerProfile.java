package net.john_just.edans.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.john_just.edans.skill.PlayerSkills;
import net.john_just.edans.skill.SkillProgress;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Профиль игрока с поддержкой версий и миграции старых форматов.
 */
public class PlayerProfile {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerProfile.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int CURRENT_VERSION = 2;

    public int version = CURRENT_VERSION;
    public String uuid;
    public String nickname;
    public String characterName;
    public int reputation;
    public int money;
    public List<String> knownPlayers;
    public PlayerSkills skills;

    public PlayerProfile(String uuid, String nickname) {
        this.uuid = uuid;
        this.nickname = nickname;
        this.characterName = null;
        this.reputation = 0;
        this.money = 0;
        this.knownPlayers = new ArrayList<>();
        this.skills = new PlayerSkills();
    }

    /**
     * Загружает профиль, мигрирует старые версии и сохраняет в новом формате.
     */
    public static PlayerProfile load(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        File profileDir = new File(server.getWorldPath(LevelResource.ROOT).toFile(), "edans_profiles");
        try {
            if (!profileDir.exists()) profileDir.mkdirs();
            File file = new File(profileDir, player.getUUID() + ".json");

            if (!file.exists()) {
                // Новый профиль
                PlayerProfile profile = new PlayerProfile(player.getUUID().toString(), player.getName().getString());
                profile.ensureAllSkillsPresent();
                profile.save(server);
                return profile;
            }

            // Читаем JSON-дерево для анализа версии
            JsonObject root;
            try (FileReader fr = new FileReader(file)) {
                root = JsonParser.parseReader(fr).getAsJsonObject();
            }
            int fileVersion = root.has("version") ? root.get("version").getAsInt() : 1;
            PlayerProfile profile;
            if (fileVersion < CURRENT_VERSION) {
                profile = migrate(root);
                profile.ensureAllSkillsPresent();
                profile.version = CURRENT_VERSION;
                profile.save(server);
            } else {
                // Десериализация текущего формата
                try (FileReader fr = new FileReader(file)) {
                    profile = GSON.fromJson(fr, PlayerProfile.class);
                }
            }
            // Гарантировать наличие всех навыков
            profile.ensureAllSkillsPresent();
            return profile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load player profile", e);
        }
    }

    /**
     * Сохраняет профиль в файл.
     */
    public void save(MinecraftServer server) {
        File profileDir = new File(server.getWorldPath(LevelResource.ROOT).toFile(), "edans_profiles");
        try {
            if (!profileDir.exists()) profileDir.mkdirs();
            File file = new File(profileDir, uuid + ".json");
            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            LOGGER.error("Ошибка сохранения профиля {}", uuid, e);
        }
    }

    /**
     * Обеспечивает наличие SkillProgress для каждого SkillType.
     */
    public void ensureAllSkillsPresent() {
        for (PlayerSkills.SkillType type : PlayerSkills.SkillType.values()) {
            if (skills.getProgress(type) == null) {
                skills.getSkills().put(type, new SkillProgress());
            }
        }
    }

    /**
     * Основная точка миграции для всех старых версий.
     */
    private static PlayerProfile migrate(JsonObject root) {
        int from = root.has("version") ? root.get("version").getAsInt() : 1;
        PlayerProfile profile = null;
        if (from == 1) {
            profile = migrateFromV1(root);
            from = 2;
        }
        // если появится версия 3:
        // if (from == 2) { profile = migrateFromV2(root); from = 3; }
        return profile;
    }

    /**
     * Миграция из формата версии 1 (сырые XP в skills.skills[].xp) в текущий.
     */
    private static PlayerProfile migrateFromV1(JsonObject root) {
        // Базовые поля
        String uuid = root.get("uuid").getAsString();
        String nick = root.get("nickname").getAsString();
        PlayerProfile profile = new PlayerProfile(uuid, nick);
        if (root.has("characterName")) profile.characterName = root.get("characterName").getAsString();
        if (root.has("reputation")) profile.reputation = root.get("reputation").getAsInt();
        if (root.has("money")) profile.money = root.get("money").getAsInt();
        if (root.has("knownPlayers")) {
            JsonArray arr = root.getAsJsonArray("knownPlayers");
            for (JsonElement e : arr) profile.knownPlayers.add(e.getAsString());
        }
        // Старые навыки могли быть обёрнуты двойным "skills"
        JsonObject skillsWrapper = root.getAsJsonObject("skills");
        JsonObject oldSkills = skillsWrapper.has("skills") ? skillsWrapper.getAsJsonObject("skills") : skillsWrapper;
        PlayerSkills newSkills = new PlayerSkills();
        for (PlayerSkills.SkillType t : PlayerSkills.SkillType.values()) {
            if (oldSkills.has(t.name())) {
                JsonObject obj = oldSkills.getAsJsonObject(t.name());
                float xp = obj.has("xp") ? obj.get("xp").getAsFloat() : 0f;
                newSkills.addXp(t, xp);
            }
        }
        profile.skills = newSkills;
        return profile;
    }

    // private static PlayerProfile migrateFromV2(JsonObject root) { ... }
}
