package org.mcgo_forge.mcgo_forge.world;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GameMapData extends SavedData {
    private static final String DATA_NAME = "mcgo_forge_map_data";
    
    private BlockPos tSpawn;
    private BlockPos ctSpawn;
    private List<BombSite> bombSites = new ArrayList<>();
    
    public GameMapData() {
        super();
    }
    
    public static GameMapData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                GameMapData::load,
                GameMapData::new,
                DATA_NAME
        );
    }
    
    public static GameMapData load(CompoundTag tag) {
        GameMapData data = new GameMapData();
        
        if (tag.contains("TSpawn")) {
            CompoundTag spawnTag = tag.getCompound("TSpawn");
            data.tSpawn = new BlockPos(
                    spawnTag.getInt("x"),
                    spawnTag.getInt("y"),
                    spawnTag.getInt("z")
            );
        }
        
        if (tag.contains("CTSpawn")) {
            CompoundTag spawnTag = tag.getCompound("CTSpawn");
            data.ctSpawn = new BlockPos(
                    spawnTag.getInt("x"),
                    spawnTag.getInt("y"),
                    spawnTag.getInt("z")
            );
        }
        
        if (tag.contains("BombSites")) {
            CompoundTag sitesTag = tag.getCompound("BombSites");
            int count = sitesTag.getInt("Count");
            for (int i = 0; i < count; i++) {
                CompoundTag siteTag = sitesTag.getCompound("Site" + i);
                String name = siteTag.getString("Name");
                BlockPos center = new BlockPos(
                        siteTag.getInt("x"),
                        siteTag.getInt("y"),
                        siteTag.getInt("z")
                );
                int radius = siteTag.getInt("Radius");
                data.bombSites.add(new BombSite(name, center, radius));
            }
        }
        
        return data;
    }
    
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        if (tSpawn != null) {
            CompoundTag spawnTag = new CompoundTag();
            spawnTag.putInt("x", tSpawn.getX());
            spawnTag.putInt("y", tSpawn.getY());
            spawnTag.putInt("z", tSpawn.getZ());
            tag.put("TSpawn", spawnTag);
        }
        
        if (ctSpawn != null) {
            CompoundTag spawnTag = new CompoundTag();
            spawnTag.putInt("x", ctSpawn.getX());
            spawnTag.putInt("y", ctSpawn.getY());
            spawnTag.putInt("z", ctSpawn.getZ());
            tag.put("CTSpawn", spawnTag);
        }
        
        if (!bombSites.isEmpty()) {
            CompoundTag sitesTag = new CompoundTag();
            sitesTag.putInt("Count", bombSites.size());
            for (int i = 0; i < bombSites.size(); i++) {
                BombSite site = bombSites.get(i);
                CompoundTag siteTag = new CompoundTag();
                siteTag.putString("Name", site.name());
                siteTag.putInt("x", site.center().getX());
                siteTag.putInt("y", site.center().getY());
                siteTag.putInt("z", site.center().getZ());
                siteTag.putInt("Radius", site.radius());
                sitesTag.put("Site" + i, siteTag);
            }
            tag.put("BombSites", sitesTag);
        }
        
        return tag;
    }
    
    public BlockPos getTSpawn() {
        return tSpawn;
    }
    
    public void setTSpawn(BlockPos pos) {
        this.tSpawn = pos;
        setDirty();
    }
    
    public BlockPos getCTSpawn() {
        return ctSpawn;
    }
    
    public void setCTSpawn(BlockPos pos) {
        this.ctSpawn = pos;
        setDirty();
    }
    
    public List<BombSite> getBombSites() {
        return bombSites;
    }
    
    public void addBombSite(String name, BlockPos center, int radius) {
        bombSites.add(new BombSite(name, center, radius));
        setDirty();
    }
    
    public void clearBombSites() {
        bombSites.clear();
        setDirty();
    }
    
    public BombSite getBombSiteAt(BlockPos pos) {
        for (BombSite site : bombSites) {
            if (site.contains(pos)) {
                return site;
            }
        }
        return null;
    }
    
    public record BombSite(String name, BlockPos center, int radius) {
        public boolean contains(BlockPos pos) {
            return center.distSqr(pos) <= radius * radius;
        }
    }
}
