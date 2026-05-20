package net.yiran.jeitetra.ingredient;

import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.yiran.jeitetra.effect.ItemEffectLangManager;
import net.yiran.jeitetra.util.I18nWrapper;
import org.jetbrains.annotations.Nullable;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.GuiTextures;

import java.util.List;

@SuppressWarnings({"all", "removal"})
public class ItemEffectIngredient implements BasicIngredient<ItemEffect> {
    public static ItemEffectIngredient INSTANCE = new ItemEffectIngredient();

    @Override
    public String getDisplayName(ItemEffect itemEffect) {
        return ItemEffectLangManager.instance.getName(itemEffect);
    }

    @Override
    public String getUniqueId(ItemEffect itemEffect, UidContext uidContext) {
        return itemEffect.getKey();
    }

    @Override
    public ResourceLocation getResourceLocation(ItemEffect itemEffect) {
        var name = itemEffect.getKey()
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
        if (name.contains(":")) {
            return new ResourceLocation(name);
        }
        return new ResourceLocation("tetra", name);
    }

    @Override
    public ItemEffect copyIngredient(ItemEffect itemEffect) {
        return itemEffect;
    }

    @Override
    public String getErrorInfo(@Nullable ItemEffect itemEffect) {
        if (itemEffect == null) {
            return "itemEffect is null";
        }
        return "itemEffect error with " + itemEffect.getKey();
    }

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
            if (I18nWrapper.exists(langKey = ItemEffectLangManager.instance.getNameKey(itemEffect))) {
                effect = I18nWrapper.get(langKey);
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
    public List<Component> getTooltip(ItemEffect itemEffect, TooltipFlag tooltipFlag) {
        var effect = itemEffect.getKey();
        return List.of(
                Component.translatable(ItemEffectLangManager.instance.getNameKey(itemEffect)),
                Component.literal(effect)
        );
    }

    @Override
    public Class<? extends ItemEffect> getIngredientClass() {
        return ItemEffect.class;
    }
}
