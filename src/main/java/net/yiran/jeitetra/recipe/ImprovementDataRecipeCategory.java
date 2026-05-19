package net.yiran.jeitetra.recipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.yiran.jeitetra.ingredient.ImprovementDataIngredient;
import net.yiran.jeitetra.ingredient.ItemEffectIngredient;
import net.yiran.jeitetra.ingredient.renderer.ImprovementRecipeIngredientRenderer;
import net.yiran.jeitetra.util.GuiCustomData;
import net.yiran.jeitetra.util.GuiElementRecipeWidget;
import se.mickelus.tetra.module.data.ImprovementData;

public class ImprovementDataRecipeCategory extends AbstractRecipeCategory<ImprovementData> {
    public static RecipeType<ImprovementData> recipeType = RecipeType.create("jeitetra", "improvement", ImprovementData.class);

    public ImprovementDataRecipeCategory(IGuiHelper guiHelper) {
        super(
                recipeType, Component.translatable("jeitetra.material.title"),
                guiHelper.createDrawableItemLike(ForgeRegistries.ITEMS.getValue(new ResourceLocation("tetra:dragon_sinew"))), 175, 200
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ImprovementData improvementData, IFocusGroup iFocusGroup) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, (175 - 55) / 2, -1)
                .setCustomRenderer(ImprovementDataIngredient.INSTANCE, ImprovementRecipeIngredientRenderer.INSTANCE)
                .addIngredient(ImprovementDataIngredient.INSTANCE, improvementData);
        if (improvementData.effects != null)
            builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
                    .addIngredients(ItemEffectIngredient.INSTANCE, improvementData.effects.getValues().stream().toList());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 5, -1)
                .setCustomRenderer(ImprovementDataIngredient.INSTANCE, ImprovementDataIngredient.INSTANCE)
                .addIngredient(ImprovementDataIngredient.INSTANCE, improvementData);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, ImprovementData recipe, IFocusGroup focuses) {
        var element = new GuiCustomData(0, 25, 175, 175, recipe);
        var widget = new GuiElementRecipeWidget(element, new ScreenPosition(0, 0), 175, 200);
        builder.addWidget(widget);
        builder.addGuiEventListener(widget);
        super.createRecipeExtras(builder, recipe, focuses);
    }

    @Override
    public void draw(ImprovementData recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(
                new ResourceLocation("jeitetra", "textures/gui/background2.png"),
                -8, -8, 0,
                0, 0,
                192, 220,
                192, 220
        );
        /*
        var name = IModularItem.getImprovementName(recipe.key,recipe.level);
        var color = (recipe.key.hashCode() & 0x00FFFFFF) | 0xCC000000;
        var font = Minecraft.getInstance().font;
        guiGraphics.pose().pushPose();

        guiGraphics.pose().translate(-0.1 + (175 - font.getSplitter().stringWidth(name)) / 2, 2, 0);
        guiGraphics.drawString(font, name, 0, 0, color, false);
        guiGraphics.pose().translate(-0.4, -0.4, 0);
        guiGraphics.drawString(font, name, 0, 0, -1, false);

        guiGraphics.pose().popPose();*/
    }
}
