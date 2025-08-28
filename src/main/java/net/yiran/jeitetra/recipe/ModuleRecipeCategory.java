package net.yiran.jeitetra.recipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.yiran.jeitetra.effect.ItemEffectIngredientTypeWithSubtypes;
import net.yiran.jeitetra.material.MaterialDataHelper;
import net.yiran.jeitetra.material.MaterialIngredientTypeWithSubtypes;
import net.yiran.jeitetra.util.GuiCustomData;
import net.yiran.jeitetra.util.GuiElementRecipeWidget;
import net.yiran.jeitetra.util.ModuleData;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.ItemModuleMajor;

import java.awt.*;

public class ModuleRecipeCategory extends AbstractRecipeCategory<ModuleData> {
    public static RecipeType<ModuleData> recipeType = RecipeType.create("jeitetra", "module", ModuleData.class);
    public Minecraft mc;

    public ModuleRecipeCategory(IGuiHelper guiHelper) {
        super(recipeType, Component.translatable("jeitetra.module.title"),
                guiHelper.createDrawableItemLike(ForgeRegistries.ITEMS.getValue(new ResourceLocation("tetra:earthpiercer"))), 175, 200);
        mc = Minecraft.getInstance();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, ModuleData moduleData, IFocusGroup iFocusGroup) {
        if (moduleData.variantData().effects != null)
            iRecipeLayoutBuilder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
                    .addIngredients(ItemEffectIngredientTypeWithSubtypes.INSTANCE, moduleData.variantData().effects.getValues().stream().toList());
        var data = MaterialDataHelper.INSTANCE.getMaterialData(moduleData.variantData().key);
        if (data != null)
            iRecipeLayoutBuilder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                    .addIngredient(MaterialIngredientTypeWithSubtypes.INSTANCE, MaterialDataHelper.INSTANCE.getMaterialData(moduleData.variantData().key));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, ModuleData recipe, IFocusGroup focuses) {
        var element = new GuiCustomData(0, 25, 175, 175, recipe);
        var widget = new GuiElementRecipeWidget(element, new ScreenPosition(0, 0), 175, 200);
        builder.addWidget(widget);
        builder.addGuiEventListener(widget);
        super.createRecipeExtras(builder, recipe, focuses);
        super.createRecipeExtras(builder, recipe, focuses);
    }

    @Override
    public void draw(ModuleData recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(
                new ResourceLocation("jeitetra", "textures/gui/background2.png"),
                -8, -8, 0,
                0, 0,
                192, 220,
                192, 220
        );

        var glyphData = recipe.variantData().glyph;
        var c = new Color(glyphData.tint);
        var size = recipe.itemModule() instanceof ItemModuleMajor ? 16 : 8;
        guiGraphics.setColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 1);
        guiGraphics.blit(
                glyphData.textureLocation,
                16 - size / 2, 12 - size / 2, 0,
                glyphData.textureX, glyphData.textureY,
                size, size, 256, 256
        );
        guiGraphics.setColor(1, 1, 1, 1);
        var name = ItemModule.getName(recipe.itemModule().getUnlocalizedName(), recipe.variantData().key);
        var color = (recipe.variantData().key.hashCode() & 0x00FFFFFF) | 0xCC000000;
        var font = mc.font;
        guiGraphics.pose().pushPose();

        guiGraphics.pose().translate(-0.1 + (175 - font.getSplitter().stringWidth(name)) / 2, 2, 0);
        guiGraphics.drawString(font, name, 0, 0, color, false);
        guiGraphics.pose().translate(-0.4, -0.4, 0);
        guiGraphics.drawString(font, name, 0, 0, -1, false);

        guiGraphics.pose().popPose();
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }

}
