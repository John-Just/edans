package net.john_just.edans;

import net.john_just.edans.ui.journal.JournalScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;      // <-- верная аннотация
import org.lwjgl.glfw.GLFW;

/** Клиент: открывает TestScreen нажатием K */
@EventBusSubscriber(
        modid = EdansMod.MOD_ID,
        value = Dist.CLIENT,
        bus   = EventBusSubscriber.Bus.GAME   // ← корректная шина
)
public final class ClientUI {

    @SubscribeEvent
    public static void onKey(net.neoforged.neoforge.client.event.InputEvent.Key evt) {
        if (evt.getKey() == GLFW.GLFW_KEY_K &&
                evt.getAction() == GLFW.GLFW_PRESS &&
                Minecraft.getInstance().screen == null) {

            Minecraft.getInstance().setScreen(new JournalScreen());
        }
    }
}