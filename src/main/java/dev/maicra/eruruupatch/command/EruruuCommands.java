package dev.maicra.eruruupatch.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.maicra.eruruupatch.event.ReinforcedPickaxeEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class EruruuCommands {
    private EruruuCommands() {
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        var buildContext = event.getBuildContext();

        event.getDispatcher().register(
                Commands.literal("eruruu")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("givepick")
                                .then(Commands.argument("pickaxe", ItemArgument.item(buildContext))
                                        .then(Commands.argument(
                                                        "level",
                                                        IntegerArgumentType.integer(
                                                                1,
                                                                ReinforcedPickaxeEvents.MAX_LEVEL)
                                                )
                                                .executes(context -> giveToSelf(context))
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(context -> giveToPlayer(
                                                                context,
                                                                EntityArgument.getPlayer(context, "player")
                                                        )))
                                        )
                                )
                        )
        );
    }

    private static int giveToSelf(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return giveToPlayer(context, context.getSource().getPlayerOrException());
    }

    private static int giveToPlayer(CommandContext<CommandSourceStack> context, ServerPlayer target) {
        CommandSourceStack source = context.getSource();
        Item item = ItemArgument.getItem(context, "pickaxe").getItem();
        int level = IntegerArgumentType.getInteger(context, "level");
        ItemStack base = item.getDefaultInstance();

        if (!(item instanceof PickaxeItem)) {
            source.sendFailure(Component.translatable(
                    "commands.eruruu_patch.givepick.not_pickaxe",
                    base.getHoverName()
            ));
            return 0;
        }

        if (ReinforcedPickaxeEvents.isBlacklisted(base)) {
            source.sendFailure(Component.translatable(
                    "commands.eruruu_patch.givepick.blacklisted",
                    base.getHoverName()
            ));
            return 0;
        }

        if (!ReinforcedPickaxeEvents.isEligibleBasePickaxe(base)) {
            source.sendFailure(Component.translatable(
                    "commands.eruruu_patch.givepick.invalid_pickaxe",
                    base.getHoverName()
            ));
            return 0;
        }

        ItemStack generated = ReinforcedPickaxeEvents.createPickaxeAtLevel(base, level);
        if (generated.isEmpty()) {
            source.sendFailure(Component.translatable(
                    "commands.eruruu_patch.givepick.failed",
                    base.getHoverName(),
                    level
            ));
            return 0;
        }

        ItemStack displayCopy = generated.copy();
        ItemStack remaining = generated.copy();
        boolean inserted = target.getInventory().add(remaining);
        if (!inserted || !remaining.isEmpty()) {
            target.drop(remaining, false);
        }
        target.containerMenu.broadcastChanges();

        source.sendSuccess(
                () -> Component.translatable(
                        "commands.eruruu_patch.givepick.success",
                        displayCopy.getHoverName(),
                        level,
                        target.getDisplayName()
                ),
                true
        );
        return 1;
    }
}
