package net.yiran.jeitetra.material;

import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import se.mickelus.tetra.module.data.MaterialData;

public class MaterialIngredientTypeWithSubtypes implements IIngredientTypeWithSubtypes<MaterialData,MaterialData> {
    public static final MaterialIngredientTypeWithSubtypes INSTANCE = new MaterialIngredientTypeWithSubtypes();
    @Override
    public Class<? extends MaterialData> getIngredientClass() {
        return MaterialData.class;
    }

    @Override
    public Class<? extends MaterialData> getIngredientBaseClass() {
        return MaterialData.class;
    }

    @Override
    public MaterialData getBase(MaterialData materialData) {
        return materialData;
    }
}
