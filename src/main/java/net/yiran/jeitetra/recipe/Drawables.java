package net.yiran.jeitetra.recipe;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.common.gui.elements.DrawableResource;
import net.minecraft.resources.ResourceLocation;

public class Drawables {
    public static IDrawable SLOT = new DrawableResource(
            new ResourceLocation("jeitetra", "textures/gui/background-export.png"),
            0, 0,
            24, 24,
            -4, 0, -4, 0,
            24, 24
    );
    public static IDrawable REPLACE_BACK_GROUND = new DrawableResource(
            new ResourceLocation("jeitetra", "textures/gui/background1.png"),
            0, 0,
            164, 60,
            -6, 0, -6, 0,
            164, 60
    );
}
