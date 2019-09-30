package co.q64.emotion.lang.value;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.auto.factory.AutoFactory;

import co.q64.emotion.types.Comparison;
import co.q64.emotion.types.Operation;
import co.q64.emotion.util.Base64;
import lombok.EqualsAndHashCode;

@AutoFactory
@EqualsAndHashCode
public class Literal implements Value {
	private String literal;

	private Literal(String literal) {
		this.literal = literal;
	}

	protected Literal(Object literal) {
		this(literal.toString());
	}

	protected Literal(List<?> list) {
		this("<<" + list.stream().map(Object::toString).map(element -> Base64.encode(element.getBytes(StandardCharsets.UTF_8))).collect(Collectors.joining(",")) + ">>");
	}

	@Override
	public String toString() {
		List<Value> elements = getBase64ListElements();
		if (elements.size() > 0) {
			return "[" + elements.stream().map(Value::toString).collect(Collectors.joining(",")) + "]";
		}
		return literal;
	}

	@Override
	public List<Value> iterate() {
		List<Value> result = getElements();
		if (result.size() > 0) {
			return result;
		}
		result = new ArrayList<>();
		if (isInteger()) {
			for (long l = 0; l < asLong(); l++) {
				result.add(new Literal(l));
			}
		} else {
			for (char c : literal.toCharArray()) {
				result.add(new Literal(c));
			}
		}
		return result;
	}

	private List<Value> getElements() {
		List<Value> result = getStandardListElements();
		if (result.size() == 0) {
			result = getBase64ListElements();
		}
		return result;
	}

	private List<Value> getStandardListElements() {
		return getElements("[", "]").stream().map(Literal::new).collect(Collectors.toList());
	}

	private List<Value> getBase64ListElements() {
		return getElements("<<", ">>").stream().map(element -> new Literal(new String(Base64.decode(element), StandardCharsets.UTF_8))).collect(Collectors.toList());
	}

	private List<String> getElements(String start, String end) {
		if (literal.startsWith(start) && literal.endsWith(end)) {
			List<String> elements = new ArrayList<>();
			StringBuilder currentElement = new StringBuilder();
			char[] chars = literal.toCharArray();
			for (int i = start.length(); i < chars.length - end.length(); i++) {
				if (String.valueOf(chars[i]).equals(",")) {
					elements.add(currentElement.toString());
					currentElement = new StringBuilder();
					continue;
				}
				currentElement.append(chars[i]);
			}
			if (elements.size() > 0) {
				if (currentElement.length() > 0) {
					elements.add(currentElement.toString());
				}
				return elements.stream().collect(Collectors.toList());
			}
			return Arrays.asList(currentElement.toString());
		}
		return Collections.emptyList();
	}

	public boolean isInteger() {
		try {
			Long.parseLong(literal);
			return true;
		} catch (Exception e) {}
		return false;
	}

	public boolean isBoolean() {
		return literal.equalsIgnoreCase("true") || literal.equalsIgnoreCase("false") || literal.equals("1") || literal.equals("0");
	}

	public boolean isList() {
		return getElements().size() > 0;
	}

	public boolean isFloat() {
		try {
			Double.parseDouble(literal);
			return true;
		} catch (Exception e) {}
		return false;
	}

	@Override
	public int asInt() {
		try {
			return Integer.parseInt(literal);
		} catch (Exception e) {}
		if (isBoolean()) {
			return asBoolean() ? 1 : 0;
		}
		if (literal.length() == 1) {
			return literal.charAt(0);
		}
		return literal.length();
	}

	@Override
	public long asLong() {
		try {
			return Long.parseLong(literal);
		} catch (Exception e) {}
		if (isBoolean()) {
			return asBoolean() ? 1 : 0;
		}
		if (literal.length() <= 1) {
			return literal.charAt(0);
		}
		return literal.length();
	}

	@Override
	public double asDouble() {
		try {
			return Double.parseDouble(literal);
		} catch (Exception e) {}
		return 0;
	}

	@Override
	public boolean asBoolean() {
		if (literal.equalsIgnoreCase("true")) {
			return true;
		}
		if (literal.equalsIgnoreCase("false")) {
			return false;
		}
		return !literal.equals("0");
	}

	@Override
	public char asChar() {
		if (literal.length() > 0) {
			return literal.charAt(0);
		}
		return 0;
	}

	@Override
	public boolean compare(Value value, Comparison type) {
		switch (type) {
		case EQUAL:
		case GREATER:
		case LESS:
			return simpleCompare(value, type);
		case NOT_EQUAL:
			return !simpleCompare(value, type);
		case EQUAL_LESS:
		case EQUAL_GREATER:
			return simpleCompare(value, type) || simpleCompare(value, Comparison.EQUAL);
		default:
			break;
		}
		return false;
	}

	private boolean simpleCompare(Value value, Comparison type) {
		if (isFloat() && value.isFloat()) {
			if (isInteger() && value.isInteger()) {
				switch (type) {
				case EQUAL:
					return asLong() == value.asLong();
				case GREATER:
					return asLong() > value.asLong();
				case LESS:
					return asLong() < value.asLong();
				default:
					break;
				}
			}
			switch (type) {
			case EQUAL:
				return asDouble() == value.asDouble();
			case GREATER:
				return asDouble() > value.asDouble();
			case LESS:
				return asDouble() < value.asDouble();
			default:
				break;
			}
		}
		if (isBoolean() && value.isBoolean()) {
			switch (type) {
			case EQUAL:
				return asBoolean() == value.asBoolean();
			case GREATER:
				return asBoolean() == value.asBoolean();
			case LESS:
				return asBoolean() == value.asBoolean();
			default:
				break;
			}
		}
		if (value.isBoolean() && isInteger()) {
			switch (type) {
			case EQUAL:
				return value.asBoolean() == true ? asInt() != 0 : asInt() == 0;
			default:
				return false;
			}
		}
		if (isBoolean() && value.isInteger()) {
			switch (type) {
			case EQUAL:
				return asBoolean() == true ? value.asInt() != 0 : value.asInt() == 0;
			default:
				return false;
			}
		}
		if (isList() && value.isList()) {
			switch (type) {
			case EQUAL:
				List<Value> currentList = iterate();
				List<Value> targetList = iterate();
				boolean equal = true;
				if (currentList.size() == targetList.size()) {
					for (int i = 0; i < currentList.size(); i++) {
						if (!currentList.get(i).compare(targetList.get(i), Comparison.EQUAL)) {
							equal = false;
							break;
						}
					}
				} else {
					equal = false;
				}
				return equal;
			default:
				return false;
			}
		}
		switch (type) {
		case EQUAL:
			return toString().equals(value.toString());
		case GREATER:
			return toString().length() > value.toString().length();
		case LESS:
			return toString().length() < value.toString().length();
		default:
			break;
		}
		return false;
	}

	@Override
	public Value operate(Value value, Operation type) {
		if (isInteger() && value.isInteger()) {
			switch (type) {
			case DIVIDE:
				return new Literal(asLong() / value.asLong());
			case SUBTRACT:
				return new Literal(asLong() - value.asLong());
			case MULTIPLY:
				return new Literal(asLong() * value.asLong());
			case ADD:
				return new Literal(asLong() + value.asLong());
			}
		}
		if (isFloat() && value.isFloat()) {
			switch (type) {
			case DIVIDE:
				return new Literal(asDouble() / value.asDouble());
			case SUBTRACT:
				return new Literal(asDouble() - value.asDouble());
			case MULTIPLY:
				return new Literal(asDouble() * value.asDouble());
			case ADD:
				return new Literal(asDouble() + value.asDouble());
			}
		}
		if (isBoolean() && value.isBoolean()) {
			switch (type) {
			case DIVIDE:
				return new Literal(asBoolean() == value.asBoolean());
			case SUBTRACT:
				return new Literal(asBoolean() != value.asBoolean());
			case MULTIPLY:
				return new Literal(asBoolean() == value.asBoolean());
			case ADD:
				return new Literal(asBoolean() == value.asBoolean());
			}
		}
		if (isList() && value.isList()) {
			Iterator<Value> itr = value.iterate().iterator();
			return new Literal(iterate().stream().map(v -> v.operate(itr.next(), type)).collect(Collectors.toList()));
		}
		if (isList() && !value.isList()) {
			return new Literal(iterate().stream().map(v -> v.operate(value, type)).collect(Collectors.toList()));
		}
		if (!isList() && value.isList()) {
			return new Literal(value.iterate().stream().map(v -> operate(v, type)).collect(Collectors.toList()));
		}
		if (isInteger()) {
			return new Literal(Collections.nCopies(asInt(), value.toString()).stream().collect(Collectors.joining()));
		}
		if (value.isInteger()) {
			return new Literal(Collections.nCopies(value.asInt(), toString()).stream().collect(Collectors.joining()));
		}
		switch (type) {
		case DIVIDE:
			return new Literal(toString() + value.toString());
		case SUBTRACT:
			return new Literal(value.toString() + toString());
		case MULTIPLY:
			return new Literal(toString() + value.toString());
		case ADD:
			return new Literal(toString() + value.toString());
		}
		return new Literal(toString() + value.toString());
	}
}
