package net.john_just.edans.chat;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ChatCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("chatradius")
                        .executes(ctx -> {
                            int radius = ChatHandler.INSTANCE.getChatRadius();
                            ctx.getSource().sendSystemMessage(
                                    Component.literal("Текущий радиус чата: " + radius + " блоков.")
                            );
                            return 1;
                        })
                        .then(Commands.literal("set")
                                .requires(src -> src.hasPermission(2)) // Только OP
                                .then(Commands.argument("radius", IntegerArgumentType.integer(5, 500))
                                        .executes(ctx -> {
                                            int radius = IntegerArgumentType.getInteger(ctx, "radius");
                                            ChatHandler.INSTANCE.setChatRadius(radius);
                                            ctx.getSource().sendSystemMessage(
                                                    Component.literal("Радиус чата установлен на: " + radius + " блоков.")
                                            );
                                            return 1;
                                        })
                                )
                        )
        );
    }
}
