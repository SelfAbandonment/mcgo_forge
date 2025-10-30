package org.mcgo_forge.gameplay;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mcgo_forge.Mcgo_forge;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = Mcgo_forge.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerCapabilityProvider {
    public static final Capability<PlayerGameData> PLAYER_GAME_DATA = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation PLAYER_GAME_DATA_ID = Objects.requireNonNull(ResourceLocation.tryParse(Mcgo_forge.MODID + ":" + "player_game_data"));

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerGameData.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            PlayerGameData.Provider provider = new PlayerGameData.Provider();
            event.addCapability(PLAYER_GAME_DATA_ID, provider);
        }
    }

    public static PlayerGameData getPlayerData(Player player) {
        return player.getCapability(PLAYER_GAME_DATA).orElse(new PlayerGameData());
    }
}
