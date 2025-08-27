package net.yiran.jeitetra.util;

import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.common.Internal;
import mezz.jei.library.focus.Focus;
import mezz.jei.library.ingredients.TypedIngredient;
import net.yiran.jeitetra.effect.ItemEffectIngredientTypeWithSubtypes;
import net.yiran.jeitetra.effect.ItemEffectLangManager;
import se.mickelus.mutil.gui.GuiClickable;
import se.mickelus.mutil.gui.GuiString;
import se.mickelus.tetra.effect.ItemEffect;

public class GuiEffectRecipe extends GuiClickable {
    public ItemEffect effect;
    public GuiString guiString;

    public GuiEffectRecipe(int x, int y, ItemEffect effect, float level, float eff) {
        super(x, y, 0, 8, () -> openRecipe(effect));
        addChild(guiString = new GuiString(0, 0, ItemEffectLangManager.instance.getName(effect) + " : [ " + level + " , " + eff + " ]"));
        setWidth(guiString.getWidth());
    }

    @Override
    protected void onBlur() {
        this.guiString.setColor(16777215);
    }

    @Override
    protected void onFocus() {
        this.guiString.setColor(16777164);
    }

    public static void openRecipe(ItemEffect effect) {
        Internal.getJeiRuntime().getRecipesGui().show(
                new Focus<>(RecipeIngredientRole.OUTPUT, TypedIngredient.createUnvalidated(ItemEffectIngredientTypeWithSubtypes.INSTANCE, effect))
        );
    }
}
