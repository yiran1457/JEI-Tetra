package net.yiran.jeitetra.recipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.yiran.jeitetra.effect.ItemEffectLangManager;
import net.yiran.jeitetra.ingredient.ItemEffectIngredient;
import net.yiran.jeitetra.ingredient.renderer.ItemEffectRecipeIngredientRenderer;
import net.yiran.jeitetra.util.Drawables;
import net.yiran.jeitetra.util.I18nWrapper;
import net.yiran.jeitetra.util.ItemUtil;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.effect.ItemEffect;

import java.util.List;
import java.util.Objects;

@SuppressWarnings({"removal", "all"})
public class EffectRecipeCategory extends AbstractRecipeCategory<ItemEffect> {
    public static RecipeType<ItemEffect> recipeType = RecipeType.create("jeitetra", "effect", ItemEffect.class);
    public Minecraft mc;

    public EffectRecipeCategory(IGuiHelper guiHelper) {
        super(recipeType, Component.translatable("jeitetra.alleffect.title"),
                guiHelper.createDrawableItemLike(ForgeRegistries.ITEMS.getValue(new ResourceLocation("tetra:pristine_quartz"))), 175, 200);
        mc = Minecraft.getInstance();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, ItemEffect itemEffect, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, (175 - 55) / 2, -1)
                .setCustomRenderer(ItemEffectIngredient.INSTANCE, ItemEffectRecipeIngredientRenderer.INSTANCE)
                .addIngredient(ItemEffectIngredient.INSTANCE, itemEffect);
        var items = getItems(itemEffect);
        if (items == null || items.isEmpty()) return;
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 5, 1).setBackground(Drawables.SLOT, 0, 0).addIngredients(VanillaTypes.ITEM_STACK, items);
    }

    public static List<ItemStack> getItems(ItemEffect itemEffect) {
        return DataManager.instance.materialData
                .getData()
                .values()
                .stream()
                .filter(materialData -> materialData.effects.contains(itemEffect))
                .map(materialData -> materialData.material.getPredicate())
                .filter(Objects::nonNull)
                .map(ItemUtil::getItemsFromItemPredicate)
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public void draw(ItemEffect recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(
                new ResourceLocation("jeitetra", "textures/gui/background2.png"),
                -8, -8, 0,
                0, 0,
                192, 220,
                192, 220
        );
        var font = mc.font;

        var effect = recipe.getKey();
        var left = (this.getWidth() - font.width(effect)) / 2;
        guiGraphics.drawString(font, "§7" + effect, left, 10, -1);

        if (I18nWrapper.exists(ItemEffectLangManager.instance.getDescKey(recipe))) {
            guiGraphics.drawWordWrap(font, FormattedText.of(I18nWrapper.getWithDefParam(ItemEffectLangManager.instance.getDescKey(recipe))), 6, 30, 175 - 12, -1);
        } else {
            guiGraphics.drawString(font, "§7Cann't find any description in :", 6, 30, -1, false);
            guiGraphics.drawString(font, "§8jeitetra.effect." + effect + ".desc", 6, 40, -1, false);
            guiGraphics.drawString(font, "§8tetra.stats." + effect + ".tooltip", 6, 50, -1, false);
            guiGraphics.drawString(font, "§8tetra.stats." + effect + ".tooltip_short", 6, 60, -1, false);
        }
    }
}
