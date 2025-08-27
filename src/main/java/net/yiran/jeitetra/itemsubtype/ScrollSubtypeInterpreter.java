package net.yiran.jeitetra.itemsubtype;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.blocks.scroll.ScrollData;

public class ScrollSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
    public static final ScrollSubtypeInterpreter INSTANCE = new ScrollSubtypeInterpreter();
    @Override
    public String apply(ItemStack itemStack, UidContext uidContext) {
        if (!itemStack.hasTag()) {
            return "";
        } else {
            return ScrollData.read(itemStack).key;
        }
    }
}
