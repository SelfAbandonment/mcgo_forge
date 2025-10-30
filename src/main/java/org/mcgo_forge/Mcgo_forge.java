package org.mcgo_forge;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.mcgo_forge.items.BombItem;
import org.mcgo_forge.items.DefuseKitItem;
import org.slf4j.Logger;

// 此处的值应与 META-INF/mods.toml 文件中的条目相匹配
@Mod(Mcgo_forge.MODID)
@SuppressWarnings({"removal","deprecation"})
public class Mcgo_forge {

    // 在一个公共位置定义 mod id，供各处引用
    public static final String MODID = "mcgo_forge";
    // 直接引用 slf4j 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();
    // 创建一个延迟注册器用于注册方块，所有方块将注册到命名空间 "mcgo_forge" 下
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // 创建一个延迟注册器用于注册物品，所有物品将注册到命名空间 "mcgo_forge" 下
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // 创建一个延迟注册器用于注册创造模式标签，所有标签将注册到命名空间 "mcgo_forge" 下
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // 创建一个新的方块，id 为 "mcgo_forge:example_block"，由命名空间和路径组合而成
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    // 创建一个新的方块物品，id 为 "mcgo_forge:example_block"，由命名空间和路径组合而成
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    // 创建一个新的食物物品，id 为 "mcgo_forge:example_id"，饱食度 1，饱和度 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));

    // CS:GO 炸弹物品
    public static final RegistryObject<Item> BOMB = ITEMS.register("bomb", () -> new BombItem(new Item.Properties().stacksTo(1)));
    
    // CS:GO 拆弹器物品
    public static final RegistryObject<Item> DEFUSE_KIT = ITEMS.register("defuse_kit", () -> new DefuseKitItem(new Item.Properties().stacksTo(1)));

    // 创建一个创造模式标签，id 为 "mcgo_forge:example_tab"，用于示例物品，位于战斗标签之后
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
             .withTabsBefore(CreativeModeTabs.COMBAT)
             .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
             .displayItems((parameters, output) -> {
             output.accept(EXAMPLE_ITEM.get()); // 将示例物品加入该标签。对你自己的标签，优先使用该方法而非事件
             output.accept(BOMB.get()); // 加入炸弹物品
             output.accept(DEFUSE_KIT.get()); // 加入拆弹器物品
             }).build());

    public Mcgo_forge() {
         IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册用于 mod 加载的 commonSetup 回调
        modEventBus.addListener(this::commonSetup);

        // 将延迟注册器注册到 mod 事件总线来完成方块注册
        BLOCKS.register(modEventBus);
        // 将延迟注册器注册到 mod 事件总线来完成物品注册
        ITEMS.register(modEventBus);
        // 将延迟注册器注册到 mod 事件总线来完成创造标签注册
        CREATIVE_MODE_TABS.register(modEventBus);

        // 为我们感兴趣的服务器与其他游戏事件注册监听
        MinecraftForge.EVENT_BUS.register(this);

        // 将物品加入创造标签
        modEventBus.addListener(this::addCreative);

        // 注册本 mod 的 ForgeConfigSpec，以便 Forge 为我们创建并加载配置文件
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // 一些通用的初始化代码
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // 将示例方块物品加入建筑方块标签
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);
    }
    // 你可以使用 SubscribeEvent 并让事件总线自动发现需要调用的方法
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // 服务器启动时执行一些操作
        LOGGER.info("HELLO from server starting");
        LOGGER.info("MCGO: CS:GO Bomb Defusal Mode initialized");
    }

    // 也可以使用 EventBusSubscriber 注解来自动注册类中所有带有 @SubscribeEvent 的静态方法
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // 一些客户端初始化代码
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
