package net.john_just.edans.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.john_just.edans.data.PlayerProfile;
import net.john_just.edans.data.PlayerProfileManager;
import net.john_just.edans.event.PlayerConnectionHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ProfileCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("profile")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    PlayerProfile profile = PlayerProfile.load(player);
                    ctx.getSource().sendSuccess(() -> Component.literal("Твоё имя: " + profile.nickname
                            + ", Репутация: " + profile.reputation
                            + ", Деньги: " + profile.money), false);
                    return 1;
                }));

        dispatcher.register(Commands.literal("setname")
                .then(Commands.argument("имя", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            String name = StringArgumentType.getString(ctx, "имя");

                            PlayerProfile profile = PlayerProfileManager.get(player);
                            if (profile == null) {
                                ctx.getSource().sendFailure(Component.literal("Профиль не найден."));
                                return 0;
                            }

                            profile.save(player.getServer());
                            profile.characterName = name;
                            ctx.getSource().sendSuccess(() -> Component.literal("Игровое имя установлено: " + name), false);
                            return 1;
                        }))
        );
    }
}
