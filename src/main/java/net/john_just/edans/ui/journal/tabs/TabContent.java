/*
 * Package: net.john_just.edans.ui.journal.tabs
 * Общий интерфейс вкладки
 */
package net.john_just.edans.ui.journal.tabs;

import net.minecraft.client.gui.GuiGraphics;

public interface TabContent {
    void render(GuiGraphics g, int originX, int originY, float partial);
}