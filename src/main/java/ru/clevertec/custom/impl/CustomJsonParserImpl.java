package ru.clevertec.custom.impl;

import ru.clevertec.custom.CustomJsonParser;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static ru.clevertec.custom.impl.JsonParserConstant.*;

public final class CustomJsonParserImpl implements CustomJsonParser {

    @Override
    public String parseToJson(Object[] objects) throws IllegalAccessException {
        if (objects == null) {
            return NULL;
        }
        StringBuilder builder = new StringBuilder();
        String className = objects.getClass().getSimpleName();
        if (OBJECT_ARRAY.equals(className)) {
            appendObjectArray(builder, objects);
        } else {
            appendReferenceArray(builder, objects);
        }
        return builder.toString();
    }

    @Override
    public String parseToJson(Object object) throws IllegalAccessException {
        StringBuilder builder = new StringBuilder();
        if (object == null) {
            return NULL;
        }
        if (Optional.class.isAssignableFrom(object.getClass())) {
            appendOptional(builder, object);
            return builder.toString();
        }
        if (Collection.class.isAssignableFrom(object.getClass())) {
            appendCollection(builder, object);
            return builder.toString();
        }
        if (Map.class.isAssignableFrom(object.getClass())) {
            appendMap(builder, object);
            return builder.toString();
        }
        String className = object.getClass().getSimpleName();
        switch (className) {
            case STRING, CHARACTER, CHARACTER_PRIMITIVE -> {
                builder.append(QUOTE).append(object).append(QUOTE);
                return builder.toString();
            }
            case BYTE, BYTE_PRIMITIVE, SHORT, SHORT_PRIMITIVE,
                    INTEGER, INTEGER_PRIMITIVE, LONG, LONG_PRIMITIVE,
                    FLOAT, FLOAT_PRIMITIVE, DOUBLE, DOUBLE_PRIMITIVE,
                    BOOLEAN, BOOLEAN_PRIMITIVE,
                    BIG_DECIMAL, BIG_INTEGER -> {
                builder.append(object.toString());
                return builder.toString();
            }
            case STRING_ARRAY -> {
                appendStringArray(builder, object);
                return builder.toString();
            }
            case CHARACTER_ARRAY -> {
                appendCharacterArray(builder, object);
                return builder.toString();
            }
            case CHARACTER_PRIMITIVE_ARRAY -> {
                appendCharacterPrimitiveArray(builder, object);
                return builder.toString();
            }
            case BYTE_ARRAY -> {
                appendByteArray(builder, object);
                return builder.toString();
            }
            case BYTE_PRIMITIVE_ARRAY -> {
                appendBytePrimitiveArray(builder, object);
                return builder.toString();
            }
            case SHORT_ARRAY -> {
                appendShortArray(builder, object);
                return builder.toString();
            }
            case SHORT_PRIMITIVE_ARRAY -> {
                appendShortPrimitiveArray(builder, object);
                return builder.toString();
            }
            case INTEGER_ARRAY -> {
                appendIntegerArray(builder, object);
                return builder.toString();
            }
            case INTEGER_PRIMITIVE_ARRAY -> {
                appendIntegerPrimitiveArray(builder, object);
                return builder.toString();
            }
            case LONG_ARRAY -> {
                appendLongArray(builder, object);
                return builder.toString();
            }
            case LONG_PRIMITIVE_ARRAY -> {
                appendLongPrimitiveArray(builder, object);
                return builder.toString();
            }
            case FLOAT_ARRAY -> {
                appendFloatArray(builder, object);
                return builder.toString();
            }
            case FLOAT_PRIMITIVE_ARRAY -> {
                appendFloatPrimitiveArray(builder, object);
                return builder.toString();
            }
            case DOUBLE_ARRAY -> {
                appendDoubleArray(builder, object);
                return builder.toString();
            }
            case DOUBLE_PRIMITIVE_ARRAY -> {
                appendDoublePrimitiveArray(builder, object);
                return builder.toString();
            }
            case BOOLEAN_ARRAY -> {
                appendBooleanArray(builder, object);
                return builder.toString();
            }
            case BOOLEAN_PRIMITIVE_ARRAY -> {
                appendBooleanPrimitiveArray(builder, object);
                return builder.toString();
            }
        }
        Field[] fields = object.getClass().getDeclaredFields();
        builder.append(BRACE_OPEN);
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            String name = fields[i].getName();
            Object value = fields[i].get(object);
            if (value != null) {
                builder.append(QUOTE).append(name).append(QUOTE)
                        .append(COLON);

                fillValues(builder, value);

                if (i < fields.length - 1) {
                    builder.append(COMMA);
                }
            }
            fields[i].setAccessible(false);
        }
        builder.append(BRACE_CLOSE);
        return builder.toString();
    }

    private void appendMap(StringBuilder builder, Object value) throws IllegalAccessException {
        builder.append(BRACE_OPEN);
        Map<?, ?> map = (Map<?, ?>) value;
        Set<?> keys = map.keySet();
        Object[] keysArray = keys.toArray();
        Object[] valuesArray = map.values().toArray();
        for (int i = 0; i < keysArray.length; i++) {
            builder.append(QUOTE).append(keysArray[i]).append(QUOTE)
                    .append(COLON);

            fillValues(builder, valuesArray[i]);

            if (i < keysArray.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(BRACE_CLOSE);
    }

    private void appendOptional(StringBuilder builder, Object value) throws IllegalAccessException {
        builder.append(BRACE_OPEN);
        if (((Optional<?>) value).isPresent()) {
            builder.append(QUOTE)
                    .append(VALUE)
                    .append(QUOTE)
                    .append(COLON)
                    .append(parseToJson(((Optional<?>) value).get()));
        }
        builder.append(BRACE_CLOSE);
    }

    private void appendCollection(StringBuilder builder, Object value) throws IllegalAccessException {
        Collection<?> collection = (Collection<?>) value;
        Object[] objects = collection.toArray();
        appendReferenceArray(builder, objects);
    }

    private void appendReferenceArray(StringBuilder builder, Object value) throws IllegalAccessException {
        builder.append(SQUARE_BRACE_OPEN);
        Object[] array = (Object[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(parseToJson(array[i]));
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendObjectArray(StringBuilder builder, Object value) throws IllegalAccessException {
        builder.append(SQUARE_BRACE_OPEN);
        Object[] array = (Object[]) value;
        for (int i = 0; i < array.length; i++) {
            String className = array[i].getClass().getSimpleName();
            switch (className) {
                case STRING, CHARACTER, CHARACTER_PRIMITIVE -> builder.append(QUOTE).append(array[i]).append(QUOTE);
                case BYTE, BYTE_PRIMITIVE, SHORT, SHORT_PRIMITIVE,
                        INTEGER, INTEGER_PRIMITIVE, LONG, LONG_PRIMITIVE,
                        FLOAT, FLOAT_PRIMITIVE, DOUBLE, DOUBLE_PRIMITIVE,
                        BOOLEAN, BOOLEAN_PRIMITIVE -> builder.append(array[i].toString());
                case STRING_ARRAY -> appendStringArray(builder, array[i]);
                case CHARACTER_ARRAY -> appendCharacterArray(builder, array[i]);
                case CHARACTER_PRIMITIVE_ARRAY -> appendCharacterPrimitiveArray(builder, array[i]);
                case BYTE_ARRAY -> appendByteArray(builder, array[i]);
                case BYTE_PRIMITIVE_ARRAY -> appendBytePrimitiveArray(builder, array[i]);
                case SHORT_ARRAY -> appendShortArray(builder, array[i]);
                case SHORT_PRIMITIVE_ARRAY -> appendShortPrimitiveArray(builder, array[i]);
                case INTEGER_ARRAY -> appendIntegerArray(builder, array[i]);
                case INTEGER_PRIMITIVE_ARRAY -> appendIntegerPrimitiveArray(builder, array[i]);
                case LONG_ARRAY -> appendLongArray(builder, array[i]);
                case LONG_PRIMITIVE_ARRAY -> appendLongPrimitiveArray(builder, array[i]);
                case FLOAT_ARRAY -> appendFloatArray(builder, array[i]);
                case FLOAT_PRIMITIVE_ARRAY -> appendFloatPrimitiveArray(builder, array[i]);
                case DOUBLE_ARRAY -> appendDoubleArray(builder, array[i]);
                case DOUBLE_PRIMITIVE_ARRAY -> appendDoublePrimitiveArray(builder, array[i]);
                case BOOLEAN_ARRAY -> appendBooleanArray(builder, array[i]);
                case BOOLEAN_PRIMITIVE_ARRAY -> appendBooleanPrimitiveArray(builder, array[i]);
                case OBJECT_ARRAY -> appendObjectArray(builder, array[i]);
                default -> builder.append(parseToJson(array[i]));
            }
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendStringArray(StringBuilder builder, Object value) {
        builder.append(SQUARE_BRACE_OPEN);
        String[] array = (String[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(QUOTE)
                    .append(array[i])
                    .append(QUOTE);
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendCharacterArray(StringBuilder builder, Object value) {
        builder.append(SQUARE_BRACE_OPEN);
        Character[] array = (Character[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(QUOTE)
                    .append(array[i])
                    .append(QUOTE);
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendCharacterPrimitiveArray(StringBuilder builder, Object value) {
        builder.append(SQUARE_BRACE_OPEN);
        char[] array = (char[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(QUOTE)
                    .append(array[i])
                    .append(QUOTE);
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendByteArray(StringBuilder builder, Object value) {
        builder.append(SQUARE_BRACE_OPEN);
        Byte[] array = (Byte[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendBytePrimitiveArray(StringBuilder builder, Object value) {
        builder.append(SQUARE_BRACE_OPEN);
        byte[] array = (byte[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendShortArray(StringBuilder builder, Object value) {
        builder.append(SQUARE_BRACE_OPEN);
        Short[] array = (Short[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendShortPrimitiveArray(StringBuilder builder, Object value) {
        builder.append(SQUARE_BRACE_OPEN);
        short[] array = (short[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendIntegerArray(StringBuilder builder, Object value) {
        builder.append(SQUARE_BRACE_OPEN);
        Integer[] array = (Integer[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendIntegerPrimitiveArray(StringBuilder builder, Object value) {
        builder.append(SQUARE_BRACE_OPEN);
        int[] array = (int[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendLongArray(StringBuilder builder, Object value) {
        builder.append(SQUARE_BRACE_OPEN);
        Long[] array = (Long[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendLongPrimitiveArray(StringBuilder builder, Object value) {
        builder.append(SQUARE_BRACE_OPEN);
        long[] array = (long[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendFloatArray(StringBuilder builder, Object value) {
        builder.append(SQUARE_BRACE_OPEN);
        Float[] array = (Float[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendFloatPrimitiveArray(StringBuilder builder, Object value) {
        builder.append(SQUARE_BRACE_OPEN);
        float[] array = (float[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendDoubleArray(StringBuilder builder, Object value) {
        builder.append(SQUARE_BRACE_OPEN);
        Double[] array = (Double[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendDoublePrimitiveArray(StringBuilder builder, Object value) {
        builder.append(SQUARE_BRACE_OPEN);
        double[] array = (double[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendBooleanArray(StringBuilder builder, Object value) {
        builder.append(SQUARE_BRACE_OPEN);
        Boolean[] array = (Boolean[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void appendBooleanPrimitiveArray(StringBuilder builder, Object value) {
        builder.append(SQUARE_BRACE_OPEN);
        boolean[] array = (boolean[]) value;
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(COMMA);
            }
        }
        builder.append(SQUARE_BRACE_CLOSE);
    }

    private void fillValues(StringBuilder builder, Object value) throws IllegalAccessException {
        if (value.getClass().isEnum()) {
            builder.append(QUOTE).append(value).append(QUOTE);
        } else if (Collection.class.isAssignableFrom(value.getClass())) {
            appendCollection(builder, value);
        } else if (Map.class.isAssignableFrom(value.getClass())) {
            appendMap(builder, value);
        } else if (value.getClass().isArray()) {
            switch (value.getClass().getSimpleName()) {
                case STRING_ARRAY -> appendStringArray(builder, value);
                case CHARACTER_ARRAY -> appendCharacterArray(builder, value);
                case CHARACTER_PRIMITIVE_ARRAY -> appendCharacterPrimitiveArray(builder, value);
                case BYTE_ARRAY -> appendByteArray(builder, value);
                case BYTE_PRIMITIVE_ARRAY -> appendBytePrimitiveArray(builder, value);
                case SHORT_ARRAY -> appendShortArray(builder, value);
                case SHORT_PRIMITIVE_ARRAY -> appendShortPrimitiveArray(builder, value);
                case INTEGER_ARRAY -> appendIntegerArray(builder, value);
                case INTEGER_PRIMITIVE_ARRAY -> appendIntegerPrimitiveArray(builder, value);
                case LONG_ARRAY -> appendLongArray(builder, value);
                case LONG_PRIMITIVE_ARRAY -> appendLongPrimitiveArray(builder, value);
                case FLOAT_ARRAY -> appendFloatArray(builder, value);
                case FLOAT_PRIMITIVE_ARRAY -> appendFloatPrimitiveArray(builder, value);
                case DOUBLE_ARRAY -> appendDoubleArray(builder, value);
                case DOUBLE_PRIMITIVE_ARRAY -> appendDoublePrimitiveArray(builder, value);
                case BOOLEAN_ARRAY -> appendBooleanArray(builder, value);
                case BOOLEAN_PRIMITIVE_ARRAY -> appendBooleanPrimitiveArray(builder, value);
                default -> appendReferenceArray(builder, value);
            }
        } else {
            switch (value.getClass().getSimpleName()) {
                case STRING, CHARACTER, CHARACTER_PRIMITIVE -> builder.append(QUOTE).append(value).append(QUOTE);
                case BYTE, BYTE_PRIMITIVE, SHORT, SHORT_PRIMITIVE,
                        INTEGER, INTEGER_PRIMITIVE, LONG, LONG_PRIMITIVE,
                        FLOAT, FLOAT_PRIMITIVE, DOUBLE, DOUBLE_PRIMITIVE,
                        BOOLEAN, BOOLEAN_PRIMITIVE,
                        BIG_DECIMAL, BIG_INTEGER -> builder.append(value);
                default -> builder.append(parseToJson(value));
            }
        }
    }
}
