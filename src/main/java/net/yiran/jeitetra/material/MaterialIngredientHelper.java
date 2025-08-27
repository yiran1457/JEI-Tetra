package net.yiran.jeitetra.material;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import se.mickelus.tetra.module.data.MaterialData;

public class MaterialIngredientHelper implements IIngredientHelper<MaterialData> {
    public static final MaterialIngredientHelper INSTANCE = new MaterialIngredientHelper();
    @Override
    public IIngredientType<MaterialData> getIngredientType() {
        return MaterialIngredientTypeWithSubtypes.INSTANCE;
    }

    @Override
    public String getDisplayName(MaterialData materialData) {
        return materialData.key;
    }

    @Override
    public String getUniqueId(MaterialData materialData, UidContext uidContext) {
        return materialData.key;
    }

    @Override
    public ResourceLocation getResourceLocation(MaterialData materialData) {
        return new ResourceLocation("tetra", "material");
    }

    @Override
    public MaterialData copyIngredient(MaterialData materialData) {
        return materialData;
    }

    @Override
    public String getErrorInfo(MaterialData materialData) {
        return materialData.key;
    }
}
