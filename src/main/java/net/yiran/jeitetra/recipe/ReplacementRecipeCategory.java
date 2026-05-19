package net.yiran.jeitetra.recipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.yiran.jeitetra.util.Drawables;
import net.yiran.jeitetra.util.ItemUtil;
import se.mickelus.tetra.module.ReplacementDefinition;

@SuppressWarnings({"removal", "all"})
public class ReplacementRecipeCategory extends AbstractRecipeCategory<ReplacementDefinition> {
    public static RecipeType<ReplacementDefinition> recipeType = RecipeType.create("jeitetra", "replacement", ReplacementDefinition.class);
    public Minecraft mc;

    public ReplacementRecipeCategory(IGuiHelper guiHelper) {
        super(
                recipeType,
                Component.translatable("jeitetra.replacement.title"),
                guiHelper.createDrawableItemLike(ForgeRegistries.ITEMS.getValue(new ResourceLocation("tetra:basic_workbench"))),
                Drawables.REPLACE_BACK_GROUND.getWidth() - 12, Drawables.REPLACE_BACK_GROUND.getHeight() - 12
        );
        mc = Minecraft.getInstance();
    }

    @Override
    public IDrawable getBackground() {
        return Drawables.REPLACE_BACK_GROUND;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ReplacementDefinition replacementDefinition, IFocusGroup iFocusGroup) {
        IRecipeSlotBuilder inputSlotBuilder = builder.addInputSlot(36, 16).setBackground(Drawables.SLOT, 0, 0);

        IRecipeSlotBuilder outputSlotBuilder = builder.addOutputSlot(164 - 12 - 16 - 36, 16).setBackground(Drawables.SLOT, 0, 0);
        outputSlotBuilder.addItemStack(replacementDefinition.itemStack);

        var items = ItemUtil.getItemsFromItemPredicate(replacementDefinition.predicate);
        if (items == null || items.isEmpty()) return;
        inputSlotBuilder.addItemStacks(items);
    }

}
