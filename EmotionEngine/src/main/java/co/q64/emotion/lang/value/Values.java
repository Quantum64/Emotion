package co.q64.emotion.lang.value;

import co.q64.emotion.lang.value.standard.BooleanValue;
import co.q64.emotion.lang.value.standard.ListValue;
import co.q64.emotion.lang.value.standard.NumberValue;
import co.q64.emotion.lang.value.standard.TextValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Values {
    public static Value create(String input) {
        if (input.contains("/") && input.length() > input.indexOf("/")) {
            String n = input.substring(0, input.indexOf("/")), d = input.substring(input.indexOf("/") + 1);
            try {
                return NumberValue.of(n, d);
            } catch (NumberFormatException e) {}
        }
        try {
            return NumberValue.of(Long.parseLong(input));
        } catch (NumberFormatException e) {}
        try {
            return NumberValue.of(input);
        } catch (NumberFormatException e) {}
        if (input.equalsIgnoreCase("true")) {
            return BooleanValue.of(true);
        }
        if (input.equalsIgnoreCase("false")) {
            return BooleanValue.of(false);
        }
        if (input.startsWith("[") && input.endsWith("]")) {
            List<Value> elements = new ArrayList<>();
            StringBuilder currentElement = new StringBuilder();
            char[] chars = input.toCharArray();
            for (int i = 1; i < chars.length - 1; i++) {
                if (String.valueOf(chars[i]).equals(",")) {
                    elements.add(create(currentElement.toString()));
                    currentElement = new StringBuilder();
                    continue;
                }
                currentElement.append(chars[i]);
            }
            if (elements.size() > 0) {
                if (currentElement.length() > 0) {
                    elements.add(create(currentElement.toString()));
                }
                return ListValue.of(elements);
            }
            return ListValue.of(Arrays.asList(create(currentElement.toString())));
        }
        return TextValue.of(input);
    }

    public static Value create(Value input) {
        if (input instanceof TextValue) {
            return create(input.toString());
        }
        return input;
    }

    public static Value create(long input) {
        return NumberValue.of(input);
    }

    public static Value create(boolean input) {
        return BooleanValue.of(input);
    }

    public static Value create(List<?> input) {
        return ListValue.of(input);
    }
}
