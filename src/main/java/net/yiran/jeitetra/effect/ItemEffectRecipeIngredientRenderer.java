package net.yiran.jeitetra.effect;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import se.mickelus.tetra.effect.ItemEffect;

import java.util.List;

public class ItemEffectRecipeIngredientRenderer implements IIngredientRenderer<ItemEffect> {
    public static final ItemEffectRecipeIngredientRenderer INSTANCE = new ItemEffectRecipeIngredientRenderer();


    @Override
    public int getHeight() {
        return 12;
    }

    @Override
    public int getWidth() {
        return 55;
    }

    @Override
    public void render(GuiGraphics guiGraphics, ItemEffect itemEffect) {
        var font = Minecraft.getInstance().font;
        var effect = itemEffect.getKey();
        var color = (effect.hashCode() & 0x00FFFFFF) | 0xCC000000;
        String langKey;
        if (I18n.exists(langKey = ItemEffectLangManager.instance.getNameKey(itemEffect))) {
            effect = I18n.get(langKey);
        }
        guiGraphics.pose().pushPose();

        guiGraphics.pose().translate(-0.1 + (55 - font.getSplitter().stringWidth(effect)) / 2, 2, 0);
        guiGraphics.drawString(font, effect, 0, 0, color, false);
        guiGraphics.pose().translate(-0.4, -0.4, 0);
        guiGraphics.drawString(font, effect, 0, 0, -1, false);

        guiGraphics.pose().popPose();

    }

    @Override
    @SuppressWarnings("removal")
    public List<Component> getTooltip(ItemEffect itemEffect, TooltipFlag tooltipFlag) {
        var effect = itemEffect.getKey();
        return List.of(
                Component.translatable(ItemEffectLangManager.instance.getName(itemEffect)),
                Component.literal(effect)
        );
    }
}
