package net.yiran.jeitetra.ingredient;

import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.yiran.jeitetra.material.MaterialDataHelper;
import org.jetbrains.annotations.Nullable;
import se.mickelus.tetra.module.data.MaterialData;

import java.util.List;

@SuppressWarnings({"all", "removal"})
public class MaterialDataIngredient implements BasicIngredient<MaterialData> {
    public static MaterialDataIngredient INSTANCE = new MaterialDataIngredient();

    @Override
    public String getDisplayName(MaterialData materialData) {
        return MaterialDataHelper.INSTANCE.getMaterialName(materialData);
    }

    @Override
    public String getUniqueId(MaterialData materialData, UidContext uidContext) {
        return materialData.key;
    }

    @Override
    public ResourceLocation getResourceLocation(MaterialData materialData) {
        return new ResourceLocation("tetra", "material");
    }

    @Override
    public MaterialData copyIngredient(MaterialData materialData) {
        return materialData;
    }

    @Override
    public String getErrorInfo(@Nullable MaterialData materialData) {
        if (materialData == null) {
            return "materialData is null";
        }
        return "materialData error with " + materialData.key;
    }

    @Override
    public void render(GuiGraphics guiGraphics, MaterialData materialData) {
        var font = Minecraft.getInstance().font;
        var color = (materialData.key.hashCode() & 0x00FFFFFF) | 0xCC000000;
        String drawString = I18n.get(MaterialDataHelper.INSTANCE.getMaterialKey(materialData));
        drawString = drawString.replaceAll("(§)(.)", "");
        var cha = drawString.charAt(0);
        if (Character.isLowerCase(cha)) {
            cha = Character.toUpperCase(cha);
        }
        var z = String.valueOf(cha);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.5, 0.5, 0);
        /*
        guiGraphics.blit(
                GuiTextures.workbench,
                0, 0, 0,
                52, 0,
                15, 15,
                256, 256
        );*/

        guiGraphics.pose().translate(-0.1 - font.getSplitter().stringWidth(z) / 2, -0.1, 0);
        guiGraphics.drawString(font, z, 8, 4, color, false);
        guiGraphics.pose().translate(-0.4, -0.4, 0);
        guiGraphics.drawString(font, z, 8, 4, -1, false);

        guiGraphics.pose().popPose();

    }

    @Override
    public List<Component> getTooltip(MaterialData materialData, TooltipFlag tooltipFlag) {
        return List.of(
                Component.translatable(MaterialDataHelper.INSTANCE.getMaterialKey(materialData)),
                Component.translatable(MaterialDataHelper.INSTANCE.getMaterialCategoryKey(materialData))
        );
    }

    @Override
    public Class<? extends MaterialData> getIngredientClass() {
        return MaterialData.class;
    }
}
