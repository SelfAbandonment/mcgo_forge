package org.mcgo_forge.mcgo_forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.mcgo_forge.mcgo_forge.gameplay.PlayerCapabilityProvider;
import org.mcgo_forge.mcgo_forge.gameplay.PlayerGameData;
import org.mcgo_forge.mcgo_forge.gameplay.Team;
import org.mcgo_forge.mcgo_forge.round.RoundManager;
import org.mcgo_forge.mcgo_forge.world.GameMapData;

import java.util.Collection;

public class GameCommands {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("mcgo")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("setspawn")
                        .then(Commands.literal("t")
                                .executes(ctx -> setTSpawn(ctx)))
                        .then(Commands.literal("ct")
                                .executes(ctx -> setCTSpawn(ctx))))
                .then(Commands.literal("addsite")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("radius", IntegerArgumentType.integer(1, 50))
                                        .executes(ctx -> addBombSite(ctx)))))
                .then(Commands.literal("clearsite")
                        .executes(ctx -> clearBombSites(ctx)))
                .then(Commands.literal("start")
                        .executes(ctx -> startGame(ctx)))
                .then(Commands.literal("team")
                        .then(Commands.argument("players", EntityArgument.players())
                                .then(Commands.literal("t")
                                        .executes(ctx -> setTeam(ctx, Team.TERRORIST)))
                                .then(Commands.literal("ct")
                                        .executes(ctx -> setTeam(ctx, Team.COUNTER_TERRORIST)))
                                .then(Commands.literal("spec")
                                        .executes(ctx -> setTeam(ctx, Team.SPECTATOR)))))
                .then(Commands.literal("money")
                        .then(Commands.argument("players", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, 16000))
                                        .executes(ctx -> setMoney(ctx)))))
        );
    }
    
    private static int setTSpawn(CommandContext<CommandSourceStack> context) {
        try {
            ServerLevel level = context.getSource().getLevel();
            BlockPos pos = BlockPos.containing(context.getSource().getPosition());
            
            GameMapData data = GameMapData.get(level);
            data.setTSpawn(pos);
            
            context.getSource().sendSuccess(() -> 
                    Component.literal("Terrorist spawn set at " + pos.toShortString())
                            .withStyle(ChatFormatting.GREEN), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Failed to set spawn: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int setCTSpawn(CommandContext<CommandSourceStack> context) {
        try {
            ServerLevel level = context.getSource().getLevel();
            BlockPos pos = BlockPos.containing(context.getSource().getPosition());
            
            GameMapData data = GameMapData.get(level);
            data.setCTSpawn(pos);
            
            context.getSource().sendSuccess(() -> 
                    Component.literal("Counter-Terrorist spawn set at " + pos.toShortString())
                            .withStyle(ChatFormatting.GREEN), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Failed to set spawn: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int addBombSite(CommandContext<CommandSourceStack> context) {
        try {
            ServerLevel level = context.getSource().getLevel();
            BlockPos pos = BlockPos.containing(context.getSource().getPosition());
            String name = StringArgumentType.getString(context, "name");
            int radius = IntegerArgumentType.getInteger(context, "radius");
            
            GameMapData data = GameMapData.get(level);
            data.addBombSite(name, pos, radius);
            
            context.getSource().sendSuccess(() -> 
                    Component.literal("Bomb site '" + name + "' added at " + pos.toShortString() + 
                            " with radius " + radius)
                            .withStyle(ChatFormatting.GREEN), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Failed to add bomb site: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int clearBombSites(CommandContext<CommandSourceStack> context) {
        try {
            ServerLevel level = context.getSource().getLevel();
            GameMapData data = GameMapData.get(level);
            data.clearBombSites();
            
            context.getSource().sendSuccess(() -> 
                    Component.literal("All bomb sites cleared")
                            .withStyle(ChatFormatting.GREEN), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Failed to clear bomb sites: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int startGame(CommandContext<CommandSourceStack> context) {
        try {
            ServerLevel level = context.getSource().getLevel();
            RoundManager manager = RoundManager.getInstance();
            manager.setLevel(level);
            manager.startGame();
            
            context.getSource().sendSuccess(() -> 
                    Component.literal("Game started!")
                            .withStyle(ChatFormatting.GREEN), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Failed to start game: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int setTeam(CommandContext<CommandSourceStack> context, Team team) {
        try {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "players");
            
            for (ServerPlayer player : players) {
                PlayerGameData data = PlayerCapabilityProvider.getPlayerData(player);
                data.setTeam(team);
                
                player.sendSystemMessage(Component.literal("You have been assigned to " + team.getDisplayName())
                        .withStyle(team.getColor()));
            }
            
            context.getSource().sendSuccess(() -> 
                    Component.literal(players.size() + " player(s) assigned to " + team.getDisplayName())
                            .withStyle(ChatFormatting.GREEN), true);
            return players.size();
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Failed to set team: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int setMoney(CommandContext<CommandSourceStack> context) {
        try {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "players");
            int amount = IntegerArgumentType.getInteger(context, "amount");
            
            for (ServerPlayer player : players) {
                PlayerGameData data = PlayerCapabilityProvider.getPlayerData(player);
                data.setMoney(amount);
                
                player.sendSystemMessage(Component.literal("Your money has been set to $" + amount)
                        .withStyle(ChatFormatting.GREEN));
            }
            
            context.getSource().sendSuccess(() -> 
                    Component.literal("Set money to $" + amount + " for " + players.size() + " player(s)")
                            .withStyle(ChatFormatting.GREEN), true);
            return players.size();
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Failed to set money: " + e.getMessage()));
            return 0;
        }
    }
}
