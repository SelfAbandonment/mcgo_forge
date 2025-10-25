package org.mcgo_forge.mcgo_forge.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.mcgo_forge.mcgo_forge.gameplay.PlayerCapabilityProvider;
import org.mcgo_forge.mcgo_forge.gameplay.PlayerGameData;
import org.mcgo_forge.mcgo_forge.gameplay.Team;
import org.mcgo_forge.mcgo_forge.round.RoundManager;
import org.mcgo_forge.mcgo_forge.round.RoundState;
import org.mcgo_forge.mcgo_forge.world.GameMapData;

public class BombItem extends Item {
    public BombItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }
        
        ServerLevel serverLevel = (ServerLevel) level;
        ServerPlayer serverPlayer = (ServerPlayer) player;
        PlayerGameData data = PlayerCapabilityProvider.getPlayerData(player);
        
        // Check if player is on Terrorist team
        if (data.getTeam() != Team.TERRORIST) {
            player.sendSystemMessage(Component.literal("Only Terrorists can use the bomb!")
                    .withStyle(ChatFormatting.RED));
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // Check if round is active
        RoundManager roundManager = RoundManager.getInstance();
        if (roundManager.getCurrentState() != RoundState.ACTIVE) {
            player.sendSystemMessage(Component.literal("Cannot plant bomb right now!")
                    .withStyle(ChatFormatting.RED));
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // Check if already planted
        if (roundManager.isBombPlanted()) {
            player.sendSystemMessage(Component.literal("Bomb is already planted!")
                    .withStyle(ChatFormatting.RED));
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // Check if in bomb site
        GameMapData mapData = GameMapData.get(serverLevel);
        BlockPos playerPos = player.blockPosition();
        GameMapData.BombSite site = mapData.getBombSiteAt(playerPos);
        
        if (site == null) {
            player.sendSystemMessage(Component.literal("You must be in a bomb site to plant!")
                    .withStyle(ChatFormatting.RED));
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // Start planting (simplified - instant plant for now, can add progress later)
        plantBomb(serverLevel, serverPlayer, site);
        
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }
    
    private void plantBomb(ServerLevel level, ServerPlayer player, GameMapData.BombSite site) {
        RoundManager roundManager = RoundManager.getInstance();
        roundManager.setBombPlanted(true);
        
        // Remove bomb from inventory
        player.getInventory().clearOrCountMatchingItems(
                stack -> stack.getItem() instanceof BombItem,
                1,
                player.inventoryMenu.getCraftSlots()
        );
        
        player.sendSystemMessage(Component.literal("Bomb planted at site " + site.name() + "!")
                .withStyle(ChatFormatting.GREEN));
    }
}
