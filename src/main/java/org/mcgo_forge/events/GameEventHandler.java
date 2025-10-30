package org.mcgo_forge.events;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mcgo_forge.Mcgo_forge;
import org.mcgo_forge.commands.GameCommands;
import org.mcgo_forge.round.RoundManager;

@Mod.EventBusSubscriber(modid = Mcgo_forge.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GameEventHandler {
    
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            RoundManager.getInstance().tick();
        }
    }
    
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            RoundManager.getInstance().handlePlayerDeath(player);
        }
    }
    
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        GameCommands.register(event.getDispatcher());
    }
}
