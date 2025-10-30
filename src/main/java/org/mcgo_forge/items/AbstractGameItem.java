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
 * 所有 CS:GO 游戏物品的抽象基类。
 * 提供队伍验证、回合状态检查以及消息发送等通用功能，
 * 以提升代码的复用性和可维护性。
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
     * 当物品在服务端被使用时调用。
     * 重写该方法以实现服务端的物品行为。
     */
    protected abstract InteractionResultHolder<ItemStack> useOnServer(
            ServerLevel level, ServerPlayer player, InteractionHand hand);
    
    /**
     * 检查玩家是否属于指定队伍。
     *
     * @param player 要检查的玩家
     * @param requiredTeam 玩家必须所在的队伍
     * @param errorMessage 若校验失败要发送的错误信息
     * @return 若校验通过返回 true，否则返回 false
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
     * 检查当前回合状态是否为指定状态。
     *
     * @param player 用于接收错误信息的玩家
     * @param requiredState 需要的回合状态
     * @param errorMessage 若校验失败要发送的错误信息
     * @return 若校验通过返回 true，否则返回 false
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
     * 向玩家发送错误信息。
     *
     * @param player 接收消息的玩家
     * @param message 错误消息文本
     */
    protected void sendErrorMessage(ServerPlayer player, String message) {
        player.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.RED));
    }
    
    /**
     * 向玩家发送成功信息。
     *
     * @param player 接收消息的玩家
     * @param message 成功消息文本
     */
    protected void sendSuccessMessage(ServerPlayer player, String message) {
        player.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.GREEN));
    }
    
    /**
     * 获取玩家的游戏数据。
     *
     * @param player 玩家
     * @return 玩家游戏数据
     */
    protected PlayerGameData getPlayerData(Player player) {
        return PlayerCapabilityProvider.getPlayerData(player);
    }
    
    /**
     * 获取回合管理器实例。
     *
     * @return 回合管理器
     */
    protected RoundManager getRoundManager() {
        return RoundManager.getInstance();
    }
}
