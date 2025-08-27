package net.yiran.jeitetra.material;

import se.mickelus.tetra.module.data.MaterialData;

public class MaterialDataHelper {
    public static final MaterialDataHelper INSTANCE = new MaterialDataHelper();

    public String getMaterialKey(MaterialData data) {
        return "tetra.material." + data.key;
    }
    public String getMaterialCategoryKey(MaterialData data) {
        return "tetra.variant_category."+data.category+".label";
    }
}
