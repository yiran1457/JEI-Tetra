package net.yiran.jeitetra.ingredient;

import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.yiran.jeitetra.util.helper.ImprovementDataHelper;
import org.jetbrains.annotations.Nullable;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.module.data.ImprovementData;

import java.util.List;

@SuppressWarnings({"all", "removal"})
public class ImprovementDataIngredient implements BasicIngredient<ImprovementData> {
    public static ImprovementDataIngredient INSTANCE = new ImprovementDataIngredient();

    @Override
    public String getDisplayName(ImprovementData improvementData) {
        return IModularItem.getImprovementName(improvementData.key, improvementData.level);
    }

    @Override
    public String getUniqueId(ImprovementData improvementData, UidContext uidContext) {
        return improvementData.key + improvementData.level;
    }

    @Override
    public ResourceLocation getResourceLocation(ImprovementData improvementData) {
        return ResourceLocation.parse(improvementData.key);
    }

    @Override
    public ImprovementData copyIngredient(ImprovementData improvementData) {
        return improvementData;
    }

    @Override
    public String getErrorInfo(@Nullable ImprovementData improvementData) {
        return "";
    }

    @Override
    public void render(GuiGraphics guiGraphics, ImprovementData improvementData) {
        var glyph = ImprovementDataHelper.getGlyphData(improvementData);
        if (glyph != null) {
            RenderSystem.enableBlend();
            guiGraphics.setColor(1, 1, 1, 0.6f);
            guiGraphics.blit(
                    glyph.textureLocation, 0, 0,
                    glyph.textureX,
                    glyph.textureY, 16, 16

            );
            guiGraphics.setColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
        }/*
        DataManager.instance.schematicData.getData().values()
                .stream()
                .filter(schematicDefinition -> {
                    if (schematicDefinition.outcomes == null)
                        return false;
                    return Arrays.stream(schematicDefinition.outcomes)
                            .map(outcomeDefinition -> outcomeDefinition.improvements)
                            .anyMatch(stringIntegerMap -> {
                                if (stringIntegerMap.containsKey(improvementData.key))
                                    return stringIntegerMap.get(improvementData.key) == improvementData.level;
                                return false;
                            });
                })
                .findFirst()
                .ifPresent(schematicDefinition -> {
                    RenderSystem.enableBlend();
                    guiGraphics.setColor(1,1,1,0.6f);
                    guiGraphics.blit(
                            schematicDefinition.glyph.textureLocation, 0, 0,
                            schematicDefinition.glyph.textureX,
                            schematicDefinition.glyph.textureY, 16, 16

                    );
                    guiGraphics.setColor(1,1,1,1);
                    RenderSystem.disableBlend();
                });*/


        var pose = guiGraphics.pose();
        pose.pushPose();
        pose.scale(0.7f, 0.7f, 1);
        if (improvementData.level > 0)
            guiGraphics.drawString(
                    Minecraft.getInstance().font, String.valueOf(improvementData.level), 2, 2, 0xffaaaaaa
            );
        pose.scale(0.45f / 0.7f, 0.45f / 0.7f, 1);
        pose.translate(0, 22, 256);
        guiGraphics.drawString(
                Minecraft.getInstance().font,
                Minecraft.getInstance().font.getSplitter().formattedHeadByWidth(IModularItem.getImprovementName(improvementData.key, improvementData.level), 9 * 4, Style.EMPTY),
                0, 0,
                -1, false
        );
        pose.popPose();

    }

    @Override
    public List<Component> getTooltip(ImprovementData improvementData, TooltipFlag tooltipFlag) {
        return List.of(Component.literal(getDisplayName(improvementData)));
    }

    @Override
    public Class<? extends ImprovementData> getIngredientClass() {
        return ImprovementData.class;
    }
}
