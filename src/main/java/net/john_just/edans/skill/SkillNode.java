package net.john_just.edans.skill;

import java.util.Set;

/** Описывает одну вершину в дереве навыков */
public record SkillNode(String id, String name, Set<String> prereq) {

}
