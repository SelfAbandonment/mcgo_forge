package org.mcgo_forge.gameplay;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerGameData implements INBTSerializable<CompoundTag> {
    private Team team = Team.SPECTATOR;
    private int money = 800;
    private boolean isAlive = true;

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = Math.max(0, Math.min(16000, money));
    }

    public void addMoney(int amount) {
        setMoney(money + amount);
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("team", team.name());
        tag.putInt("money", money);
        tag.putBoolean("isAlive", isAlive);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        try {
            this.team = Team.valueOf(tag.getString("team"));
        } catch (IllegalArgumentException e) {
            this.team = Team.SPECTATOR;
        }
        this.money = tag.getInt("money");
        this.isAlive = tag.getBoolean("isAlive");
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final PlayerGameData data = new PlayerGameData();
        private final LazyOptional<PlayerGameData> optional = LazyOptional.of(() -> data);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return PlayerCapabilityProvider.PLAYER_GAME_DATA.orEmpty(cap, optional);
        }

        @Override
        public CompoundTag serializeNBT() {
            return data.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            data.deserializeNBT(nbt);
        }

        public void invalidate() {
            optional.invalidate();
        }
    }
}
