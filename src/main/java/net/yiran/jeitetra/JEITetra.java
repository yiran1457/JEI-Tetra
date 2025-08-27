package net.yiran.jeitetra;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.yiran.jeitetra.effect.ItemEffectLangManager;
import org.slf4j.Logger;

@Mod(JEITetra.MODID)
@SuppressWarnings({"all", "removal"})
public class JEITetra {
    public static final String MODID = "jeitetra";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static boolean TMRVLOAD;

    public JEITetra() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(ItemEffectLangManager.instance::onReload);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        TMRVLOAD = ModList.get().isLoaded("toomanyrecipeviewers");
    }

}
