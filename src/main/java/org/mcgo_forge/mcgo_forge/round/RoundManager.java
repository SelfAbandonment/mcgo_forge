package org.mcgo_forge.mcgo_forge.round;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.mcgo_forge.mcgo_forge.gameplay.PlayerCapabilityProvider;
import org.mcgo_forge.mcgo_forge.gameplay.PlayerGameData;
import org.mcgo_forge.mcgo_forge.gameplay.Team;
import org.mcgo_forge.mcgo_forge.world.GameMapData;

import java.util.List;

public class RoundManager {
    private static RoundManager instance;
    
    private RoundState currentState = RoundState.WAITING;
    private int roundNumber = 0;
    private int tScore = 0;
    private int ctScore = 0;
    private int stateTimer = 0;
    private boolean bombPlanted = false;
    private ServerLevel level;
    
    // Configuration
    private static final int FREEZETIME_DURATION = 15 * 20; // 15 seconds in ticks
    private static final int ROUND_DURATION = 115 * 20; // 1:55 in ticks
    private static final int POST_ROUND_DURATION = 5 * 20; // 5 seconds in ticks
    private static final int BOMB_TIMER = 45 * 20; // 45 seconds in ticks
    
    private RoundManager() {}
    
    public static RoundManager getInstance() {
        if (instance == null) {
            instance = new RoundManager();
        }
        return instance;
    }
    
    public void setLevel(ServerLevel level) {
        this.level = level;
    }
    
    public RoundState getCurrentState() {
        return currentState;
    }
    
    public int getRoundNumber() {
        return roundNumber;
    }
    
    public int getTScore() {
        return tScore;
    }
    
    public int getCtScore() {
        return ctScore;
    }
    
    public void tick() {
        if (level == null) return;
        
        stateTimer++;
        
        switch (currentState) {
            case WAITING:
                // Wait for command to start
                break;
                
            case FREEZETIME:
                if (stateTimer >= FREEZETIME_DURATION) {
                    startRound();
                }
                break;
                
            case ACTIVE:
                int remainingTime = bombPlanted ? BOMB_TIMER : ROUND_DURATION;
                if (stateTimer >= remainingTime) {
                    endRound(bombPlanted ? Team.TERRORIST : Team.COUNTER_TERRORIST, 
                            bombPlanted ? "Bomb Exploded!" : "Time Expired!");
                } else {
                    checkWinConditions();
                }
                break;
                
            case POST_ROUND:
                if (stateTimer >= POST_ROUND_DURATION) {
                    startNewRound();
                }
                break;
        }
    }
    
    public void startGame() {
        roundNumber = 0;
        tScore = 0;
        ctScore = 0;
        startNewRound();
        broadcastMessage(Component.literal("Game Started!").withStyle(ChatFormatting.GREEN));
    }
    
    private void startNewRound() {
        roundNumber++;
        bombPlanted = false;
        currentState = RoundState.FREEZETIME;
        stateTimer = 0;
        
        // Reset all players
        List<ServerPlayer> players = level.getPlayers(p -> true);
        for (ServerPlayer player : players) {
            PlayerGameData data = PlayerCapabilityProvider.getPlayerData(player);
            
            if (data.getTeam() != Team.SPECTATOR) {
                data.setAlive(true);
                
                // Give starting money for first round, otherwise add round money
                if (roundNumber == 1) {
                    data.setMoney(800);
                } else {
                    data.addMoney(1400); // Loss bonus
                }
                
                // Respawn player at team spawn
                GameMapData mapData = GameMapData.get(level);
                if (data.getTeam() == Team.TERRORIST && mapData.getTSpawn() != null) {
                    player.teleportTo(mapData.getTSpawn().getX(), mapData.getTSpawn().getY(), mapData.getTSpawn().getZ());
                } else if (data.getTeam() == Team.COUNTER_TERRORIST && mapData.getCTSpawn() != null) {
                    player.teleportTo(mapData.getCTSpawn().getX(), mapData.getCTSpawn().getY(), mapData.getCTSpawn().getZ());
                }
                
                if (player.gameMode.getGameModeForPlayer() != GameType.SURVIVAL) {
                    player.setGameMode(GameType.SURVIVAL);
                }
            }
        }
        
        broadcastMessage(Component.literal("Round " + roundNumber + " - Buy Time")
                .withStyle(ChatFormatting.YELLOW));
    }
    
    private void startRound() {
        currentState = RoundState.ACTIVE;
        stateTimer = 0;
        broadcastMessage(Component.literal("Round Started!").withStyle(ChatFormatting.GREEN));
    }
    
    private void checkWinConditions() {
        if (level == null) return;
        
        List<ServerPlayer> players = level.getPlayers(p -> true);
        boolean tAlive = false;
        boolean ctAlive = false;
        
        for (ServerPlayer player : players) {
            PlayerGameData data = PlayerCapabilityProvider.getPlayerData(player);
            if (data.isAlive()) {
                if (data.getTeam() == Team.TERRORIST) tAlive = true;
                if (data.getTeam() == Team.COUNTER_TERRORIST) ctAlive = true;
            }
        }
        
        if (!tAlive && !ctAlive) {
            // Draw - shouldn't happen but handle it
            endRound(null, "Draw!");
        } else if (!tAlive) {
            endRound(Team.COUNTER_TERRORIST, "Counter-Terrorists Eliminated Terrorists!");
        } else if (!ctAlive) {
            endRound(Team.TERRORIST, "Terrorists Eliminated Counter-Terrorists!");
        }
    }
    
    public void endRound(Team winner, String reason) {
        currentState = RoundState.POST_ROUND;
        stateTimer = 0;
        
        if (winner == Team.TERRORIST) {
            tScore++;
            awardMoney(Team.TERRORIST, 3250);
            awardMoney(Team.COUNTER_TERRORIST, 1400);
        } else if (winner == Team.COUNTER_TERRORIST) {
            ctScore++;
            awardMoney(Team.COUNTER_TERRORIST, 3250);
            awardMoney(Team.TERRORIST, 1400);
        }
        
        String scoreText = String.format("Score - T: %d | CT: %d", tScore, ctScore);
        broadcastMessage(Component.literal(reason).withStyle(ChatFormatting.GOLD));
        broadcastMessage(Component.literal(scoreText).withStyle(ChatFormatting.AQUA));
    }
    
    private void awardMoney(Team team, int amount) {
        if (level == null) return;
        
        List<ServerPlayer> players = level.getPlayers(p -> true);
        for (ServerPlayer player : players) {
            PlayerGameData data = PlayerCapabilityProvider.getPlayerData(player);
            if (data.getTeam() == team) {
                data.addMoney(amount);
            }
        }
    }
    
    public void setBombPlanted(boolean planted) {
        this.bombPlanted = planted;
        if (planted) {
            stateTimer = 0; // Reset timer for bomb countdown
            broadcastMessage(Component.literal("Bomb has been planted!").withStyle(ChatFormatting.RED));
        }
    }
    
    public boolean isBombPlanted() {
        return bombPlanted;
    }
    
    private void broadcastMessage(Component message) {
        if (level == null) return;
        
        List<ServerPlayer> players = level.getPlayers(p -> true);
        for (ServerPlayer player : players) {
            player.sendSystemMessage(message);
        }
    }
    
    public void handlePlayerDeath(Player player) {
        PlayerGameData data = PlayerCapabilityProvider.getPlayerData(player);
        data.setAlive(false);
        
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.setGameMode(GameType.SPECTATOR);
        }
        
        // Check if round should end
        if (currentState == RoundState.ACTIVE) {
            checkWinConditions();
        }
    }
}
