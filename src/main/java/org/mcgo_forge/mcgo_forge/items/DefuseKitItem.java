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

public class DefuseKitItem extends Item {
    public DefuseKitItem(Properties properties) {
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
        
        // Check if player is on Counter-Terrorist team
        if (data.getTeam() != Team.COUNTER_TERRORIST) {
            player.sendSystemMessage(Component.literal("Only Counter-Terrorists can use the defuse kit!")
                    .withStyle(ChatFormatting.RED));
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // Check if round is active
        RoundManager roundManager = RoundManager.getInstance();
        if (roundManager.getCurrentState() != RoundState.ACTIVE) {
            player.sendSystemMessage(Component.literal("No bomb to defuse right now!")
                    .withStyle(ChatFormatting.RED));
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // Check if bomb is planted
        if (!roundManager.isBombPlanted()) {
            player.sendSystemMessage(Component.literal("No bomb has been planted!")
                    .withStyle(ChatFormatting.RED));
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // Check if near bomb (simplified - can add actual bomb entity later)
        BlockPos bombPos = roundManager.getBombPosition();
        if (bombPos != null && player.blockPosition().distSqr(bombPos) > 25) { // 5 block radius
            player.sendSystemMessage(Component.literal("You are too far from the bomb!")
                    .withStyle(ChatFormatting.RED));
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // Start defusing (simplified - instant defuse for now, can add progress later)
        defuseBomb(serverLevel, serverPlayer);
        
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }
    
    private void defuseBomb(ServerLevel level, ServerPlayer player) {
        RoundManager roundManager = RoundManager.getInstance();
        roundManager.defuseBomb();
        
        player.sendSystemMessage(Component.literal("Bomb defused!")
                .withStyle(ChatFormatting.GREEN));
    }
}
