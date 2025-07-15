package net.john_just.edans.skill;

import java.util.EnumMap;
import java.util.Map;

public class PlayerSkills {
    public enum SkillType {
        WOODCUTTING, MINING, FARMING,
        FISHING, COMBAT, MAGIC, SMITHING
    }

    // вместо HashMap<SkillType,Float>
    private final Map<SkillType, SkillProgress> skills = new EnumMap<>(SkillType.class);

    public PlayerSkills() {
        for (SkillType t : SkillType.values()) {
            skills.put(t, new SkillProgress());
        }
    }

    /** Добавить XP конкретному навыку */
    public void addXp(SkillType type, float amount) {
        skills.get(type).addXp(amount);
    }

    /** Получить прогресс по навыку */
    public SkillProgress getProgress(SkillType type) {
        return skills.get(type);
    }

    /** Для сериализации/десериализации через GSON */
    public Map<SkillType, SkillProgress> getSkills() {
        return skills;
    }
}
