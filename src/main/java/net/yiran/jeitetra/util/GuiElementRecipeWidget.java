package net.yiran.jeitetra.util;

import mezz.jei.api.gui.inputs.IJeiGuiEventListener;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import se.mickelus.mutil.gui.GuiElement;

public class GuiElementRecipeWidget implements IRecipeWidget, IJeiGuiEventListener {
    public GuiElement element;
    public ScreenPosition position;
    public ScreenRectangle rect;

    public GuiElementRecipeWidget(GuiElement element, ScreenPosition position, int width, int height) {
        this.element = element;
        this.position = position;
        rect = new ScreenRectangle(position, width, height);
    }

    @Override
    @SuppressWarnings("removal")
    public void draw(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        element.updateFocusState(0, 0, (int) mouseX, (int) mouseY);
        element.draw(guiGraphics, 0, 0, rect.width(), rect.height(), (int) mouseX, (int) mouseY, 1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return element.onMouseClick((int) mouseX, (int) mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        element.onMouseRelease((int) mouseX, (int) mouseY, button);
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
        return element.onMouseScroll((int) mouseX, (int) mouseY, scrollDelta);
    }

    @Override
    public ScreenPosition getPosition() {
        return position;
    }

    @Override
    public ScreenRectangle getArea() {
        return rect;
    }
}
