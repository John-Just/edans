// ────────────────────────────────────────────────────────────────
//  UI «Дневник» – минимальный рабочий скелет для 1.21 + NeoForge 21
//  Пакет: net.john_just.edans.ui.journal
// ────────────────────────────────────────────────────────────────

/*
 * JournalScreen.java – корневой экран с вкладками
 */
package net.john_just.edans.ui.journal;

import net.john_just.edans.ui.journal.tabs.ProfileTab;
import net.john_just.edans.ui.journal.tabs.FriendsTab;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class JournalScreen extends Screen {

    // внутри JournalScreen
    private final ProfileTab profTab = new ProfileTab();
    private final FriendsTab frTab  = new FriendsTab();
    private boolean showProfile = true;  // true=profile, false=friends
    private Button btnProfile, btnFriends;

    public JournalScreen() {
        super(Component.literal("Дневник"));   // ← заголовок экрана
    }


    @Override protected void init() {
        int cx = width / 2;
        btnProfile = Button.builder(Component.literal("Профиль"),
                b -> showProfile = true).bounds(cx-60, 10, 60, 20).build();
        btnFriends = Button.builder(Component.literal("Игроки"),
                b -> showProfile = false).bounds(cx, 10, 60, 20).build();
        addRenderableWidget(btnProfile);
        addRenderableWidget(btnFriends);
    }

    public void refreshProfileTab() {
        profTab.reload();
    }

    @Override public void render(GuiGraphics g,int x,int y,float d) {
        g.fill(0,0,width,height,0xAA000000);
        super.render(g,x,y,d);
        if (showProfile) profTab.render(g, 20, 40, d);
        else             frTab.render(g, 20, 40, d);
    }

}








