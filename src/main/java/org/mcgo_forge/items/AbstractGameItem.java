package org.mcgo_forge.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.mcgo_forge.gameplay.PlayerCapabilityProvider;
import org.mcgo_forge.gameplay.PlayerGameData;
import org.mcgo_forge.gameplay.Team;
import org.mcgo_forge.round.RoundManager;
import org.mcgo_forge.round.RoundState;

/**
 * Abstract base class for all CS:GO game items.
 * Provides common functionality for team validation, round state checking,
 * and message sending to improve code reusability and maintainability.
 */
public abstract class AbstractGameItem extends Item {
    
    protected AbstractGameItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public final InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }
        
        ServerLevel serverLevel = (ServerLevel) level;
        ServerPlayer serverPlayer = (ServerPlayer) player;
        
        return useOnServer(serverLevel, serverPlayer, hand);
    }
    
    /**
     * Called when the item is used on the server side.
     * Override this method to implement server-side item behavior.
     */
    protected abstract InteractionResultHolder<ItemStack> useOnServer(
            ServerLevel level, ServerPlayer player, InteractionHand hand);
    
    /**
     * Checks if the player is on the required team.
     * 
     * @param player The player to check
     * @param requiredTeam The team the player must be on
     * @param errorMessage The error message to send if validation fails
     * @return true if validation passes, false otherwise
     */
    protected boolean validateTeam(ServerPlayer player, Team requiredTeam, String errorMessage) {
        PlayerGameData data = PlayerCapabilityProvider.getPlayerData(player);
        if (data.getTeam() != requiredTeam) {
            sendErrorMessage(player, errorMessage);
            return false;
        }
        return true;
    }
    
    /**
     * Checks if the current round state matches the required state.
     * 
     * @param player The player to send error messages to
     * @param requiredState The required round state
     * @param errorMessage The error message to send if validation fails
     * @return true if validation passes, false otherwise
     */
    protected boolean validateRoundState(ServerPlayer player, RoundState requiredState, String errorMessage) {
        RoundManager roundManager = RoundManager.getInstance();
        if (roundManager.getCurrentState() != requiredState) {
            sendErrorMessage(player, errorMessage);
            return false;
        }
        return true;
    }
    
    /**
     * Sends an error message to the player.
     * 
     * @param player The player to send the message to
     * @param message The error message text
     */
    protected void sendErrorMessage(ServerPlayer player, String message) {
        player.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.RED));
    }
    
    /**
     * Sends a success message to the player.
     * 
     * @param player The player to send the message to
     * @param message The success message text
     */
    protected void sendSuccessMessage(ServerPlayer player, String message) {
        player.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.GREEN));
    }
    
    /**
     * Gets the player's game data.
     * 
     * @param player The player
     * @return The player's game data
     */
    protected PlayerGameData getPlayerData(Player player) {
        return PlayerCapabilityProvider.getPlayerData(player);
    }
    
    /**
     * Gets the round manager instance.
     * 
     * @return The round manager
     */
    protected RoundManager getRoundManager() {
        return RoundManager.getInstance();
    }
}
