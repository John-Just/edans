package net.john_just.edans.skill; // или event, если у тебя слушатели в пакете event

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.slf4j.Logger;

public class SkillEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    // ─── ломаем блок ─────────────────────────────────
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        LOGGER.info("[XP] Сломали блок: " + event.getState().getBlock());
        for (SkillTriggerEntry<BlockEvent.BreakEvent> entry : SkillTriggerRegistry.BREAK_TRIGGERS) {
            if (entry.condition.test(event)) {
                entry.action.accept(event);
            }
        }
    }
}
