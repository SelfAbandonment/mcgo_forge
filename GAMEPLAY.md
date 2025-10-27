# MCGO Forge - CS:GO Bomb Defusal Mode

A Minecraft Forge mod that recreates the classic Counter-Strike: Global Offensive bomb defusal gameplay experience in Minecraft.

## Features

### Implemented Features

#### Team System
- **Three teams**: Terrorist (T), Counter-Terrorist (CT), and Spectator
- Team-based spawn points
- Color-coded team identification
- Persistent team assignment across rounds

#### Economy System
- Starting money: $800
- Win bonus: $3,250
- Loss bonus: $1,400
- Money cap: $16,000
- Money persists across rounds

#### Round Management
- **Round States**:
  - Waiting: Pre-game setup
  - Freeze Time: 15-second buy phase
  - Active: 1:55 round timer
  - Post Round: 5-second results display
- Automatic round progression
- Score tracking (T vs CT)
- Round number display

#### Bomb Mechanics
- C4 Explosive item (bomb)
- Bomb can only be used by Terrorists
- Must be planted in designated bomb sites
- 45-second bomb timer after planting
- Bomb explosion ends round

#### Win Conditions
- Team Elimination: All members of one team killed
- Time Expiration: CT wins if time runs out before bomb is planted
- Bomb Explosion: T wins if bomb explodes
- Bomb Defusal: CT wins if bomb is defused (future feature)

#### Death & Spectator System
- No respawn during active round
- Automatic spectator mode on death
- Respawn at round start
- Team-based spawn points

## Commands

All commands require operator permissions (level 2).

### Setup Commands

```
/mcgo setspawn t
```
Sets the Terrorist spawn point at your current location.

```
/mcgo setspawn ct
```
Sets the Counter-Terrorist spawn point at your current location.

```
/mcgo addsite <name> <radius>
```
Adds a bomb site at your current location with the specified name and radius.
Example: `/mcgo addsite A 10`

```
/mcgo clearsite
```
Removes all bomb sites from the map.

### Game Management Commands

```
/mcgo start
```
Starts the game. Initializes the first round.

```
/mcgo team <players> t|ct|spec
```
Assigns players to a team.
Examples:
- `/mcgo team @a t` - Assigns all players to Terrorists
- `/mcgo team @p ct` - Assigns nearest player to Counter-Terrorists
- `/mcgo team PlayerName spec` - Assigns PlayerName to Spectator

```
/mcgo money <players> <amount>
```
Sets the money for specified players.
Example: `/mcgo money @a 16000`

## Setup Guide

1. **Prepare your map**:
   - Build a CS:GO-style map with two spawn areas and bomb sites

2. **Set spawn points**:
   ```
   /mcgo setspawn t
   /mcgo setspawn ct
   ```

3. **Create bomb sites**:
   ```
   /mcgo addsite A 10
   /mcgo addsite B 10
   ```

4. **Assign teams**:
   ```
   /mcgo team @a[name=Player1,name=Player2] t
   /mcgo team @a[name=Player3,name=Player4] ct
   ```

5. **Start the game**:
   ```
   /mcgo start
   ```

## Gameplay Flow

1. **Freeze Time (15 seconds)**:
   - Players spawn at team spawn points
   - Can move and prepare but round hasn't started
   - Time to buy equipment (future feature)

2. **Active Round (1:55)**:
   - Terrorists try to plant the bomb at a bomb site
   - Counter-Terrorists try to prevent the plant or eliminate Terrorists
   - If bomb is planted, timer resets to 45 seconds
   - Round ends when:
     - One team is eliminated
     - Time expires (CT wins)
     - Bomb explodes (T wins)
     - Bomb is defused (CT wins - future feature)

3. **Post Round (5 seconds)**:
   - Displays round results and score
   - Awards money to both teams
   - Prepares for next round

4. **Next Round**:
   - All dead players respawn
   - Money is added based on win/loss
   - Round counter increments
   - Returns to Freeze Time

## Economy Rewards

- **Round Win**: $3,250
- **Round Loss**: $1,400
- **Kill Reward**: Not yet implemented
- **Starting Money**: $800

## Technical Details

### Player Capability
Each player has a `PlayerGameData` capability attached that stores:
- Team assignment (T/CT/Spectator)
- Money (0-16,000)
- Alive status

### World Saved Data
Map configuration is stored in `GameMapData`:
- Terrorist spawn point
- Counter-Terrorist spawn point
- List of bomb sites (name, center position, radius)

### Round Manager
Singleton `RoundManager` handles:
- Round state transitions
- Timer management
- Win condition checking
- Money distribution
- Player respawning

## Future Enhancements

- [ ] Bomb defuse kit
- [ ] Defusing mechanics with progress bar
- [ ] Buy menu for weapons and equipment
- [ ] Kill rewards
- [ ] Loss bonus streak system
- [ ] Halftime team swap
- [ ] Overtime system
- [ ] More detailed statistics
- [ ] Weapon system integration
- [ ] Armor system
- [ ] Grenade utilities

## Development Notes

### Backwards Compatibility
The implementation is designed to be backwards compatible:
- Existing Minecraft items and blocks are not modified
- All features are additive and can be disabled
- Configuration is stored separately in world save data
- Player capabilities use NBT serialization for compatibility

### Code Structure
```
org.mcgo_forge.mcgo_forge/
├── gameplay/           # Team and player data
│   ├── Team.java
│   ├── PlayerGameData.java
│   └── PlayerCapabilityProvider.java
├── round/              # Round state management
│   ├── RoundState.java
│   └── RoundManager.java
├── world/              # World save data
│   └── GameMapData.java
├── items/              # Custom items
│   └── BombItem.java
├── commands/           # Admin commands
│   └── GameCommands.java
└── events/             # Event handlers
    └── GameEventHandler.java
```

## License

MIT License - See LICENSE file for details

## Credits

Developed for the mcgo_forge project by SelfAbandonment
Based on Counter-Strike: Global Offensive gameplay mechanics
