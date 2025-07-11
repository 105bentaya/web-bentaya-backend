package org.scouts105bentaya.shared.util;

import org.thymeleaf.context.Context;

public class TemplateUtils {

    private TemplateUtils() {
    }

    public static Context getContext(Object... values) {
        if (values.length % 2 != 0) throw new IllegalArgumentException("Total values number must be even");
        Context context = new Context();
        for (int i = 0; i < values.length; i += 2) {
            context.setVariable(values[i].toString(), values[i + 1]);
        }
        return context;
    }

    public static Context contextOf(String key, Object value) {
        return getContext(key, value);
    }
}
