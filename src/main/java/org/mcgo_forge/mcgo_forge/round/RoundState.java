package org.mcgo_forge.mcgo_forge.round;

public enum RoundState {
    WAITING,     // Waiting for players/setup
    FREEZETIME,  // Buy time / freeze time
    ACTIVE,      // Round in progress
    POST_ROUND   // Round ended, showing results
}
