package net.yiran.jeitetra.module;

import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import net.yiran.jeitetra.util.ModuleData;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.module.ModuleRegistry;

import java.util.Arrays;
import java.util.List;

public class ModuleSimpleRecipeManagerPlugin implements ISimpleRecipeManagerPlugin<ModuleData> {
    public static final ModuleSimpleRecipeManagerPlugin INSTANCE = new ModuleSimpleRecipeManagerPlugin();

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
    public List<ModuleData> getRecipesForInput(ITypedIngredient<?> iTypedIngredient) {
        var item = iTypedIngredient.getItemStack().get();
        return ((IModularItem) item.getItem()).getAllModules(item)
                .stream()
                .map(module -> new ModuleData(module, module.getVariantData(item)))
                .toList();
    }

    @Override
    public List<ModuleData> getRecipesForOutput(ITypedIngredient<?> iTypedIngredient) {
        return List.of();
    }

    @Override
    public List<ModuleData> getAllRecipes() {
        return ModuleRegistry.instance.getAllModules().stream().flatMap(module ->
                Arrays.stream(module.getVariantData()).map(variantData -> new ModuleData(module, variantData))
        ).toList();
    }
}
