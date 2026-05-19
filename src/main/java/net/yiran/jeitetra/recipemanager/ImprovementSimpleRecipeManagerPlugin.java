package net.yiran.jeitetra.recipemanager;

import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.module.data.ImprovementData;

import java.util.Arrays;
import java.util.List;

public class ImprovementSimpleRecipeManagerPlugin implements ISimpleRecipeManagerPlugin<ImprovementData> {
    public static final ImprovementSimpleRecipeManagerPlugin INSTANCE = new ImprovementSimpleRecipeManagerPlugin();

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
    public List<ImprovementData> getRecipesForInput(ITypedIngredient<?> iTypedIngredient) {
        var item = iTypedIngredient.getItemStack().get();
        return Arrays.stream(((IModularItem) item.getItem()).getImprovements(item))
                .toList();
    }

    @Override
    public List<ImprovementData> getRecipesForOutput(ITypedIngredient<?> iTypedIngredient) {
        return List.of();
    }

    @Override
    public List<ImprovementData> getAllRecipes() {
        return DataManager.instance.improvementData.getData().values().stream().flatMap(Arrays::stream).toList();
    }
}
