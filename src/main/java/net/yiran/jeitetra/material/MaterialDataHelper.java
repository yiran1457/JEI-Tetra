package net.yiran.jeitetra.material;

import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.module.data.MaterialData;

public class MaterialDataHelper {
    public static final MaterialDataHelper INSTANCE = new MaterialDataHelper();

    public String getMaterialKey(MaterialData data) {
        return "tetra.material." + data.key;
    }

    public String getMaterialCategoryKey(MaterialData data) {
        return "tetra.variant_category." + data.category + ".label";
    }

    public MaterialData getMaterialData(String materialKey) {
        return DataManager.instance.materialData
                .getData()
                .values()
                .stream()
                .filter(materialData -> materialKey.endsWith("/" + materialData.key))
                .findAny()
                .orElse(null);
    }
}
