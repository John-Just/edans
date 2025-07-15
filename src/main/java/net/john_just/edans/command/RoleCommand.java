package net.john_just.edans.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.john_just.edans.chat.role.RoleManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class RoleCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("setrole")
                        .requires(src -> src.hasPermission(3)) // Только OP
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("role", StringArgumentType.word())
                                        .executes(ctx -> {
                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                            String role = StringArgumentType.getString(ctx, "role");

                                            RoleManager.setRole(target.getUUID(), role);
                                            ctx.getSource().sendSystemMessage(Component.literal("Роль игроку выдана: " + role));
                                            return 1;
                                        })
                                )
                        )
        );
    }
}
