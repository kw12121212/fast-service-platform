package com.fastservice.platform.backend.common.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SimpleJson {

    private SimpleJson() {
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String json) {
        Object value = parse(json);
        if (!(value instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("JSON value is not an object");
        }
        return (Map<String, Object>) map;
    }

    public static Object parse(String json) {
        return new Parser(json).parse();
    }

    public static String stringify(Object value) {
        StringBuilder builder = new StringBuilder();
        writeValue(builder, value);
        return builder.toString();
    }

    private static void writeValue(StringBuilder builder, Object value) {
        if (value == null) {
            builder.append("null");
            return;
        }
        if (value instanceof String stringValue) {
            builder.append('"');
            for (int i = 0; i < stringValue.length(); i++) {
                char current = stringValue.charAt(i);
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
            return;
        }
        if (value instanceof Number || value instanceof Boolean) {
            builder.append(value);
            return;
        }
        if (value instanceof Map<?, ?> mapValue) {
            builder.append('{');
            boolean first = true;
            for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
                if (!first) {
                    builder.append(',');
                }
                first = false;
                writeValue(builder, String.valueOf(entry.getKey()));
                builder.append(':');
                writeValue(builder, entry.getValue());
            }
            builder.append('}');
            return;
        }
        if (value instanceof Iterable<?> iterable) {
            builder.append('[');
            boolean first = true;
            for (Object entry : iterable) {
                if (!first) {
                    builder.append(',');
                }
                first = false;
                writeValue(builder, entry);
            }
            builder.append(']');
            return;
        }
        throw new IllegalArgumentException("Unsupported JSON value type: " + value.getClass().getName());
    }

    private static final class Parser {
        private final String input;
        private int index;

        private Parser(String input) {
            this.input = input;
        }

        private Object parse() {
            skipWhitespace();
            Object value = parseValue();
            skipWhitespace();
            if (index != input.length()) {
                throw error("Unexpected trailing content");
            }
            return value;
        }

        private Object parseValue() {
            skipWhitespace();
            if (index >= input.length()) {
                throw error("Unexpected end of input");
            }
            char current = input.charAt(index);
            return switch (current) {
                case '{' -> parseObject();
                case '[' -> parseArray();
                case '"' -> parseString();
                case 't' -> parseLiteral("true", Boolean.TRUE);
                case 'f' -> parseLiteral("false", Boolean.FALSE);
                case 'n' -> parseLiteral("null", null);
                default -> {
                    if (current == '-' || Character.isDigit(current)) {
                        yield parseNumber();
                    }
                    throw error("Unexpected token: " + current);
                }
            };
        }

        private Map<String, Object> parseObject() {
            expect('{');
            skipWhitespace();
            Map<String, Object> values = new LinkedHashMap<>();
            if (peek('}')) {
                index++;
                return values;
            }
            while (true) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                expect(':');
                Object value = parseValue();
                values.put(key, value);
                skipWhitespace();
                if (peek('}')) {
                    index++;
                    return values;
                }
                expect(',');
            }
        }

        private List<Object> parseArray() {
            expect('[');
            skipWhitespace();
            List<Object> values = new ArrayList<>();
            if (peek(']')) {
                index++;
                return values;
            }
            while (true) {
                values.add(parseValue());
                skipWhitespace();
                if (peek(']')) {
                    index++;
                    return values;
                }
                expect(',');
            }
        }

        private String parseString() {
            expect('"');
            StringBuilder builder = new StringBuilder();
            while (index < input.length()) {
                char current = input.charAt(index++);
                if (current == '"') {
                    return builder.toString();
                }
                if (current != '\\') {
                    builder.append(current);
                    continue;
                }
                if (index >= input.length()) {
                    throw error("Unexpected end of input in string escape");
                }
                char escaped = input.charAt(index++);
                switch (escaped) {
                    case '"', '\\', '/' -> builder.append(escaped);
                    case 'b' -> builder.append('\b');
                    case 'f' -> builder.append('\f');
                    case 'n' -> builder.append('\n');
                    case 'r' -> builder.append('\r');
                    case 't' -> builder.append('\t');
                    case 'u' -> builder.append(parseUnicode());
                    default -> throw error("Invalid string escape: \\" + escaped);
                }
            }
            throw error("Unterminated string");
        }

        private char parseUnicode() {
            if (index + 4 > input.length()) {
                throw error("Incomplete unicode escape");
            }
            String hex = input.substring(index, index + 4);
            index += 4;
            try {
                return (char) Integer.parseInt(hex, 16);
            } catch (NumberFormatException ignored) {
                throw error("Invalid unicode escape: " + hex);
            }
        }

        private Number parseNumber() {
            int start = index;
            if (input.charAt(index) == '-') {
                index++;
            }
            readDigits();
            if (peek('.')) {
                index++;
                readDigits();
            }
            if (peek('e') || peek('E')) {
                index++;
                if (peek('+') || peek('-')) {
                    index++;
                }
                readDigits();
            }
            String token = input.substring(start, index);
            if (token.contains(".") || token.contains("e") || token.contains("E")) {
                return Double.parseDouble(token);
            }
            return Long.parseLong(token);
        }

        private void readDigits() {
            if (index >= input.length() || !Character.isDigit(input.charAt(index))) {
                throw error("Expected digit");
            }
            while (index < input.length() && Character.isDigit(input.charAt(index))) {
                index++;
            }
        }

        private Object parseLiteral(String literal, Object value) {
            if (!input.startsWith(literal, index)) {
                throw error("Expected literal: " + literal);
            }
            index += literal.length();
            return value;
        }

        private void skipWhitespace() {
            while (index < input.length() && Character.isWhitespace(input.charAt(index))) {
                index++;
            }
        }

        private boolean peek(char expected) {
            return index < input.length() && input.charAt(index) == expected;
        }

        private void expect(char expected) {
            if (!peek(expected)) {
                throw error("Expected '" + expected + "'");
            }
            index++;
        }

        private IllegalArgumentException error(String message) {
            return new IllegalArgumentException(message + " at index " + index);
        }
    }
}
