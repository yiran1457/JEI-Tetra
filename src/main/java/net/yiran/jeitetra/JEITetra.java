package net.yiran.jeitetra;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkHooks;
import org.slf4j.Logger;
import se.mickelus.tetra.blocks.workbench.WorkbenchTile;

@Mod(JEITetra.MODID)
@SuppressWarnings({"all", "removal"})
public class JEITetra {
    public static final String MODID = "jeitetra";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static boolean TMRVLOAD;

    public JEITetra() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        TMRVLOAD = ModList.get().isLoaded("toomanyrecipeviewers");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void right(PlayerInteractEvent.RightClickBlock event) {
        var pos = event.getPos();
        var level = event.getLevel();
        if (level.isClientSide()) return;
        var be = level.getBlockEntity(pos, WorkbenchTile.type.get()).orElse(null);
        if (be == null) return;
        var stack = event.getItemStack();
        if (stack.is(Items.DIAMOND_AXE)) {
            var tag = stack.getOrCreateTag();
            tag.putInt("x", pos.getX());
            tag.putInt("y", pos.getY());
            tag.putInt("z", pos.getZ());
            tag.putString("level",level.dimension().location().toString());
            event.setUseBlock(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onright(PlayerInteractEvent.RightClickItem event) {
        var stack = event.getItemStack();
        var level = event.getLevel();
        var player = event.getEntity();
        if(level.isClientSide()) return;
        if(player instanceof ServerPlayer serverPlayer) {
            if (stack.is(Items.DIAMOND_AXE) && stack.hasTag()) {
                var tag = stack.getTag();
                if (tag.contains("x")) {
                    var rkd = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(tag.getString("level")));
                    var pLevel = serverPlayer.server.getLevel(rkd);
                    var pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
                    var be = level.getBlockEntity(pos, WorkbenchTile.type.get()).orElse(null);
                    //NetworkHooks.openScreen(serverPlayer, be,friendlyByteBuf -> friendlyByteBuf.writeGlobalPos(GlobalPos.of(rkd, pos)));
                    NetworkHooks.openScreen(serverPlayer, be,friendlyByteBuf -> friendlyByteBuf.writeBlockPos(pos));
                    //serverPlayer.openMenu(level.getBlockEntity(pos, WorkbenchTile.type.get()).orElse(null));
                }
            }
        }
    }
}
