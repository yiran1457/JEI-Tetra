package net.yiran.jeitetra.effect;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.GuiTextures;

import java.util.List;

public class ItemEffectIngredientRenderer implements IIngredientRenderer<ItemEffect> {
    public static final ItemEffectIngredientRenderer INSTANCE = new ItemEffectIngredientRenderer();

    @Override
    public void render(GuiGraphics guiGraphics, ItemEffect itemEffect) {
        var manager = Minecraft.getInstance().getResourceManager();
        var effect = itemEffect.getKey();
        var sneakEffect = effect.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        var resource = getResourceLocation(sneakEffect);
        var a = manager.getResource(resource);
        if (a.isPresent()) {
            guiGraphics.blit(resource, 0, 0, 0, 0, 0, 16, 16, 16, 16);
        } else {
            var font = Minecraft.getInstance().font;
            var color = (effect.hashCode() & 0x00FFFFFF) | 0xCC000000;
            String langKey;
            if (I18n.exists(langKey = ItemEffectLangManager.instance.getNameKey(itemEffect))) {
                effect = I18n.get(langKey);
            }
            effect = effect.replaceAll("(§)(.)", "");
            var cha = effect.charAt(0);
            if (Character.isLowerCase(cha)) {
                cha = Character.toUpperCase(cha);
            }
            var z = String.valueOf(cha);
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
        }
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
        return List.of(
                Component.translatable(ItemEffectLangManager.instance.getName(itemEffect)),
                Component.literal(effect)
        );
    }
}
