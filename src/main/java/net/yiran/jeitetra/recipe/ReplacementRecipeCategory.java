package net.yiran.jeitetra.recipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import net.yiran.jeitetra.util.Drawables;
import se.mickelus.tetra.module.ReplacementDefinition;

@SuppressWarnings({"removal", "all"})
public class ReplacementRecipeCategory  extends AbstractRecipeCategory<ReplacementDefinition> {
    public static RecipeType<ReplacementDefinition> recipeType = RecipeType.create("jeitetra", "replacement", ReplacementDefinition.class);
    public Minecraft mc;

    public ReplacementRecipeCategory(IGuiHelper guiHelper) {
        super(
                recipeType,
                Component.translatable("jeitetra.replacement.title"),
                guiHelper.createDrawableItemLike(ForgeRegistries.ITEMS.getValue(new ResourceLocation("tetra:basic_workbench"))),
                //new DrawableText("测！", 16, 16, -1),
                Drawables.REPLACE_BACK_GROUND.getWidth()-12, Drawables.REPLACE_BACK_GROUND.getHeight()-12
        );
        mc = Minecraft.getInstance();
    }

    @Override
    public IDrawable getBackground() {
        return Drawables.REPLACE_BACK_GROUND;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ReplacementDefinition replacementDefinition, IFocusGroup iFocusGroup) {
        IRecipeSlotBuilder inputSlotBuilder = builder.addInputSlot(36, 16).setBackground(Drawables.SLOT,0,0);
        if (replacementDefinition.predicate.tag != null) {
            inputSlotBuilder.addIngredients(Ingredient.of(replacementDefinition.predicate.tag));
        } else {
            inputSlotBuilder.addIngredients(Ingredient.of(replacementDefinition.predicate.items.stream().map(ItemStack::new)));
        }
        IRecipeSlotBuilder outputSlotBuilder = builder.addOutputSlot(164-12-16-36, 16).setBackground(Drawables.SLOT,0,0);
        outputSlotBuilder.addItemStack(replacementDefinition.itemStack);
    }

    @Override
    public void draw(ReplacementDefinition recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }
}
