package org.mcgo_forge.round;

public enum RoundState {
    WAITING,     // 等待玩家/地图设置
    FREEZETIME,  // 购买/冻结时间
    ACTIVE,      // 回合进行中
    POST_ROUND   // 回合结束，展示结果
}
