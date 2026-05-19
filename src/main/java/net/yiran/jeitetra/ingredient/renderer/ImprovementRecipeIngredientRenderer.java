package net.yiran.jeitetra.ingredient.renderer;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.module.data.ImprovementData;

import java.util.List;

public class ImprovementRecipeIngredientRenderer implements IIngredientRenderer<ImprovementData> {
    public static ImprovementRecipeIngredientRenderer INSTANCE = new ImprovementRecipeIngredientRenderer();

    @Override
    public int getHeight() {
        return 12;
    }

    @Override
    public int getWidth() {
        return 55;
    }

    @Override
    public void render(GuiGraphics guiGraphics, ImprovementData improvementData) {
        var font = Minecraft.getInstance().font;
        var color = (improvementData.key.hashCode() & 0x00FFFFFF) | 0xCC000000;
        String drawString = I18n.get(IModularItem.getImprovementName(improvementData.key, improvementData.level));
        var z = drawString;
        guiGraphics.pose().pushPose();

        guiGraphics.pose().translate(-0.1 + (55 - font.getSplitter().stringWidth(z)) / 2, 2, 0);
        guiGraphics.drawString(font, z, 0, 0, color, false);
        guiGraphics.pose().translate(-0.4, -0.4, 0);
        guiGraphics.drawString(font, z, 0, 0, -1, false);

        guiGraphics.pose().popPose();

    }

    @Override
    @SuppressWarnings("removal")
    public List<Component> getTooltip(ImprovementData improvementData, TooltipFlag tooltipFlag) {
        return List.of(
                Component.literal(IModularItem.getImprovementName(improvementData.key, improvementData.level)),
                Component.literal(improvementData.key)
        );
    }

}
