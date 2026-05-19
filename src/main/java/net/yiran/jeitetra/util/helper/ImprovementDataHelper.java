package net.yiran.jeitetra.util.helper;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.module.data.GlyphData;
import se.mickelus.tetra.module.data.ImprovementData;

import java.util.Arrays;
import java.util.Map;

public class ImprovementDataHelper {
    public static Map<ImprovementData, GlyphData> GlyphCache = new Object2ObjectOpenHashMap<>();

    public static void ClearCache() {
        GlyphCache.clear();
    }

    public static GlyphData getGlyphData(ImprovementData improvementData) {
        if (!GlyphCache.containsKey(improvementData)) {
            DataManager.instance.schematicData.getData().values()
                    .stream()
                    .filter(schematicDefinition -> {
                        return Arrays.stream(schematicDefinition.outcomes)
                                .map(outcomeDefinition -> outcomeDefinition.improvements)
                                .anyMatch(stringIntegerMap -> {
                                    if (stringIntegerMap.containsKey(improvementData.key))
                                        return stringIntegerMap.get(improvementData.key) == improvementData.level;
                                    return false;
                                });
                    })
                    .findFirst()
                    .ifPresentOrElse(schematicDefinition -> {
                        GlyphCache.put(improvementData, schematicDefinition.glyph);
                    }, () -> GlyphCache.put(improvementData, null));
        }
        return GlyphCache.get(improvementData);
    }
}
