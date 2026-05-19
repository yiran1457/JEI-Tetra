package net.yiran.jeitetra.itemsubtype;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.items.modular.IModularItem;

public class ModularSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
    public static final ModularSubtypeInterpreter INSTANCE = new ModularSubtypeInterpreter();

    @Override
    public String apply(ItemStack itemStack, UidContext uidContext) {
        if (!itemStack.hasTag()) return "";
        if (itemStack.getItem() instanceof IModularItem modularItem) {
            return String.join("", modularItem.getAllModules(itemStack)
                    .stream().map(v -> v.getSlot() + v.getVariantData(itemStack).key)
                    .toArray(String[]::new));
        }
        return "";
        //}
    }
}
