package net.john_just.edans.skill;

import com.google.gson.JsonObject;
import java.util.EnumMap;
import java.util.Map;

public class SkillTreeRegistry {
    // для каждого типа навыка свою карту узлов
    private static final Map<PlayerSkills.SkillType, Map<String, SkillNode>> TREES =
            new EnumMap<>(PlayerSkills.SkillType.class);

    public static void clear() {
        TREES.clear();
    }

    /** Вызывается из загрузчика JSON-ресурсов */
    public static void apply(PlayerSkills.SkillType type, Map<String, SkillNode> nodes) {
        // распарсить JsonObject root в Map<String, SkillNode> для данного type
        // и положить в TREES.put(type, parsedMap);
        TREES.put(type, nodes);
    }

    public static SkillNode getNode(PlayerSkills.SkillType type, String nodeId) {
        Map<String, SkillNode> tree = TREES.get(type);
        return tree == null ? null : tree.get(nodeId);
    }
}
