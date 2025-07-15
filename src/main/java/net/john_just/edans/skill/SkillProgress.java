package net.john_just.edans.skill;

import java.util.HashSet;
import java.util.Set;

public final class SkillProgress {
    private float xp;              // накопленный XP
    private int   level;           // 0–100
    private int   points;          // нераспределённые очки
    private final Set<String> unlockedNodes = new HashSet<>();

    /** Добавляем XP, повышаем уровень и даём очко за каждый новый уровень */
    public void addXp(float amount) {
        xp += amount;
        // пока хватает XP на следующий уровень
        while (level < 100 && xp >= xpFor(level + 1)) {
            xp -= xpFor(level + 1);
            level++;
            points++;
        }
    }

    /** Сколько XP нужно, чтобы перейти на указанный уровень */
    private static float xpFor(int lvl) {
        // пример: квадратичный рост
        // lvl=1 → 25, lvl=2 → 100, lvl=3 → 225, …
        return 25f * lvl * lvl;
    }

    /** Пытаемся разблокировать узел дерева. Возвращает true, если получилось */
    public boolean unlockNode(String nodeId, Set<String> prereqs) {
        if (points <= 0) return false;             // нет очков
        if (unlockedNodes.contains(nodeId)) return false; // уже взят
        if (!unlockedNodes.containsAll(prereqs)) return false; // не все зависимости

        points--;
        unlockedNodes.add(nodeId);
        return true;
    }

    // Геттеры/сеттеры для сериализации
    public float getXp()        { return xp; }
    public int   getLevel()     { return level; }
    public int   getPoints()    { return points; }
    public Set<String> getUnlockedNodes() { return unlockedNodes; }
}
