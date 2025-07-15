package net.john_just.edans.skill;

import net.john_just.edans.data.PlayerProfileManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SkillTriggerRegistry {

    /* ── коллекции ────────────────────────── */
    public static final List<SkillTriggerEntry<BlockEvent.BreakEvent>>  BREAK_TRIGGERS  = new ArrayList<>();
    public static final List<SkillTriggerEntry<PlayerEvent.ItemCraftedEvent>> CRAFT_TRIGGERS = new ArrayList<>();

    /* ── очистка при перезагрузке JSON ─────── */
    public static void clearAll() {
        BREAK_TRIGGERS.clear();
        CRAFT_TRIGGERS.clear();
    }

    /* ── помощники для Break ───────────────── */
    public static void addBreakRule(Predicate<BlockEvent.BreakEvent> cond,
                                    Consumer<BlockEvent.BreakEvent> act) {
        BREAK_TRIGGERS.add(new SkillTriggerEntry<>(cond, act));
    }
    public static void addBreakRule(Block block, PlayerSkills.SkillType skill, float xp) {
        addBreakRule(evt -> evt.getState().getBlock() == block,
                evt -> add(p(evt), skill, xp));
    }
    public static void addBreakRuleTag(TagKey<Block> tag, PlayerSkills.SkillType skill, float xp) {
        addBreakRule(evt -> evt.getState().is(tag),
                evt -> add(p(evt), skill, xp));
    }

    /* ── помощники для Craft ───────────────── */
    public static void addCraftRule(Predicate<PlayerEvent.ItemCraftedEvent> cond,
                                    PlayerSkills.SkillType skill, float xp) {
        CRAFT_TRIGGERS.add(new SkillTriggerEntry<>(cond, evt -> add(p(evt), skill, xp)));
    }

    /* ── утилити ───────────────────────────── */
    private static ServerPlayer p(BlockEvent.BreakEvent e)           { return (ServerPlayer) e.getPlayer(); }
    private static ServerPlayer p(PlayerEvent.ItemCraftedEvent e)    { return (ServerPlayer) e.getEntity(); }

    private static void add(ServerPlayer p, PlayerSkills.SkillType t, float xp) {
        PlayerProfileManager.get(p).skills.addXp(t, xp);
    }
}
