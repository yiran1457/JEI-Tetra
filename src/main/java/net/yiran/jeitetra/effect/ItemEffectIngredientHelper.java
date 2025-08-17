package net.yiran.jeitetra.effect;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import se.mickelus.tetra.effect.ItemEffect;

public class ItemEffectIngredientHelper implements IIngredientHelper<ItemEffect> {
    public static final ItemEffectIngredientHelper INSTANCE = new ItemEffectIngredientHelper();

    @Override
    public IIngredientType<ItemEffect> getIngredientType() {
        return ItemEffectIngredientTypeWithSubtypes.INSTANCE;
    }

    @Override
    public String getDisplayName(ItemEffect itemEffect) {
        return itemEffect.getKey();
    }

    @Override
    public String getUniqueId(ItemEffect itemEffect, UidContext uidContext) {
        return itemEffect.getKey();
    }

    @Override
    public ResourceLocation getResourceLocation(ItemEffect itemEffect) {
        var name = itemEffect.getKey()
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
        if (name.contains(":")) {
            return new ResourceLocation(name);
        }
        return new ResourceLocation("tetra", name);
    }

    @Override
    public ItemEffect copyIngredient(ItemEffect itemEffect) {
        return itemEffect;
    }

    @Override
    public String getErrorInfo(ItemEffect itemEffect) {
        return "jei_tetra";
    }
}
