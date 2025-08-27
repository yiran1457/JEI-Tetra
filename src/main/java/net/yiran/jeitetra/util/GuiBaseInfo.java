package net.yiran.jeitetra.util;

import net.minecraft.client.resources.language.I18n;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiString;

public class GuiBaseInfo extends GuiElement {
    public GuiString guiString;
    public Number value;

    public GuiBaseInfo(int x ,String key, Number value) {
        super(x, 0, 0, 10);
        var name = I18n.get("tetra.holo.craft.materials.stat." + key);
        addChild(guiString = new GuiString(0, 0, name + " : " + value ));
        if(value.doubleValue() == 0)remove();
    }

}
