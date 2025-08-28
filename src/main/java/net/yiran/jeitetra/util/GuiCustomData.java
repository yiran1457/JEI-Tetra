package net.yiran.jeitetra.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.joml.Vector4f;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiString;
import se.mickelus.tetra.module.data.MaterialData;

import java.util.stream.Collectors;

public class GuiCustomData extends GuiElement {
    public int offsetY = 0;

    public GuiCustomData(int x, int y, int width, int height, MaterialData materialData) {
        super(x, y, width, height);
        addChild(new GuiBaseInfo(5, "durability", materialData.durability));
        addChild(new GuiBaseInfo(5, "primary", materialData.primary));
        addChild(new GuiBaseInfo(5, "secondary", materialData.secondary));
        addChild(new GuiBaseInfo(5, "tertiary", materialData.tertiary));

        addChild(new GuiBaseInfo(5, "toolLevel", materialData.toolLevel));
        addChild(new GuiBaseInfo(5, "toolEfficiency", materialData.toolEfficiency));
        addChild(new GuiBaseInfo(5, "experienceCost", materialData.experienceCost));
        addChild(new GuiBaseInfo(5, "integrityGain", materialData.integrityGain));
        addChild(new GuiBaseInfo(5, "integrityCost", materialData.integrityCost));

        if (materialData.requiredTools != null) {
            addChild(new GuiMultiInfo(5, "requiredTools", materialData.requiredTools.levelMap
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> I18n.get("tetra.tool." + e.getKey().name()),
                            e -> e.getValue().toString()
                    ))
            ));
        }

        if (materialData.attributes != null) {
            Multimap<String, String> multimap = HashMultimap.create();
            materialData.attributes
                    .entries().stream()
                    .forEach(e -> multimap.put(I18n.get(e.getKey().getDescriptionId()), getAttributeModifierValue(e.getValue())));
            addChild(new GuiMultiInfo(5, "attributes", multimap
            ));
        }

        var effects = materialData.effects;
        var effectSet = effects.getValues();
        if (!effectSet.isEmpty()) {
            addChild(new GuiString(5, 0, I18n.get("tetra.holo.craft.materials.stat.effects")));
            effectSet.forEach(itemEffect -> {
                addChild(
                        new GuiEffectRecipe(
                                10, getNumChildren() * 10,
                                itemEffect,
                                effects.getLevel(itemEffect), effects.getEfficiency(itemEffect)
                        )
                );
            });
        }

        if (!materialData.aspects.levelMap.isEmpty()) {
            addChild(new GuiMultiInfo(5, "aspects", materialData.aspects.levelMap
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> I18n.get("tetra.aspect." + e.getKey().getKey()),
                            e -> String.valueOf(e.getValue().intValue())
                    ))
            ));
        }

        if (!materialData.improvements.isEmpty()) {
            addChild(new GuiMultiInfo(5, "improvements", materialData.improvements
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> I18n.get("tetra.improvement." + e.getKey() + ".name"),
                            e -> e.getValue().toString()
                    ))
            ));
        }

    }

    public GuiCustomData(int x, int y, int width, int height, ModuleData materialData) {
        super(x, y, width, height);
        var variantData = materialData.variantData();
        addChild(new GuiBaseInfo(5, "durability", variantData.durability));
        addChild(new GuiBaseInfo(5, "magicCapacity", variantData.magicCapacity));
        addChild(new GuiBaseInfo(5, "integrity", variantData.integrity));

        if (variantData.tools != null) {
            addChild(new GuiMultiInfo(5, "hasTools", variantData.tools.getValues()
                    .stream()
                    .collect(Collectors.toMap(
                            t -> I18n.get("tetra.tool." + t.name()),
                            t -> " [ " + variantData.tools.getLevel(t) + " , " + variantData.tools.getEfficiency(t) + " ]"
                    ))
            ));
        }

        if (variantData.attributes != null) {
            Multimap<String, String> multimap = HashMultimap.create();
            variantData.attributes
                    .entries().stream()
                    .forEach(e -> multimap.put(I18n.get(e.getKey().getDescriptionId()), getAttributeModifierValue(e.getValue())));
            addChild(new GuiMultiInfo(5, "attributesT", multimap
            ));
        }

        var effects = variantData.effects;
        if(effects != null) {
            var effectSet = effects.getValues();
            if (!effectSet.isEmpty()) {
                addChild(new GuiString(5, 0, I18n.get("tetra.holo.craft.materials.stat.effectsT")));
                effectSet.forEach(itemEffect -> {
                    addChild(
                            new GuiEffectRecipe(
                                    10, getNumChildren() * 10,
                                    itemEffect,
                                    effects.getLevel(itemEffect), effects.getEfficiency(itemEffect)
                            )
                    );
                });
            }
        }

        if (variantData.aspects != null&&!variantData.aspects.levelMap.isEmpty()) {
            addChild(new GuiMultiInfo(5, "aspectsT", variantData.aspects.levelMap
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> I18n.get("tetra.aspect." + e.getKey().getKey()),
                            e -> String.valueOf(e.getValue().intValue())
                    ))
            ));
        }

    }

    public String getAttributeModifierValue(AttributeModifier modifier) {
        String color = modifier.getAmount() > 0 ? "§a" : "§4";
        if (modifier.getOperation().toValue() == 0) {
            return color + modifier.getAmount();
        } else {
            return color + modifier.getAmount() * 100 + "%";
        }
    }

    public int getChildButton() {
        if (getNumChildren() != 0) {
            var lastChild = getChild(getNumChildren() - 1);
            return lastChild.getHeight() + lastChild.getY();
        }
        return 0;
    }

    @Override
    public void addChild(GuiElement child) {
        if (child.shouldRemove()) return;
        child.setY(getChildButton());
        super.addChild(child);
    }

    @Override
    public void draw(GuiGraphics graphics, int refX, int refY, int screenWidth, int screenHeight, int mouseX, int mouseY, float opacity) {
        var pos = graphics.pose().last().pose().transform(new Vector4f(0, 0, 0, 1));
        graphics.enableScissor((int) pos.x + x, (int) pos.y + y, (int) pos.x + x + width, (int) pos.y + y + height);

        super.draw(graphics, refX, refY + Mth.clamp(offsetY, -getChildButton(), 0), screenWidth, screenHeight, mouseX, mouseY, opacity);
        graphics.disableScissor();
    }

    @Override
    public void updateFocusState(int refX, int refY, int mouseX, int mouseY) {
        if (mouseOutScreen(refX + mouseX, refY + mouseY)) {
            super.updateFocusState(-9999, -9999, -9999, -9999);
            return;
        }
        super.updateFocusState(refX, refY + offsetY, mouseX, mouseY);
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double distance) {
        if (mouseOutScreen((int) mouseX, (int) mouseY)) return false;
        if (offsetY + distance * 3 < -getChildButton() + height || offsetY + distance * 3 > 0)
            return false;
        offsetY += (int) distance * 6;
        return true;
    }

    public boolean mouseOutScreen(int mouseX, int mouseY) {
        return mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height;
    }
}
