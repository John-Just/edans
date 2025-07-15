package net.john_just.edans.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.john_just.edans.skill.PlayerSkills;
import net.john_just.edans.skill.SkillNode;
import net.john_just.edans.skill.SkillTreeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Загрузчик JSON-деревьев навыков из data/<namespace>/skill_trees/*.json
 */
public final class SkillTreeLoader extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    public static final SkillTreeLoader INSTANCE = new SkillTreeLoader();

    private SkillTreeLoader() {
        super(GSON, "skill_trees");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files,
                         ResourceManager resourceManager,
                         ProfilerFiller profiler) {
        // Очищаем старые данные
        SkillTreeRegistry.clear();

        for (var entry : files.entrySet()) {
            ResourceLocation loc = entry.getKey();
            String path = loc.getPath();
            final String prefix = "skill_trees/";
            if (!path.startsWith(prefix)) {
                LOGGER.warn("Unexpected resource path for skill tree: {}", path);
                continue;
            }
            // Извлекаем имя навыка из имени файла: skill_trees/<skill>.json
            String skillName = path.substring(prefix.length());
            PlayerSkills.SkillType type;
            try {
                type = PlayerSkills.SkillType.valueOf(skillName.toUpperCase());
            } catch (IllegalArgumentException ex) {
                LOGGER.error("Unknown skill type '{}' in skill tree loader", skillName);
                continue;
            }

            // Парсим JSON внутри файла
            JsonObject root = entry.getValue().getAsJsonObject();
            Map<String, SkillNode> nodes = new HashMap<>();

            for (var nodeEntry : root.entrySet()) {
                String nodeId = nodeEntry.getKey();
                JsonObject obj = nodeEntry.getValue().getAsJsonObject();
                String displayName = obj.get("name").getAsString();

                Set<String> prereq = new HashSet<>();
                if (obj.has("prereq")) {
                    JsonArray arr = obj.getAsJsonArray("prereq");
                    for (JsonElement e : arr) {
                        prereq.add(e.getAsString());
                    }
                }
                nodes.put(nodeId, new SkillNode(nodeId, displayName, prereq));
            }

            // Сохраняем в реестр
            SkillTreeRegistry.apply(type, nodes);
            LOGGER.info("Loaded skill tree for {}: {} nodes", type, nodes.size());
        }
    }
}
