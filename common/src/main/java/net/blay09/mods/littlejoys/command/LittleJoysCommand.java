package net.blay09.mods.littlejoys.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.blay09.mods.balm.api.command.BalmCommands;
import net.blay09.mods.littlejoys.handler.DigSpotHandler;
import net.blay09.mods.littlejoys.handler.DropRushHandler;
import net.blay09.mods.littlejoys.handler.FishingSpotHandler;
import net.blay09.mods.littlejoys.handler.GoldRushHandler;
import net.blay09.mods.littlejoys.recipe.DigSpotRecipe;
import net.blay09.mods.littlejoys.recipe.DropRushRecipe;
import net.blay09.mods.littlejoys.recipe.FishingSpotRecipe;
import net.blay09.mods.littlejoys.recipe.GoldRushRecipe;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class LittleJoysCommand {

    private static final ResourceLocation PERMISSION_LITTLEJOYS_DIGSPOT = new ResourceLocation("littlejoys", "command.littlejoys.digspot");
    private static final ResourceLocation PERMISSION_LITTLEJOYS_FISHINGSPOT = new ResourceLocation("littlejoys", "command.littlejoys.fishingspot");
    private static final ResourceLocation PERMISSION_LITTLEJOYS_GOLDRUSH = new ResourceLocation("littlejoys", "command.littlejoys.goldrush");
    private static final ResourceLocation PERMISSION_LITTLEJOYS_DROPRUSH = new ResourceLocation("littlejoys", "command.littlejoys.droprush");

    private static final DynamicCommandExceptionType ERROR_UNKNOWN_RECIPE = new DynamicCommandExceptionType((arg) -> Component.translatable("recipe.notFound", arg));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        BalmCommands.registerPermission(PERMISSION_LITTLEJOYS_DIGSPOT, 2);
        BalmCommands.registerPermission(PERMISSION_LITTLEJOYS_FISHINGSPOT, 2);
        BalmCommands.registerPermission(PERMISSION_LITTLEJOYS_GOLDRUSH, 2);
        BalmCommands.registerPermission(PERMISSION_LITTLEJOYS_DROPRUSH, 2);

        dispatcher.register(Commands.literal("littlejoys")
                .then(Commands.literal("digspot")
                        .requires(BalmCommands.requirePermission(PERMISSION_LITTLEJOYS_DIGSPOT))
                        .then(Commands.argument("position", BlockPosArgument.blockPos())
                                .executes(context -> {
                                    final var level = context.getSource().getLevel();
                                    final var player = context.getSource().getPlayerOrException();
                                    final var pos = BlockPosArgument.getBlockPos(context, "position");
                                    return DigSpotHandler.createDigSpot(level, pos, player) ? 1 : 0;
                                })
                                .then(Commands.argument("recipe", ResourceLocationArgument.id())
                                        .executes(context -> {
                                            final var level = context.getSource().getLevel();
                                            final var pos = BlockPosArgument.getBlockPos(context, "position");
                                            final var recipe = ResourceLocationArgument.getRecipe(context, "recipe");
                                            if (recipe instanceof DigSpotRecipe digSpotRecipe) {
                                                DigSpotHandler.createDigSpot(level, pos, digSpotRecipe);
                                                return 1;
                                            } else {
                                                throw ERROR_UNKNOWN_RECIPE.create(ResourceLocationArgument.getId(context, "recipe"));
                                            }
                                        }))))
                .then(Commands.literal("fishingspot")
                        .requires(BalmCommands.requirePermission(PERMISSION_LITTLEJOYS_FISHINGSPOT))
                        .then(Commands.argument("position", BlockPosArgument.blockPos())
                                .executes(context -> {
                                    final var level = context.getSource().getLevel();
                                    final var player = context.getSource().getPlayerOrException();
                                    final var pos = BlockPosArgument.getBlockPos(context, "position");
                                    return FishingSpotHandler.createFishingSpot(level, pos, player) ? 1 : 0;
                                })
                                .then(Commands.argument("recipe", ResourceLocationArgument.id())
                                        .executes(context -> {
                                            final var level = context.getSource().getLevel();
                                            final var pos = BlockPosArgument.getBlockPos(context, "position");
                                            final var recipe = ResourceLocationArgument.getRecipe(context, "recipe");
                                            if (recipe instanceof FishingSpotRecipe fishingSpotRecipe) {
                                                FishingSpotHandler.createFishingSpot(level, pos, fishingSpotRecipe);
                                                return 1;
                                            } else {
                                                throw ERROR_UNKNOWN_RECIPE.create(ResourceLocationArgument.getId(context, "recipe"));
                                            }
                                        }))))
                .then(Commands.literal("goldrush")
                        .requires(BalmCommands.requirePermission(PERMISSION_LITTLEJOYS_GOLDRUSH))
                        .then(Commands.argument("position", BlockPosArgument.blockPos())
                                .executes(context -> {
                                    final var level = context.getSource().getLevel();
                                    final var player = context.getSource().getPlayerOrException();
                                    final var pos = BlockPosArgument.getBlockPos(context, "position");
                                    GoldRushHandler.startGoldRush(level, pos, level.getBlockState(pos), player);
                                    return 1;
                                })
                                .then(Commands.argument("recipe", ResourceLocationArgument.id())
                                        .executes(context -> {
                                            final var level = context.getSource().getLevel();
                                            final var player = context.getSource().getPlayerOrException();
                                            final var pos = BlockPosArgument.getBlockPos(context, "position");
                                            final var recipe = ResourceLocationArgument.getRecipe(context, "recipe");
                                            if (recipe instanceof GoldRushRecipe goldRushRecipe) {
                                                GoldRushHandler.startGoldRush(level, pos, level.getBlockState(pos), player, goldRushRecipe);
                                                return 1;
                                            } else {
                                                throw ERROR_UNKNOWN_RECIPE.create(ResourceLocationArgument.getId(context, "recipe"));
                                            }
                                        }))))
                .then(Commands.literal("droprush")
                        .requires(BalmCommands.requirePermission(PERMISSION_LITTLEJOYS_DROPRUSH))
                        .then(Commands.argument("position", BlockPosArgument.blockPos())
                                .executes(context -> {
                                    final var level = context.getSource().getLevel();
                                    final var player = context.getSource().getPlayerOrException();
                                    final var pos = BlockPosArgument.getBlockPos(context, "position");
                                    DropRushHandler.startDropRush(level, pos, level.getBlockState(pos), player);
                                    return 0;
                                }).then(Commands.argument("recipe", ResourceLocationArgument.id())
                                        .executes(context -> {
                                            final var level = context.getSource().getLevel();
                                            final var player = context.getSource().getPlayerOrException();
                                            final var pos = BlockPosArgument.getBlockPos(context, "position");
                                            final var recipe = ResourceLocationArgument.getRecipe(context, "recipe");
                                            if (recipe instanceof DropRushRecipe dropRushRecipe) {
                                                DropRushHandler.startDropRush(level, pos, player, dropRushRecipe);
                                                return 1;
                                            } else {
                                                throw ERROR_UNKNOWN_RECIPE.create(ResourceLocationArgument.getId(context, "recipe"));
                                            }
                                        }))))
        );
    }
}
