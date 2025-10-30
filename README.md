# MCGO Forge - Counter-Strike: Global Offensive in Minecraft

A Minecraft Forge mod that recreates the classic Counter-Strike: Global Offensive bomb defusal gameplay experience in Minecraft 1.20.1.

## 🎮 特性

### 核心 CS:GO 炸弹拆除游戏玩法
- **团队系统**：恐怖分子 (T) 对抗 反恐精英 (CT)，并提供观战模式
- **回合管理**：完整的回合周期，包括购买时间、主动阶段和回合结束
- **炸弹机制**：种植、拆除和爆炸，具有正确的胜利条件
- **经济系统**：起始资金、胜利/失败奖金和资金上限
- **死亡系统**：回合中无重生，观战模式，回合结束复活

### 已实现的系统

#### 🎯 回合流程
- **冻结时间**：回合开始时的 15 秒购买阶段
- **主动回合**：1:55 计时器（或炸弹种植后 45 秒）
- **回合结束**：5 秒结果显示
- **自动推进**：无缝的回合转换

#### 💰 经济
- 起始资金：$800
- 胜利奖金：$3,250
- 失败奖金：$1,400
- 资金上限：$16,000

#### 🏆 胜利条件
1. 消灭敌队（击杀所有敌人）
2. 时间到期（CT 胜利）
3. 炸弹爆炸（T 胜利）
4. 炸弹拆除（CT 胜利）

#### 📦 物品
- **C4 爆炸物**：恐怖分子用于在炸弹点种植的炸弹
- **拆弹工具包**：反恐精英用于拆除已种植炸弹的工具

#### ⚙️ 指令
所有指令都需要操作员权限（等级 2）：

```
/mcgo setspawn t|ct          - 设置团队重生点
/mcgo addsite <name> <radius> - 在当前位置添加炸弹点
/mcgo clearsite               - 移除所有炸弹点
/mcgo start                   - 开始游戏
/mcgo team <players> t|ct|spec - 分配玩家到团队
/mcgo money <players> <amount> - 设置玩家资金
```

## 📖 文档

- **[GAMEPLAY.md](GAMEPLAY.md)** - 完整的用户指南和设置说明
- **[TESTING.md](TESTING.md)** - 综合测试指南
- **[IMPLEMENTATION.md](IMPLEMENTATION.md)** - 技术文档和架构

## 🚀 快速开始

### 安装

1. 安装 Minecraft 1.20.1
2. 安装 Forge 47.4.10
3. 将 mod JAR 放入 `mods/` 文件夹
4. 启动游戏

### 设置

1. **配置重生点**：
   ```
   /mcgo setspawn t
   /mcgo setspawn ct
   ```

2. **添加炸弹点**：
   ```
   /mcgo addsite A 10
   /mcgo addsite B 10
   ```

3. **分配团队**：
   ```
   /mcgo team @p t
   /mcgo team Player2 ct
   ```

4. **开始游戏**：
   ```
   /mcgo start
   ```

有关详细的设置指南，请参见 [GAMEPLAY.md](GAMEPLAY.md)。

## 🏗️ 构建

```bash
./gradlew build
```

构建的 mod 将位于 `build/libs/` 中。

## 🎯 游戏玩法示例

1. 回合开始，进入 15 秒的冻结时间
2. 恐怖分子拥有 $800，需要种植炸弹
3. 反恐精英拥有 $800，需要阻止种植或拆除炸弹
4. 回合计时器：1:55
5. 如果炸弹已种植：45 秒内拆除，否则爆炸！
6. 回合结束，胜利者获得 $3,250，失败者获得 $1,400
7. 下一回合自动开始

## 📊 统计

- **12 个 Java 类**，跨越 6 个包
- **~1,200 行代码**
- **2 个自定义物品**（炸弹，拆弹工具包）
- **6 个指令**，具有完整的管理员控制
- **3 个综合文档文件**
- **100% 向后兼容** - 未修改现有代码

## 🔄 向后兼容性

该 mod 旨在考虑向后兼容性：
- ✅ 未修改现有 Minecraft 物品/方块
- ✅ 所有特性都是附加的
- ✅ 现有世界保持不变
- ✅ 安全地添加/移除现有服务器
- ✅ 示例 mod 内容得以保留

有关完整兼容性细节，请参见 [IMPLEMENTATION.md](IMPLEMENTATION.md)。

## 🛣️ 路线图

### 第二阶段：增强机制
- 种植/拆除进度条，移动时取消
- 声音效果和视觉反馈
- 世界中的物理炸弹实体

### 第三阶段：购买系统
- 购买菜单 GUI
- 武器和装备购买
- 购买区域限制

### 第四阶段：武器和装备
- CS:GO 武器系统（AK-47，M4A4，AWP 等）
- 护甲系统（防弹衣，头盔）
- 手雷（高爆，闪光，烟雾，莫洛托夫）
- 击杀奖励

### 第五阶段：高级特性
- 失败奖金连胜系统
- 半场团队交换
- 加时赛（MR3）
- MVP 系统
- 统计追踪

## 🧪 测试

有关全面的测试场景，涵盖以下内容，请参见 [TESTING.md](TESTING.md)：
- 设置和配置
- 团队分配
- 回合流程
- 炸弹机制（种植/拆除）
- 胜利条件
- 经济系统
- 死亡和重生
- 边缘情况

## 🤝 贡献

欢迎贡献！请：
1. 遵循现有代码风格
2. 保持向后兼容性
3. 在 TESTING.md 中添加测试
4. 更新文档

## 📜 许可证

MIT 许可证 - 请参见 [LICENSE](LICENSE) 文件。

该实现可以自由使用、修改和分发。

## 🙏 致谢

- **原始 CS:GO 游戏玩法**：Valve 公司
- **实现**：SelfAbandonment
- **Minecraft Forge**：Forge 开发团队
- **Minecraft**：Mojang Studios

## 📞 支持

- **问题**： https://github.com/SelfAbandonment/mcgo_forge/issues
- **文档**：请参见 GAMEPLAY.md，TESTING.md，IMPLEMENTATION.md

## 📝 版本

当前版本：**0.1.0** - 核心炸弹拆除游戏玩法

### 包含的内容
- 完整的回合管理系统
- 团队分配和重生点
- 炸弹种植/拆除机制
- 带有奖励的经济系统
- 死亡和观战系统
- 完整的管理员命令套件
- 综合文档

### 接下来要推出的内容
- 种植/拆除动作的进度条
- 购买系统和菜单
- 武器系统
- 更多 CS:GO 特性！

---

**注意**：该 mod 需要操作员权限（等级 2）来设置和管理游戏。配置完成后，游戏玩法将自动进行。

**兼容性**：Minecraft 1.20.1 和 Forge 47.4.10

**为 CS:GO 和 Minecraft 社区而精心制作**
