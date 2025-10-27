package org.mcgo_forge.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import org.mcgo_forge.gameplay.Team;
import org.mcgo_forge.round.RoundState;
import org.mcgo_forge.world.GameMapData;

/**
 * C4 Explosive item for Terrorists to plant at bomb sites.
 * Extends AbstractGameItem for common functionality and better maintainability.
 */
public class BombItem extends AbstractGameItem {
    
    public BombItem(Properties properties) {
        super(properties);
    }
    
    @Override
    protected InteractionResultHolder<ItemStack> useOnServer(
            ServerLevel level, ServerPlayer player, InteractionHand hand) {
        
        // Validate player is on Terrorist team
        if (!validateTeam(player, Team.TERRORIST, "Only Terrorists can use the bomb!")) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // Validate round is active
        if (!validateRoundState(player, RoundState.ACTIVE, "Cannot plant bomb right now!")) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // Check if bomb is already planted
        if (getRoundManager().isBombPlanted()) {
            sendErrorMessage(player, "Bomb is already planted!");
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // Validate player is in a bomb site
        GameMapData mapData = GameMapData.get(level);
        BlockPos playerPos = player.blockPosition();
        GameMapData.BombSite site = mapData.getBombSiteAt(playerPos);
        
        if (site == null) {
            sendErrorMessage(player, "You must be in a bomb site to plant!");
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // Plant the bomb
        plantBomb(level, player, site);
        
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }
    
    /**
     * Plants the bomb at the specified site.
     * 
     * @param level The server level
     * @param player The player planting the bomb
     * @param site The bomb site where the bomb is being planted
     */
    private void plantBomb(ServerLevel level, ServerPlayer player, GameMapData.BombSite site) {
        BlockPos playerPos = player.blockPosition();
        
        // Update round manager state
        getRoundManager().setBombPlanted(true);
        getRoundManager().setBombPosition(playerPos);
        
        // Remove bomb from inventory
        player.getInventory().clearOrCountMatchingItems(
                stack -> stack.getItem() instanceof BombItem,
                1,
                player.inventoryMenu.getCraftSlots()
        );
        
        // Send success message
        sendSuccessMessage(player, "Bomb planted at site " + site.name() + "!");
    }
}
