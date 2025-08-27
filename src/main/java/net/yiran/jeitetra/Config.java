package net.yiran.jeitetra;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class Config
{
    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.ConfigValue<Boolean> JeiSubtypeScrollItem;
    public static final ForgeConfigSpec.ConfigValue<Boolean> OutputReplacementItem;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ShowAllEffects;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ShowAllMaterials;
    public static final ForgeConfigSpec.ConfigValue<List<String>> JeiSubtypeModularItem;

    static {
        JeiSubtypeScrollItem = BUILDER
                .define("JeiSubtypeScrollItem", true);
        OutputReplacementItem = BUILDER
                .define("OutputReplacementItem", true);
        ShowAllEffects = BUILDER
                .define("ShowAllEffects", true);
        ShowAllMaterials = BUILDER
                .define("ShowAllMaterials", false);
        JeiSubtypeModularItem = BUILDER
                .define("JeiSubtypeModularItem", Arrays.asList(
                        "tetra:modular_toolbelt",
                        "tetra:modular_double",
                        "tetra:modular_bow",
                        "tetra:modular_crossbow",
                        "tetra:modular_shield",
                        "tetra:modular_single",
                        "tetra:modular_sword"
                ));
        SPEC = BUILDER.build();
    }
}
