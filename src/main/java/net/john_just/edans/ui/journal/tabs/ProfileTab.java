/*
 * ProfileTab.java – «Мой профиль»
 */
package net.john_just.edans.ui.journal.tabs;

import net.john_just.edans.ClientCache;
import net.john_just.edans.data.PlayerProfileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.Map;

public class ProfileTab implements TabContent {
    private Component cachedLines = Component.literal("загрузка…");

    public void reload() {
        var pkt = ClientCache.profileLocal;
        if (pkt == null) return;
        var prof = PlayerProfileManager.getClient();
        StringBuilder sb = new StringBuilder();
        sb.append("Имя: ").append(prof.characterName).append("\n");
        sb.append("Репутация: ").append(pkt.reputation()).append("\n");
        sb.append("Деньги: ").append(pkt.money()).append("\n");
        sb.append("Навыки:\n");
        for (Map.Entry<String,Float> e : pkt.skills().entrySet())
            sb.append("  ").append(e.getKey()).append(": ")
                    .append(String.format("%.1f", e.getValue())).append("\n");
        cachedLines = Component.literal(sb.toString());
    }

    @Override
    public void render(GuiGraphics g, int x, int y, float partial) {
        g.drawString(Minecraft.getInstance().font, cachedLines, x+10, y+10, 0xFFFFFF);
    }
}