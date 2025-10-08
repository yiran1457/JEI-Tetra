package net.yiran.jeitetra.util;

import net.minecraft.client.resources.language.I18n;

public class I18nWrapper {
    public static Object[] DefaultParameters = new String[]{"n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n"};

    public static String getWithDefParam(String translateKey) {
        return get(translateKey, DefaultParameters);
    }

    public static String get(String translateKey) {
        return post(I18n.get(translateKey));
    }

    public static String get(String translateKey, Object... parameters) {
        return post(I18n.get(translateKey, parameters));
    }

    public static String post(String text) {
        return text.replace("\\n", "\n");
    }

    public static boolean exists(String translateKey) {
        return I18n.exists(translateKey);
    }
}
