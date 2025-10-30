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
 * 反恐精英用于拆除已安放炸弹的拆弹器物品。
 * 继承自 AbstractGameItem，以复用通用功能并提升可维护性。
 */
public class DefuseKitItem extends AbstractGameItem {
    
    private static final int DEFUSE_RANGE_SQUARED = 25; // 5 格的平方距离

    public DefuseKitItem(Properties properties) {
        super(properties);
    }
    
    @Override
    protected InteractionResultHolder<ItemStack> useOnServer(
            ServerLevel level, ServerPlayer player, InteractionHand hand) {
        
        // 校验玩家是否为反恐精英
        if (!validateTeam(player, Team.COUNTER_TERRORIST,
                "Only Counter-Terrorists can use the defuse kit!")) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // 校验当前是否为进行中的回合
        if (!validateRoundState(player, RoundState.ACTIVE, "No bomb to defuse right now!")) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // 检查是否存在已安放的炸弹
        if (!getRoundManager().isBombPlanted()) {
            sendErrorMessage(player, "No bomb has been planted!");
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // 校验玩家是否靠近炸弹
        if (!isPlayerNearBomb(player)) {
            sendErrorMessage(player, "You are too far from the bomb!");
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // 拆除炸弹
        defuseBomb(level, player);
        
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }
    
    /**
     * 检查玩家是否在可拆除范围内。
     *
     * @param player 要检查的玩家
     * @return 若玩家靠近炸弹返回 true，否则返回 false
     */
    private boolean isPlayerNearBomb(ServerPlayer player) {
        BlockPos bombPos = getRoundManager().getBombPosition();
        if (bombPos == null) {
            return false;
        }
        return player.blockPosition().distSqr(bombPos) <= DEFUSE_RANGE_SQUARED;
    }
    
    /**
     * 拆除已安放的炸弹。
     *
     * @param level 服务器维度
     * @param player 执行拆除的玩家
     */
    private void defuseBomb(ServerLevel level, ServerPlayer player) {
        getRoundManager().defuseBomb();
        sendSuccessMessage(player, "Bomb defused!");
    }
}
