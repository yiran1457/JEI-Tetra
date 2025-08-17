package net.yiran.jeitetra.effect;

import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.library.color.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.GuiTextures;

import java.awt.*;
import java.util.List;

public class ItemEffectIngredientRenderer implements IIngredientRenderer<ItemEffect> {
    public static final ItemEffectIngredientRenderer INSTANCE = new ItemEffectIngredientRenderer();

    @Override
    public void render(GuiGraphics guiGraphics, ItemEffect itemEffect) {
        var manager = Minecraft.getInstance().getResourceManager();
        var effect = itemEffect.getKey();
        var resource = getResourceLocation(effect
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase());
        var a = manager.getResource(resource);
        var b = a.isPresent();
        if (a.isPresent()) {
            guiGraphics.blit(resource, 0, 0, 0, 0, 0, 16, 16, 16, 16);
        } else {
            var font = Minecraft.getInstance().font;
            var color = (effect.hashCode() & 0x00FFFFFF) | 0xCC000000;
            var langKey = "tetra.stats." + effect;
            if (I18n.exists(langKey)) {
                effect = I18n.get(langKey);
            }
            var cha = effect.charAt(0);
            if (Character.isLowerCase(cha)) {
                cha = Character.toUpperCase(cha);
            }
            var z = String.valueOf(cha);
            var s = 23;
            /*
            guiGraphics.pose().translate(0.5,0.5,0);
            guiGraphics.blit(
                    new ResourceLocation("jeitetra", "textures/gui/background-export.png"),
                    -4,-4,0,
                    0,0,
                    s,s,
                    s,s
            );
            guiGraphics.pose().translate(-0.5,-0.5,0);
            /*
            guiGraphics.blit(
                    GuiTextures.workbench,
                    0,0,0,
                    52,16,
                    16,16,
                    256,256
            );*/
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.5, 0.5, 0);
            guiGraphics.blit(
                    GuiTextures.workbench,
                    0, 0, 0,
                    52, 0,
                    15, 15,
                    256, 256
            );

            guiGraphics.pose().translate(-0.1 - font.getSplitter().stringWidth(z) / 2, -0.1, 0);
            guiGraphics.drawString(font, z, 8, 4, color, false);
            guiGraphics.pose().translate(-0.4, -0.4, 0);
            guiGraphics.drawString(font, z, 8, 4, -1, false);


            guiGraphics.pose().popPose();

            //guiGraphics.drawString( font,z,8-  font.width(z)/2, 4, color,false);

            /*
            var drawText = font.split(FormattedText.of(effect), 16);
            if (drawText.size() == 1) {
                guiGraphics.drawString(Minecraft.getInstance().font, drawText.get(0), 0, 4, color);
            } else {
                for (int i = 0; i < Math.min(2, drawText.size()); i++) {
                    guiGraphics.drawString(Minecraft.getInstance().font, drawText.get(i), 0, i * 8, color);

                }
            }*/
        }
        //guiGraphics.drawString(Minecraft.getInstance().font, effect,0,4,-1);
    }

    public ResourceLocation getResourceLocation(String name) {
        if (name.contains(":")) {
            return new ResourceLocation("jeitetra", "textures/effects/" + name.replace(":", "/") + ".png");
        }
        return new ResourceLocation("jeitetra", "textures/effects/tetra/" + name + ".png");
    }

    @Override
    @SuppressWarnings("removal")
    public List<Component> getTooltip(ItemEffect itemEffect, TooltipFlag tooltipFlag) {
        var effect = itemEffect.getKey();
        return List.of(Component.translatable("tetra.stats." + effect), Component.literal(effect));
    }
}
