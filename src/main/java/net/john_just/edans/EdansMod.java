package net.john_just.edans;

import net.john_just.edans.chat.ChatCommand;
import net.john_just.edans.chat.ChatHandler;
import net.john_just.edans.command.ProfileCommand;
import net.john_just.edans.command.RoleCommand;
import net.john_just.edans.data.PlayerProfile;
import net.john_just.edans.event.PlayerConnectionHandler;
import net.john_just.edans.skill.SkillEventHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(EdansMod.MOD_ID)
public class EdansMod
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "johndeadlystorieschatmod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public EdansMod(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        //modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Регистрация обработчиков
        NeoForge.EVENT_BUS.register(new ChatHandler());
        NeoForge.EVENT_BUS.register(new PlayerConnectionHandler());
        //NeoForge.EVENT_BUS.register(new PlayerSkillHandler());

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    // Регистрация команд
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        RoleCommand.register(event.getDispatcher());
        ChatCommand.register(event.getDispatcher());
        ProfileCommand.register(event.getDispatcher());
    }

    // Запрещаем рендерить имя игрока
    @SubscribeEvent
    public void onRenderNameTag(RenderNameTagEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCanRender(TriState.FALSE);
        }
    }

    // 3апрет установки точки во3рождения чере3 кровати и якоря
    @SubscribeEvent
    public void setSpawn(PlayerSetSpawnEvent event) {
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide) return;
        PlayerProfile profile = PlayerProfile.load(player);

        BlockPos pos = event.getPos();
        String blockName = event.getState().getBlock().getName().getString();

        Component msg = Component.literal(player.getName().getString() + " Сломал блок: " + blockName +
                " на " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()).withStyle(ChatFormatting.GREEN);

        player.getServer().getPlayerList().broadcastSystemMessage(msg, false);

        profile.save(player.getServer());
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        // 2) Регистрируем класс-слушатель на Forge Event Bus
        NeoForge.EVENT_BUS.register(SkillEventHandler.class);
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
