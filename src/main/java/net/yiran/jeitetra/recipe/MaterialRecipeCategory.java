package net.yiran.jeitetra.recipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.runtime.IRecipesGui;
import mezz.jei.common.Internal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.yiran.jeitetra.effect.ItemEffectIngredientTypeWithSubtypes;
import net.yiran.jeitetra.material.MaterialIngredientTypeWithSubtypes;
import net.yiran.jeitetra.material.MaterialRecipeIngredientRenderer;
import net.yiran.jeitetra.util.Drawables;
import net.yiran.jeitetra.util.GuiCustomData;
import net.yiran.jeitetra.util.GuiElementRecipeWidget;
import se.mickelus.tetra.module.data.MaterialData;

import java.util.List;

@SuppressWarnings({"removal", "all"})
public class MaterialRecipeCategory extends AbstractRecipeCategory<MaterialData> {
    public static RecipeType<MaterialData> recipeType = RecipeType.create("jeitetra", "material", MaterialData.class);
    public Minecraft mc;
    public static Object[] prarm = new String[]{"n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n"};

    public MaterialRecipeCategory(IGuiHelper guiHelper) {
        super(recipeType, Component.translatable("jeitetra.material.title"),
                guiHelper.createDrawableItemLike(ForgeRegistries.ITEMS.getValue(new ResourceLocation("tetra:dragon_sinew"))), 175, 200);
        mc = Minecraft.getInstance();
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, MaterialData recipe, IFocusGroup focuses) {
        IRecipesGui recipesGui = Internal.getJeiRuntime().getRecipesGui();
        var element = new GuiCustomData(0, 25, 175, 175, recipe);
        var widget = new GuiElementRecipeWidget(element, new ScreenPosition(0, 0), 175, 200);
        builder.addWidget(widget);
        builder.addGuiEventListener(widget);
        super.createRecipeExtras(builder, recipe, focuses);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, MaterialData materialData, IFocusGroup iFocusGroup) {
        var effects = materialData.effects.getValues();
        if (!effects.isEmpty())
            iRecipeLayoutBuilder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addIngredients(ItemEffectIngredientTypeWithSubtypes.INSTANCE, effects.stream().toList());
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, (175 - 55) / 2, 1)
                .setCustomRenderer(MaterialIngredientTypeWithSubtypes.INSTANCE, MaterialRecipeIngredientRenderer.INSTANCE)
                .addIngredient(MaterialIngredientTypeWithSubtypes.INSTANCE, materialData);
        var items = getItems(materialData);
        if (items == null || items.isEmpty()) return;
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 5, 1).setBackground(Drawables.SLOT, 0, 0).addIngredients(VanillaTypes.ITEM_STACK, items);
    }

    public static List<ItemStack> getItems(MaterialData data) {
        if (data.material.getPredicate() == null) return null;
        return EffectRecipeCategory.getItems(data.material.getPredicate());
    }

    @Override
    public void draw(MaterialData recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(
                new ResourceLocation("jeitetra", "textures/gui/background2.png"),
                -8, -8, 0,
                0, 0,
                192, 220,
                192, 220
        );

    }
}
