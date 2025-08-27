package net.yiran.jeitetra.util;

import com.google.common.collect.Multimap;
import net.minecraft.client.resources.language.I18n;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiString;

import java.util.Map;

public class GuiMultiInfo extends GuiElement {
    public Number value;

    public GuiMultiInfo(int x, String key, Map<String, String> data) {
        super(x, 0, 0, 10);
        var name = I18n.get("tetra.holo.craft.materials.stat." + key);
        addChild(new GuiString(0, 0, name));
        data.forEach((k, v) -> {
            addChild(new GuiString(5, 0, k + " : " + v));
        });
        if (data.isEmpty()) remove();
        setHeight(getChildButton());
    }

    public GuiMultiInfo(int x, String key, Multimap<String, String> data) {
        super(x, 0, 0, 10);
        var name = I18n.get("tetra.holo.craft.materials.stat." + key);
        addChild(new GuiString(0, 0, name));
        data.forEach((k, v) -> {
            addChild(new GuiString(5, 0, k + " : " + v));
        });
        if (data.isEmpty()) remove();
        setHeight(getChildButton());
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

}
