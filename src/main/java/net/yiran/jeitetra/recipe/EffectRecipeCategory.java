package net.yiran.jeitetra.recipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import net.yiran.jeitetra.effect.ItemEffectIngredientTypeWithSubtypes;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.effect.ItemEffect;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"removal", "all"})
public class EffectRecipeCategory extends AbstractRecipeCategory<ItemEffect> {
    public static RecipeType<ItemEffect> recipeType = RecipeType.create("jeitetra", "effect", ItemEffect.class);
    public Minecraft mc;
    public static Object[] prarm = new String[]{"n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n"};

    public EffectRecipeCategory(IGuiHelper guiHelper) {
        super(recipeType, Component.literal("all.effect"), guiHelper.createDrawableItemLike(ForgeRegistries.ITEMS.getValue(new ResourceLocation("tetra:geode"))), 175, 200);
        mc = Minecraft.getInstance();
    }

    @Override
    public IDrawable getBackground() {
        return null;
        //return Drawables.EFFECT_BACK_GROUND;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, ItemEffect itemEffect, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addIngredient(ItemEffectIngredientTypeWithSubtypes.INSTANCE, itemEffect);
        var items = getItems(itemEffect);
        if(items == null || items.isEmpty()) return;
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,5,1).setBackground(Drawables.SLOT,0,0).addIngredients(VanillaTypes.ITEM_STACK, items);
    }

    public static List<ItemStack> getItems(ItemEffect itemEffect) {
        return DataManager.instance.materialData
                .getData()
                .values()
                .stream()
                .filter(materialData -> materialData.effects.contains(itemEffect))
                .map(materialData -> getItems(materialData.material.getPredicate()))
                .flatMap(List::stream)
                .toList();
    }

    public static List<ItemStack> getItems(ItemPredicate itemPredicate) {
        if (itemPredicate.tag != null) {
            return List.of(Ingredient.of(itemPredicate.tag).getItems());
        } else {
            return itemPredicate.items.stream().map(ItemStack::new).toList();
        }
    }
    @Override
    public void draw(ItemEffect recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        /*
        guiGraphics.blit(
                new ResourceLocation("tetra", "textures/gui/pamphlet.png"),
                -8, -8, 0,
                4, 5,
                192, 220,
                256*192/153, 256*220/179
        );
        */
        guiGraphics.blit(
                new ResourceLocation("jeitetra", "textures/gui/background2.png"),
                -8, -8, 0,
                0, 0,
                192, 220,
                192, 220
        );
        var font = mc.font;
        var effect = recipe.getKey();
        var langKey = "tetra.stats." + effect;
        var drawText = I18n.get(langKey);
        var left = (this.getWidth() - font.width(drawText)) / 2;
        guiGraphics.drawString(font, drawText, left, 0, -1);
        left = (this.getWidth() - font.width(effect)) / 2;
        guiGraphics.drawString(font, "§7" + effect, left, 10, -1);
        if(I18n.exists(langKey = "jeitetra.effect." + effect + ".desc")
                ||I18n.exists(langKey = "tetra.stats." + effect + ".tooltip")
                ||I18n.exists(langKey = "tetra.stats." + effect + ".tooltip_short")
        )
            guiGraphics.drawWordWrap(font,FormattedText.of(I18n.get(langKey, prarm)),6,30,175-12,-1);
        else {
            guiGraphics.drawString(font,"§7Cann't find any description in :" , 6,30,-1,false);
            guiGraphics.drawString(font, "§8jeitetra.effect." + effect + ".desc", 6, 40, -1,false);
            guiGraphics.drawString(font, "§8tetra.stats." + effect + ".tooltip", 6, 50, -1,false);
            guiGraphics.drawString(font, "§8tetra.stats." + effect + ".tooltip_short", 6, 60, -1,false);
        }
    }
}
