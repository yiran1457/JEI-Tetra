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
        //if (uidContext.equals(UidContext.Ingredient)) {
        //    return itemStack.getTag().getString("id");
        //} else {
        if (itemStack.getItem() instanceof IModularItem modularItem) {
            var a = modularItem.getAllModules(itemStack)
                    .stream()
                    //.sorted()
                    ;
            var b = a.map(v -> v.getSlot() + v.getVariantData(itemStack).key)
                    .toArray(String[]::new);
            var z = String.join("", b);
            return z;
        }
        return "";
        //}
    }
}
