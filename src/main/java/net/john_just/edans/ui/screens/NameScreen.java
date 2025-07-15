package net.john_just.edans.ui.screens;

import net.john_just.edans.network.SetNamePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class NameScreen extends Screen {
    private EditBox field;
    private Button ok;

    public NameScreen() { super(Component.literal("Введите имя")); }

    @Override
    protected void init() {
        field = new EditBox(font, width/2-100, height/2-10, 200, 20, Component.empty());
        addRenderableWidget(field);

        ok = Button.builder(Component.literal("OK"), b -> sendAndClose())
                .bounds(width/2-40, height/2+15, 80, 20)
                .build();
        addRenderableWidget(ok);
    }

    private void sendAndClose() {
        String name = field.getValue().trim();
        if (!name.isEmpty()) {
            PacketDistributor.sendToServer(new SetNamePacket(name));
            onClose();
        }
    }

    @Override public void render(GuiGraphics gui, int x, int y, float d) {
        gui.fill(0, 0, width, height, 0xAA000000);
        super.render(gui, x, y, d);
        gui.drawCenteredString(font, "Введите игровое имя", width/2, height/2-30, 0xFFFFFF);
    }
    @Override public boolean isPauseScreen() { return true; }
}
