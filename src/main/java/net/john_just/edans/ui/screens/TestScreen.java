package net.john_just.edans.ui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TestScreen extends Screen {

    public TestScreen() {
        super(Component.literal("Тест-экран"));
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        super.renderBackground(gui, mouseX, mouseY, delta);           // затемнить
        super.render(gui, mouseX, mouseY, delta);
        gui.drawCenteredString(font,
                "Привет! Это TestScreen",
                width / 2, height / 2 - 4,
                0xFFFFFF);

    }

    @Override public boolean isPauseScreen() { return false; }
}
