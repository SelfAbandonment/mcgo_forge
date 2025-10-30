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
 * 恐怖分子在爆破点安放的 C4 炸弹物品。
 * 继承自 AbstractGameItem，以复用通用功能并提升可维护性。
 */
public class BombItem extends AbstractGameItem {
    
    public BombItem(Properties properties) {
        super(properties);
    }
    
    @Override
    protected InteractionResultHolder<ItemStack> useOnServer(
            ServerLevel level, ServerPlayer player, InteractionHand hand) {
        
        // 校验玩家是否为恐怖分子
        if (!validateTeam(player, Team.TERRORIST, "Only Terrorists can use the bomb!")) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // 校验当前是否为进行中的回合
        if (!validateRoundState(player, RoundState.ACTIVE, "Cannot plant bomb right now!")) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // 检查是否已经安放过炸弹
        if (getRoundManager().isBombPlanted()) {
            sendErrorMessage(player, "Bomb is already planted!");
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // 校验玩家是否处于爆破点内
        GameMapData mapData = GameMapData.get(level);
        BlockPos playerPos = player.blockPosition();
        GameMapData.BombSite site = mapData.getBombSiteAt(playerPos);
        
        if (site == null) {
            sendErrorMessage(player, "You must be in a bomb site to plant!");
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        // 安放炸弹
        plantBomb(level, player, site);
        
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }
    
    /**
     * 在指定爆破点安放炸弹。
     *
     * @param level 服务器维度
     * @param player 安放炸弹的玩家
     * @param site 将要安放的爆破点
     */
    private void plantBomb(ServerLevel level, ServerPlayer player, GameMapData.BombSite site) {
        BlockPos playerPos = player.blockPosition();
        
        // 更新回合管理器状态
        getRoundManager().setBombPlanted(true);
        getRoundManager().setBombPosition(playerPos);
        
        // 从玩家背包移除炸弹
        player.getInventory().clearOrCountMatchingItems(
                stack -> stack.getItem() instanceof BombItem,
                1,
                player.inventoryMenu.getCraftSlots()
        );
        
        // 发送成功消息
        sendSuccessMessage(player, "Bomb planted at site " + site.name() + "!");
    }
}
