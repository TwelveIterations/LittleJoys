package net.blay09.mods.littlejoys.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.blay09.mods.balm.commands.BalmCommands;
import net.blay09.mods.littlejoys.LittleJoys;
import net.blay09.mods.littlejoys.blessing.BlessingManager;
import net.blay09.mods.littlejoys.blessing.Blessings;
import net.blay09.mods.littlejoys.handler.*;
import net.blay09.mods.littlejoys.recipe.DigSpotRecipe;
import net.blay09.mods.littlejoys.recipe.DropRushRecipe;
import net.blay09.mods.littlejoys.recipe.FishingSpotRecipe;
import net.blay09.mods.littlejoys.recipe.GoldRushRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.item.crafting.RecipeHolder;

public class LittleJoysCommand {

    private static final Identifier PERMISSION_LITTLEJOYS_DIGSPOT = LittleJoys.id("command.littlejoys.digspot");
    private static final Identifier PERMISSION_LITTLEJOYS_FISHINGSPOT = LittleJoys.id("command.littlejoys.fishingspot");
    private static final Identifier PERMISSION_LITTLEJOYS_GOLDRUSH = LittleJoys.id("command.littlejoys.goldrush");
    private static final Identifier PERMISSION_LITTLEJOYS_DROPRUSH = LittleJoys.id("command.littlejoys.droprush");
    private static final Identifier PERMISSION_LITTLEJOYS_FALLENSTAR = LittleJoys.id("command.littlejoys.fallenstar");
    private static final Identifier PERMISSION_LITTLEJOYS_BLESSING = LittleJoys.id("command.littlejoys.blessing");

    private static final DynamicCommandExceptionType ERROR_UNKNOWN_RECIPE = new DynamicCommandExceptionType((arg) -> Component.translatable("recipe.notFound", arg));
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_BLESSING = new DynamicCommandExceptionType((arg) -> Component.translatable("argument.resource.not_found", arg, "blessing"));

    @SuppressWarnings("unchecked")
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        BalmCommands.registerPermission(PERMISSION_LITTLEJOYS_DIGSPOT, Permissions.COMMANDS_GAMEMASTER);
        BalmCommands.registerPermission(PERMISSION_LITTLEJOYS_FISHINGSPOT, Permissions.COMMANDS_GAMEMASTER);
        BalmCommands.registerPermission(PERMISSION_LITTLEJOYS_GOLDRUSH, Permissions.COMMANDS_GAMEMASTER);
        BalmCommands.registerPermission(PERMISSION_LITTLEJOYS_DROPRUSH, Permissions.COMMANDS_GAMEMASTER);
        BalmCommands.registerPermission(PERMISSION_LITTLEJOYS_FALLENSTAR, Permissions.COMMANDS_GAMEMASTER);
        BalmCommands.registerPermission(PERMISSION_LITTLEJOYS_BLESSING, Permissions.COMMANDS_GAMEMASTER);

        dispatcher.register(Commands.literal("littlejoys")
                .then(Commands.literal("blessing")
                        .requires(BalmCommands.requirePermission(PERMISSION_LITTLEJOYS_BLESSING))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("blessing", ResourceKeyArgument.key(Blessings.REGISTRY_KEY))
                                        .executes(context -> {
                                            final var player = EntityArgument.getPlayer(context, "player");
                                            final var blessingKey = context.getArgument("blessing", ResourceKey.class);
                                            final var blessing = Blessings.byId(blessingKey.identifier())
                                                    .orElseThrow(() -> ERROR_UNKNOWN_BLESSING.create(blessingKey.identifier()));
                                            BlessingManager.applyBlessing(player, blessing);
                                            context.getSource().sendSuccess(() -> Component.translatable("commands.littlejoys.blessing.success",
                                                    Component.literal("[")
                                                            .append(Component.translatable(blessing.effect().unwrapKey().orElseThrow().identifier().toLanguageKey("effect")))
                                                            .append("]")
                                                            .withStyle(ChatFormatting.BOLD),
                                                    player.getDisplayName()), true);
                                            return 1;
                                        }))))
                .then(Commands.literal("fallenstar")
                        .requires(BalmCommands.requirePermission(PERMISSION_LITTLEJOYS_FALLENSTAR))
                        .executes(context -> {
                            final var level = context.getSource().getLevel();
                            final var player = context.getSource().getPlayerOrException();
                            return FallenStarHandler.startFallingStar(level, player) ? 1 : 0;
                        })
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> {
                                    final var player = EntityArgument.getPlayer(context, "player");
                                    return FallenStarHandler.startFallingStar(player.level(), player) ? 1 : 0;
                                }))
                        .then(Commands.argument("position", BlockPosArgument.blockPos())
                                .executes(context -> {
                                    final var level = context.getSource().getLevel();
                                    final var pos = BlockPosArgument.getBlockPos(context, "position");
                                    FallenStarHandler.startFallingStar(level, pos);
                                    return 1;
                                })))
                .then(Commands.literal("digspot")
                        .requires(BalmCommands.requirePermission(PERMISSION_LITTLEJOYS_DIGSPOT))
                        .then(Commands.argument("position", BlockPosArgument.blockPos())
                                .executes(context -> {
                                    final var level = context.getSource().getLevel();
                                    final var player = context.getSource().getPlayerOrException();
                                    final var pos = BlockPosArgument.getBlockPos(context, "position");
                                    return DigSpotHandler.createDigSpot(level, pos, player) ? 1 : 0;
                                })
                                .then(Commands.argument("recipe", ResourceKeyArgument.key(Registries.RECIPE))
                                        .executes(context -> {
                                            final var level = context.getSource().getLevel();
                                            final var pos = BlockPosArgument.getBlockPos(context, "position");
                                            final var recipeHolder = ResourceKeyArgument.getRecipe(context, "recipe");
                                            if (recipeHolder.value() instanceof DigSpotRecipe) {
                                                DigSpotHandler.createDigSpot(level, pos, (RecipeHolder<DigSpotRecipe>) recipeHolder);
                                                return 1;
                                            } else {
                                                throw ERROR_UNKNOWN_RECIPE.create(context.getArgument("recipe", ResourceKey.class));
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
                                .then(Commands.argument("recipe", ResourceKeyArgument.key(Registries.RECIPE))
                                        .executes(context -> {
                                            final var level = context.getSource().getLevel();
                                            final var pos = BlockPosArgument.getBlockPos(context, "position");
                                            final var recipeHolder = ResourceKeyArgument.getRecipe(context, "recipe");
                                            if (recipeHolder.value() instanceof FishingSpotRecipe) {
                                                FishingSpotHandler.createFishingSpot(level, pos, (RecipeHolder<FishingSpotRecipe>) recipeHolder);
                                                return 1;
                                            } else {
                                                throw ERROR_UNKNOWN_RECIPE.create(context.getArgument("recipe", ResourceKey.class));
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
                                .then(Commands.argument("recipe", ResourceKeyArgument.key(Registries.RECIPE))
                                        .executes(context -> {
                                            final var level = context.getSource().getLevel();
                                            final var player = context.getSource().getPlayerOrException();
                                            final var pos = BlockPosArgument.getBlockPos(context, "position");
                                            final var recipeHolder = ResourceKeyArgument.getRecipe(context, "recipe");
                                            if (recipeHolder.value() instanceof GoldRushRecipe) {
                                                GoldRushHandler.startGoldRush(level, pos, level.getBlockState(pos), player, (RecipeHolder<GoldRushRecipe>) recipeHolder);
                                                return 1;
                                            } else {
                                                throw ERROR_UNKNOWN_RECIPE.create(context.getArgument("recipe", ResourceKey.class));
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
                                }).then(Commands.argument("recipe", ResourceKeyArgument.key(Registries.RECIPE))
                                        .executes(context -> {
                                            final var level = context.getSource().getLevel();
                                            final var player = context.getSource().getPlayerOrException();
                                            final var pos = BlockPosArgument.getBlockPos(context, "position");
                                            final var recipeHolder = ResourceKeyArgument.getRecipe(context, "recipe");
                                            if (recipeHolder.value() instanceof DropRushRecipe) {
                                                DropRushHandler.startDropRush(level, pos, player, (RecipeHolder<DropRushRecipe>) recipeHolder);
                                                return 1;
                                            } else {
                                                throw ERROR_UNKNOWN_RECIPE.create(context.getArgument("recipe", ResourceKey.class));
                                            }
                                        }))))
        );
    }
}
