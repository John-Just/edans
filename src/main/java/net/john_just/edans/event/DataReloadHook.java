package net.john_just.edans.event;

import net.john_just.edans.EdansMod;
import net.john_just.edans.data.XpTableLoader;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(modid = EdansMod.MOD_ID)
public final class DataReloadHook {

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent e) {
        e.addListener(XpTableLoader.INSTANCE);
    }
}
