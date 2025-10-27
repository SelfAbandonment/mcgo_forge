package org.mcgo_forge.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import org.mcgo_forge.gameplay.Team;
import org.mcgo_forge.round.RoundState;

/**
 * Defuse Kit item for Counter-Terrorists to defuse planted bombs.
 * Extends AbstractGameItem for common functionality and better maintainability.
 */
public class DefuseKitItem extends AbstractGameItem {
    
    private static final int DEFUSE_RANGE_SQUARED = 25; // 5 blocks squared
    
    public DefuseKitItem(Properties properties) {
        super(properties);
    }
    
    @Override
    protected InteractionResultHolder<ItemStack> useOnServer(
            ServerLevel level, ServerPlayer player, InteractionHand hand) {
        
        // Validate player is on Counter-Terrorist team
        if (!validateTeam(player, Team.COUNTER_TERRORIST, 
                "Only Counter-Terrorists can use the defuse kit!")) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // Validate round is active
        if (!validateRoundState(player, RoundState.ACTIVE, "No bomb to defuse right now!")) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // Check if bomb is planted
        if (!getRoundManager().isBombPlanted()) {
            sendErrorMessage(player, "No bomb has been planted!");
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // Validate player is near the bomb
        if (!isPlayerNearBomb(player)) {
            sendErrorMessage(player, "You are too far from the bomb!");
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // Defuse the bomb
        defuseBomb(level, player);
        
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }
    
    /**
     * Checks if the player is within defuse range of the planted bomb.
     * 
     * @param player The player to check
     * @return true if player is near the bomb, false otherwise
     */
    private boolean isPlayerNearBomb(ServerPlayer player) {
        BlockPos bombPos = getRoundManager().getBombPosition();
        if (bombPos == null) {
            return false;
        }
        return player.blockPosition().distSqr(bombPos) <= DEFUSE_RANGE_SQUARED;
    }
    
    /**
     * Defuses the planted bomb.
     * 
     * @param level The server level
     * @param player The player defusing the bomb
     */
    private void defuseBomb(ServerLevel level, ServerPlayer player) {
        getRoundManager().defuseBomb();
        sendSuccessMessage(player, "Bomb defused!");
    }
}
