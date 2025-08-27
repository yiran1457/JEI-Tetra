package net.yiran.jeitetra.effect;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Unit;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import se.mickelus.tetra.effect.ItemEffect;

import java.util.HashMap;
import java.util.Map;

public class ItemEffectLangManager {
    public static ItemEffectLangManager instance = new ItemEffectLangManager();
    public Map<ItemEffect, String> nameMap = new HashMap<>(100);
    public Map<ItemEffect, String> descMap = new HashMap<>(100);

    public void onReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((
                stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor
        ) -> stage.wait(Unit.INSTANCE).thenRunAsync(() -> {
            reloadProfiler.startTick();
            reloadProfiler.push("listener");
            clearCache();
            reloadProfiler.pop();
            reloadProfiler.endTick();
        }, gameExecutor));
    }

    public void clearCache() {
        nameMap.clear();
        descMap.clear();
    }

    public String getName(ItemEffect itemEffect) {
        return I18n.get(getNameKey(itemEffect));
    }

    public String getNameKey(ItemEffect effect) {
        if (nameMap.containsKey(effect)) {
            return nameMap.get(effect);
        }
        var name = ItemEffectLangHelper.getDisplayKey(effect);
        name = getEndLang(name);
        nameMap.put(effect, name);
        return name;
    }

    public String getDescKey(ItemEffect effect) {
        if (descMap.containsKey(effect)) {
            return descMap.get(effect);
        }
        var name = ItemEffectLangHelper.getDescKey(effect);
        name = getEndLang(name);
        descMap.put(effect, name);
        return name;
    }

    public String getEndLang(String key) {
        if (!I18n.exists(key)) return key;
        String lang = I18n.get(key);
        if (lang.startsWith("goto:")) {
            return getEndLang(lang.substring(5));
        }
        return key;
    }
}
