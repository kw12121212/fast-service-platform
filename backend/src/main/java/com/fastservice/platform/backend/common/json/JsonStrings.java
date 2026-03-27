package com.fastservice.platform.backend.common.json;

public final class JsonStrings {

    private JsonStrings() {
    }

    public static String quote(String value) {
        if (value == null) {
            return "null";
        }
        StringBuilder builder = new StringBuilder(value.length() + 2);
        builder.append('"');
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            switch (current) {
            case '"' -> builder.append("\\\"");
            case '\\' -> builder.append("\\\\");
            case '\b' -> builder.append("\\b");
            case '\f' -> builder.append("\\f");
            case '\n' -> builder.append("\\n");
            case '\r' -> builder.append("\\r");
            case '\t' -> builder.append("\\t");
            default -> {
                if (current < 0x20) {
                    builder.append(String.format("\\u%04x", (int) current));
                } else {
                    builder.append(current);
                }
            }
            }
        }
        builder.append('"');
        return builder.toString();
    }
}
