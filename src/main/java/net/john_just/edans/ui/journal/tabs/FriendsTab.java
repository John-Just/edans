package net.john_just.edans.ui.journal.tabs;

import net.john_just.edans.ClientCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Вкладка «Игроки» – выводит список знакомых */
public class FriendsTab implements TabContent {
    private List<UUID> friends = List.of();     // пустой до первой загрузки

    /** вызывайте после прихода ProfileSyncPacket */
    public void reload() {
        var pkt = ClientCache.profileLocal;
        if (pkt == null) {                      // данных ещё нет
            friends = List.of();
            return;
        }
        // здесь пока берём список из какого-то своего хранилища;
        // пример: knownPlayers внутри PlayerProfileManager
        friends = new ArrayList<>(/* ваш источник uuid */);
    }

    @Override
    public void render(GuiGraphics g, int ox, int oy, float d) {
        int off = 0;
        for (UUID id : friends) {
            Component name = Component.literal(id.toString()); // замените на отображаемое имя
            g.drawString(Minecraft.getInstance().font,
                    name, ox + 10, oy + 10 + off, 0xFFD0A0);
            off += 12;
        }
    }
}
