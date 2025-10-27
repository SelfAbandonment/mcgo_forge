# Testing Guide for CS:GO Bomb Defusal Mode

This document describes how to test the CS:GO bomb defusal gameplay implementation.

## Prerequisites

1. Build the mod (requires Forge Gradle setup)
2. Install in a Minecraft 1.20.1 server or client with Forge 47.4.10
3. Have operator permissions (level 2+)

## Test Scenarios

### Test 1: Basic Setup

**Objective**: Verify map configuration commands work correctly.

**Steps**:
1. Join the server
2. Stand at desired Terrorist spawn location
3. Run: `/mcgo setspawn t`
4. Expected: Success message with coordinates
5. Stand at desired CT spawn location
6. Run: `/mcgo setspawn ct`
7. Expected: Success message with coordinates
8. Stand at bomb site A center
9. Run: `/mcgo addsite A 10`
10. Expected: Success message confirming site creation
11. Stand at bomb site B center
12. Run: `/mcgo addsite B 10`
13. Expected: Success message confirming site creation

**Pass Criteria**:
- All commands execute successfully
- Messages display correct coordinates
- Data persists across server restarts

### Test 2: Team Assignment

**Objective**: Verify team assignment system works correctly.

**Steps**:
1. Get two or more players
2. Run: `/mcgo team @a t`
3. Expected: All players assigned to Terrorists
4. Run: `/mcgo team Player1 ct`
5. Expected: Player1 assigned to Counter-Terrorists
6. Run: `/mcgo team Player2 spec`
7. Expected: Player2 assigned to Spectator

**Pass Criteria**:
- Players receive team assignment messages
- Team colors display correctly
- Team data persists

### Test 3: Round Start and Flow

**Objective**: Verify round management system functions correctly.

**Steps**:
1. Assign at least 2 players to T and CT teams
2. Run: `/mcgo start`
3. Expected: Game starts, round 1 begins
4. Observe freeze time (15 seconds)
5. Expected: Players spawn at team spawns, can move
6. Wait for freeze time to end
7. Expected: "Round Started!" message appears
8. Observe round timer
9. Expected: Round lasts 1:55 if no bomb planted

**Pass Criteria**:
- Round states transition correctly
- Timers work as expected
- Players spawn at correct locations
- Chat messages appear

### Test 4: Bomb Plant Mechanics

**Objective**: Verify bomb planting works correctly.

**Steps**:
1. Start a round
2. Give Terrorist player bomb: `/give @p mcgo_forge:bomb`
3. Have T player go to bomb site
4. Right-click with bomb
5. Expected: "Bomb planted at site X!" message
6. Expected: Bomb removed from inventory
7. Expected: Timer resets to 45 seconds
8. Expected: All players see "Bomb has been planted!" message

**Pass Criteria**:
- Bomb only plants in bomb sites
- Only Terrorists can plant
- Bomb removal works
- Timer resets correctly
- Messages broadcast to all players

### Test 5: Bomb Defuse Mechanics

**Objective**: Verify bomb defusing works correctly.

**Steps**:
1. Start a round and plant bomb
2. Give CT player defuse kit: `/give @p mcgo_forge:defuse_kit`
3. Have CT player approach bomb location
4. Right-click with defuse kit
5. Expected: "Bomb defused!" message
6. Expected: CT team wins round
7. Expected: Score updates

**Pass Criteria**:
- Only CT can use defuse kit
- Defuse only works on planted bomb
- Range check works (5 blocks)
- Round ends with CT victory
- Scores update correctly

### Test 6: Team Elimination Win

**Objective**: Verify elimination win condition works.

**Steps**:
1. Start a round with 2v2 teams
2. Kill all members of one team
3. Expected: Round ends
4. Expected: Winning team announced
5. Expected: Scores update
6. Expected: Dead players enter spectator mode

**Pass Criteria**:
- Deaths tracked correctly
- Dead players become spectators
- Round ends when team eliminated
- Correct team wins
- Scores update

### Test 7: Time Expiration Win

**Objective**: Verify time expiration works correctly.

**Steps**:
1. Start a round
2. Do NOT plant bomb
3. Wait for 1:55 to elapse
4. Expected: "Time Expired!" message
5. Expected: CT team wins
6. Expected: Scores update

**Pass Criteria**:
- Timer counts down correctly
- CT wins on time expiration
- Round ends automatically
- Scores update

### Test 8: Bomb Explosion Win

**Objective**: Verify bomb explosion works correctly.

**Steps**:
1. Start a round
2. Plant bomb
3. Wait for 45 seconds after plant
4. Do NOT defuse
5. Expected: "Bomb Exploded!" message
6. Expected: T team wins
7. Expected: Scores update

**Pass Criteria**:
- Bomb timer counts down correctly
- T wins on explosion
- Round ends automatically
- Scores update

### Test 9: Economy System

**Objective**: Verify money system works correctly.

**Steps**:
1. Start first round
2. Check T player has $800
3. Check CT player has $800
4. Win round as T
5. Check T players have $800 + $3250 = $4050
6. Check CT players have $800 + $1400 = $2200
7. Start next round
8. Lose round as T
9. Check T players have $4050 + $1400 = $5450
10. Verify money caps at $16,000

**Pass Criteria**:
- Starting money correct ($800)
- Win bonus correct ($3250)
- Loss bonus correct ($1400)
- Money persists across rounds
- Money caps at $16,000

### Test 10: Death and Respawn

**Objective**: Verify death/respawn system works correctly.

**Steps**:
1. Start a round
2. Kill a player
3. Expected: Player becomes spectator
4. Expected: Player cannot interact
5. Wait for round to end
6. Expected: Player respawns at team spawn
7. Expected: Player returns to survival mode
8. Expected: Player's team unchanged

**Pass Criteria**:
- Dead players become spectators
- No respawn during round
- Respawn at round start
- Correct spawn locations
- Correct game mode restoration

### Test 11: Multi-Round Game

**Objective**: Verify game flow over multiple rounds.

**Steps**:
1. Play 5 rounds
2. Track scores
3. Verify money accumulation
4. Verify round counter increments
5. Verify consistent behavior

**Pass Criteria**:
- All rounds function correctly
- Scores persist
- Money accumulates properly
- No crashes or errors
- Consistent gameplay

### Test 12: Edge Cases

**Objective**: Test edge cases and error handling.

**Steps**:
1. Try to plant bomb outside bomb site
2. Expected: Error message
3. Try to plant bomb as CT
4. Expected: Error message
5. Try to defuse without planted bomb
6. Expected: Error message
7. Try to defuse as T
8. Expected: Error message
9. Try to defuse far from bomb
10. Expected: Error message
11. Test with no spawn points set
12. Test with no bomb sites set
13. Test with all spectators
14. Test with unbalanced teams (5v1)

**Pass Criteria**:
- Appropriate error messages for invalid actions
- No crashes
- Graceful handling of edge cases
- Clear feedback to players

## Performance Testing

### Test 13: Server Performance

**Objective**: Verify mod doesn't impact server performance.

**Steps**:
1. Monitor server TPS
2. Run multiple rounds
3. Check memory usage
4. Monitor tick time

**Pass Criteria**:
- No TPS drops
- No memory leaks
- Minimal tick time impact

## Integration Testing

### Test 14: Compatibility

**Objective**: Verify compatibility with existing systems.

**Steps**:
1. Test with other mods installed
2. Verify world save/load
3. Test on dedicated server
4. Test on client

**Pass Criteria**:
- No conflicts with other mods
- Data persists correctly
- Works on server and client
- No corruption issues

## Bug Reporting

If you find issues during testing:

1. Note the exact steps to reproduce
2. Include error messages/logs
3. Specify Minecraft version
4. Specify Forge version
5. List other mods installed
6. Include relevant screenshots

## Success Criteria Summary

For the implementation to be considered complete, all test scenarios must pass with:
- No crashes or errors
- Correct gameplay behavior
- Proper data persistence
- Clear user feedback
- Good performance
- Backwards compatibility maintained
