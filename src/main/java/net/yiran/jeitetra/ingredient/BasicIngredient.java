package net.yiran.jeitetra.ingredient;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;

public interface BasicIngredient<T> extends IIngredientType<T>, IIngredientHelper<T>, IIngredientRenderer<T> {
    default IIngredientType<T> getIngredientType() {
        return this;
    }
}
