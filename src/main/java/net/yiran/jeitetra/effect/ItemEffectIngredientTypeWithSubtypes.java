package net.yiran.jeitetra.effect;

import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import se.mickelus.tetra.effect.ItemEffect;

public class ItemEffectIngredientTypeWithSubtypes implements IIngredientTypeWithSubtypes<ItemEffect,ItemEffect> {
    public static final ItemEffectIngredientTypeWithSubtypes INSTANCE = new ItemEffectIngredientTypeWithSubtypes();
    @Override
    public Class<? extends ItemEffect> getIngredientClass() {
        return ItemEffect.class;
    }

    @Override
    public Class<? extends ItemEffect> getIngredientBaseClass() {
        return ItemEffect.class;
    }

    @Override
    public ItemEffect getBase(ItemEffect itemEffect) {
        return ItemEffect.intuit;
    }
}
