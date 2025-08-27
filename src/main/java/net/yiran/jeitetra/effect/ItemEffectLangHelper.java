package net.yiran.jeitetra.effect;

import net.minecraft.client.resources.language.I18n;
import se.mickelus.tetra.effect.ItemEffect;

import java.util.Arrays;
import java.util.List;

public class ItemEffectLangHelper {
    public static List<String> prefixs = Arrays.asList(
            "tetra.stats.",
            "tetra.stats.toolbelt.",
            "tetra.stats.bow.",
            "tetra.stats.holo.",
            "tetra.stats.tool."
    );
    public static List<String> suffixs = Arrays.asList(
            ".tooltip",
            ".tooltip_short"
    );

    public static String getDisplayKey(ItemEffect itemEffect) {
        var effect = itemEffect.getKey();
        var effect3 = effect.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        for (var prefix : prefixs) {
            if (I18n.exists(prefix + effect)) {
                return prefix + effect;
            }
            if (I18n.exists(prefix + effect3)) {
                return prefix + effect3;
            }
        }
        return "tetra.stats." + effect;
    }

    public static String getDescKey(ItemEffect itemEffect) {
        var effect = itemEffect.getKey();
        var effect3 = effect.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        if (I18n.exists("jeitetra.effect." + effect + ".desc")) {
            return "jeitetra.effect." + effect + ".desc";
        }
        for (var prefix : prefixs) {
            for (var suffix : suffixs) {
                if (I18n.exists(prefix + effect + suffix)) {
                    return prefix + effect + suffix;
                }
                if (I18n.exists(prefix + effect3 + suffix)) {
                    return prefix + effect3 + suffix;
                }
            }
        }
        return "jeitetra.effect." + effect + ".desc";
    }
}
