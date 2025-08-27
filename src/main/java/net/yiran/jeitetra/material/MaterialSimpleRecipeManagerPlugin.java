package net.yiran.jeitetra.material;

import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.module.data.MaterialData;

import java.util.List;

public class MaterialSimpleRecipeManagerPlugin implements ISimpleRecipeManagerPlugin<MaterialData> {
    public static final MaterialSimpleRecipeManagerPlugin INSTANCE = new MaterialSimpleRecipeManagerPlugin();
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
    public List<MaterialData> getRecipesForInput(ITypedIngredient<?> iTypedIngredient) {
        var item = iTypedIngredient.getItemStack().get();

        var keys = ((IModularItem)item.getItem()).getAllModules(item)
                .stream()
                .map(module -> module.getVariantData(item).key.replace(module.getKey().replace(module.getSlot()+"/","")+"/",""))
                //.map(variantData -> variantData.key)
                .toList();
        return getAllRecipes()
                .stream()
                .filter(materialData ->
                                keys.stream().anyMatch(s -> s.endsWith("/"+materialData.key))
                        )
                .toList();
        //return new ArrayList<>(((IModularItem)item.getItem()).getEffects(item));

        //return List.of();
    }

    @Override
    public List<MaterialData> getRecipesForOutput(ITypedIngredient<?> iTypedIngredient) {
        return List.of();
    }

    @Override
    public List<MaterialData> getAllRecipes() {
        return DataManager.instance.materialData.getData().values().stream().toList();
    }
}
