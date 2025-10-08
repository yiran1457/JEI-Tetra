package net.yiran.jeitetra.util;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.List;

public class ItemUtil {
    @Nullable
    public static List<ItemStack> getItemsFromItemPredicate(@Nullable ItemPredicate itemPredicate) {
        if (itemPredicate == null) return null;
        if (itemPredicate.items != null) {
            if (itemPredicate.nbt.equals(NbtPredicate.ANY)) {
                return itemPredicate.items.stream()
                        .map(ItemStack::new)
                        .toList();
            } else {
                return itemPredicate.items.stream()
                        .map(item -> {
                            var stack = new ItemStack(item);
                            stack.setTag(itemPredicate.nbt.tag);
                            return stack;
                        })
                        .toList();
            }
        } else if (itemPredicate.tag != null) {
            return List.of(Ingredient.of(itemPredicate.tag).getItems());
        }
        return null;
    }
}
