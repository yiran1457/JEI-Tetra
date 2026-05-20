package net.yiran.jeitetra.compat;

import net.minecraftforge.fml.ModList;
import net.yiran.jeitetra.util.GuiCustomData;
import net.yiran.jeitetra.util.GuiMultiInfo;
import net.yiran.jeitetra.util.I18nWrapper;
import net.yiran.tee.TetraExtendEnchant;
import net.yiran.tee.core.IEnchantmentsDataProvider;
import se.mickelus.tetra.module.data.MaterialData;
import se.mickelus.tetra.module.data.VariantData;

import java.util.stream.Collectors;

public class ExtendEnchantCompat {
    public static boolean isLoad = ModList.get().isLoaded(TetraExtendEnchant.MODID);

    public static void addVariantData(GuiCustomData data, VariantData variantData) {
        if (!isLoad) return;
        addEnchantData(data, (IEnchantmentsDataProvider) variantData);
    }

    public static void addMaterialData(GuiCustomData data, MaterialData materialData) {
        if (!isLoad) return;
        addEnchantData(data, (IEnchantmentsDataProvider) materialData);
    }

    private static void addEnchantData(GuiCustomData data, IEnchantmentsDataProvider dataProvider) {
        var enchantments = dataProvider.getEnchantmentData().getEnchantmentsMap();
        if (enchantments.isEmpty()) return;
        data.addChild(new GuiMultiInfo(5, "enchantmentsT", enchantments
                .entrySet().stream()
                .collect(Collectors.toMap(
                        e -> I18nWrapper.get( e.getKey().getDescriptionId()),
                        e -> String.format("%.02f",e.getValue())
                ))
        ));
    }
}
