package net.yiran.jeitetra.effect;

import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import net.yiran.jeitetra.TetraPlugin;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.IModularItem;

import java.util.ArrayList;
import java.util.List;

public class ItemEffectSimpleRecipeManagerPlugin implements ISimpleRecipeManagerPlugin<ItemEffect> {
    public static final ItemEffectSimpleRecipeManagerPlugin INSTANCE = new ItemEffectSimpleRecipeManagerPlugin();

    @Override
    public boolean isHandledInput(ITypedIngredient<?> iTypedIngredient) {
        var z = iTypedIngredient.getItemStack();
        return z.filter(itemStack -> itemStack.getItem() instanceof IModularItem).isPresent();
    }

    @Override
    public boolean isHandledOutput(ITypedIngredient<?> iTypedIngredient) {
        return false;
    }

    @Override
    public List<ItemEffect> getRecipesForInput(ITypedIngredient<?> iTypedIngredient) {
        var item = iTypedIngredient.getItemStack().get();
        return new ArrayList<>(((IModularItem)item.getItem()).getEffects(item));
    }

    @Override
    public List<ItemEffect> getRecipesForOutput(ITypedIngredient<?> iTypedIngredient) {
        return List.of();
    }

    @Override
    public List<ItemEffect> getAllRecipes() {
        return TetraPlugin.getAllEffects();
    }
}
