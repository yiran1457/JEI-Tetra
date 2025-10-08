package net.yiran.jeitetra.itemsubtype;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.blocks.scroll.ScrollData;

import java.util.ArrayList;
import java.util.List;

public class ScrollSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
    public static final ScrollSubtypeInterpreter INSTANCE = new ScrollSubtypeInterpreter();
    public static final List<ItemStack> STACKS = new ArrayList<>();
    @Override
    public String apply(ItemStack itemStack, UidContext uidContext) {
        if (!itemStack.hasTag()) {
            return "";
        } else {
            STACKS.add(itemStack);
            return ScrollData.read(itemStack).key;
        }
    }
}
