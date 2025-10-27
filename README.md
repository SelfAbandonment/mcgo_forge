# MCGO Forge - Counter-Strike: Global Offensive in Minecraft

A Minecraft Forge mod that recreates the classic Counter-Strike: Global Offensive bomb defusal gameplay experience in Minecraft 1.20.1.

## 🎮 Features

### Core CS:GO Bomb Defusal Gameplay
- **Team System**: Terrorist (T) vs Counter-Terrorist (CT) with spectator mode
- **Round Management**: Complete round cycle with buy time, active phase, and post-round
- **Bomb Mechanics**: Plant, defuse, and explosion with proper win conditions
- **Economy System**: Starting money, win/loss bonuses, and money cap
- **Death System**: No respawn during rounds, spectator mode, round-end revival

### Implemented Systems

#### 🎯 Round Flow
- **Freeze Time**: 15-second buy phase at round start
- **Active Round**: 1:55 timer (or 45s after bomb plant)
- **Post Round**: 5-second result display
- **Automatic Progression**: Seamless round transitions

#### 💰 Economy
- Starting Money: $800
- Win Bonus: $3,250
- Loss Bonus: $1,400
- Money Cap: $16,000

#### 🏆 Win Conditions
1. Team Elimination (all enemies killed)
2. Time Expiration (CT wins)
3. Bomb Explosion (T wins)
4. Bomb Defusal (CT wins)

#### 📦 Items
- **C4 Explosive**: Terrorist bomb for planting at bomb sites
- **Defuse Kit**: Counter-Terrorist tool for defusing planted bombs

#### ⚙️ Commands
All commands require operator permission (level 2):

```
/mcgo setspawn t|ct          - Set team spawn points
/mcgo addsite <name> <radius> - Add bomb site at current location
/mcgo clearsite               - Remove all bomb sites
/mcgo start                   - Start the game
/mcgo team <players> t|ct|spec - Assign players to teams
/mcgo money <players> <amount> - Set player money
```

## 📖 Documentation

- **[GAMEPLAY.md](GAMEPLAY.md)** - Complete user guide with setup instructions
- **[TESTING.md](TESTING.md)** - Comprehensive testing guide
- **[IMPLEMENTATION.md](IMPLEMENTATION.md)** - Technical documentation and architecture

## 🚀 Quick Start

### Installation

1. Install Minecraft 1.20.1
2. Install Forge 47.4.10
3. Place mod JAR in `mods/` folder
4. Launch game

### Setup

1. **Configure spawn points**:
   ```
   /mcgo setspawn t
   /mcgo setspawn ct
   ```

2. **Add bomb sites**:
   ```
   /mcgo addsite A 10
   /mcgo addsite B 10
   ```

3. **Assign teams**:
   ```
   /mcgo team @p t
   /mcgo team Player2 ct
   ```

4. **Start game**:
   ```
   /mcgo start
   ```

See [GAMEPLAY.md](GAMEPLAY.md) for detailed setup guide.

## 🏗️ Building

```bash
./gradlew build
```

The built mod will be in `build/libs/`.

## 🎯 Gameplay Example

1. Round starts with 15-second freeze time
2. Terrorists spawn with $800, need to plant bomb
3. Counter-Terrorists spawn with $800, need to prevent plant or defuse
4. Round timer: 1:55
5. If bomb planted: 45 seconds to defuse or boom!
6. Round ends, winners get $3,250, losers get $1,400
7. Next round starts automatically

## 📊 Statistics

- **12 Java classes** across 6 packages
- **~1,200 lines of code**
- **2 custom items** (bomb, defuse kit)
- **6 commands** with full admin control
- **3 comprehensive documentation files**
- **100% backwards compatible** - no existing code modified

## 🔄 Backwards Compatibility

This mod is designed with backwards compatibility in mind:
- ✅ No modifications to existing Minecraft items/blocks
- ✅ All features are additive
- ✅ Existing worlds work unchanged
- ✅ Safe to add/remove from existing servers
- ✅ Example mod content preserved

See [IMPLEMENTATION.md](IMPLEMENTATION.md) for full compatibility details.

## 🛣️ Roadmap

### Phase 2: Enhanced Mechanics
- Plant/defuse progress bars with movement cancellation
- Sound effects and visual feedback
- Physical bomb entity in world

### Phase 3: Buy System
- Buy menu GUI
- Weapon and equipment purchases
- Buy zone restrictions

### Phase 4: Weapons & Equipment
- CS:GO weapon system (AK-47, M4A4, AWP, etc.)
- Armor system (kevlar, helmet)
- Grenades (HE, flash, smoke, molotov)
- Kill rewards

### Phase 5: Advanced Features
- Loss bonus streak system
- Halftime team swap
- Overtime (MR3)
- MVP system
- Statistics tracking

## 🧪 Testing

See [TESTING.md](TESTING.md) for comprehensive testing scenarios covering:
- Setup and configuration
- Team assignment
- Round flow
- Bomb mechanics (plant/defuse)
- Win conditions
- Economy system
- Death and respawn
- Edge cases

## 🤝 Contributing

Contributions are welcome! Please:
1. Follow existing code style
2. Maintain backwards compatibility
3. Add tests to TESTING.md
4. Update documentation

## 📜 License

MIT License - See [LICENSE](LICENSE) file.

This mod is free to use, modify, and distribute.

## 🙏 Credits

- **Original CS:GO gameplay**: Valve Corporation
- **Implementation**: SelfAbandonment
- **Minecraft Forge**: Forge Development Team
- **Minecraft**: Mojang Studios

## 📞 Support

- **Issues**: https://github.com/SelfAbandonment/mcgo_forge/issues
- **Documentation**: See GAMEPLAY.md, TESTING.md, IMPLEMENTATION.md

## 📝 Version

Current Version: **0.1.0** - Core Bomb Defusal Gameplay

### What's Included
- Complete round management system
- Team assignment and spawn points
- Bomb plant/defuse mechanics
- Economy system with rewards
- Death and spectator system
- Full admin command suite
- Comprehensive documentation

### What's Coming Next
- Progress bars for plant/defuse actions
- Buy system and menu
- Weapon system
- More CS:GO features!

---

**Note**: This mod requires operator permissions (level 2) to set up and manage games. Once configured, gameplay is automatic.

**Compatibility**: Minecraft 1.20.1 with Forge 47.4.10

**Made with ❤️ for the CS:GO and Minecraft communities**
