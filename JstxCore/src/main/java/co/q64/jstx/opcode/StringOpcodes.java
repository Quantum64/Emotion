package co.q64.jstx.opcode;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.jstx.lang.opcode.OpcodeMarker;
import co.q64.jstx.lang.opcode.OpcodeRegistry;

@Singleton
public class StringOpcodes extends OpcodeRegistry {
	protected @Inject StringOpcodes() {
		super(OpcodeMarker.STRING);
	}

	@Override
	public void register() {

		r("string.charAt", stack -> stack.push(stack.peek(2).toString().charAt(stack.pull(2).asInt())), "Push the character of the second stack value at the index of the top stack value.");
		r("string.compareTo", stack -> stack.push(stack.peek(2).toString().compareTo(stack.pull(2).toString())), "Push the comparison integer of the second stack value to the first stack value.");
		r("string.contains", stack -> stack.push(stack.peek(2).toString().contains(stack.pull(2).toString())), "Push a true if the second stack value contains the first stack value, else false.");
		r("string.concat", stack -> stack.push(stack.peek(2).toString().concat(stack.pull(2).toString())), "Push the second stack value concatenated with the first stack value, always interpreted as strings.");
		r("string.endsWith", stack -> stack.push(stack.peek(2).toString().endsWith(stack.pull(2).toString())), "Push a true if the second stack value ends with the first stack value, else false.");
		r("string.equals", stack -> stack.push(stack.peek(2).toString().equals(stack.pull(2).toString())), "Push a true if the second stack value exactly equals the first stack value, else false.");
		r("string.equalsIgnoreCase", stack -> stack.push(stack.peek(2).toString().equalsIgnoreCase(stack.pull(2).toString())), "Push a true if the second stack value equals the first stack value disregarding case, else false.");
		//r("string.format",  stack -> stack.push(String.format(stack.peek(2).toString(), stack.pull(2).iterate().toArray()))); // GWT does not like this
		r("string.getBytes", stack -> stack.push(IntStream.range(0, stack.peek().toString().getBytes(StandardCharsets.UTF_8).length).map(i -> stack.pop().toString().getBytes(StandardCharsets.UTF_8)[i]).boxed().map(Object::toString).collect(Collectors.toList())), "Push a list of bytes representing the top stack value interpreted as a string.");
		r("string.hashCode", stack -> stack.push(stack.pop().toString().hashCode()), "Push hash code generated by the first stack value interpreted as a string.");
		r("string.indexOfChar", stack -> stack.push(stack.peek(2).toString().indexOf(stack.pull(2).asChar())), "Push the index of the first character in the first stack value in the second stack value, else -1.");
		r("string.indexOf", stack -> stack.push(stack.peek(2).toString().indexOf(stack.pull(2).toString())), "Push the index of the first stack value in the second stack value, else -1.");
		r("string.intern", stack -> stack.push(stack.pop().toString().intern()), "Pushed the pooled reference of the top stack value.");
		r("string.isEmpty", stack -> stack.push(stack.pop().toString().isEmpty()), "Push true if the top stack value is empty, else false.");
		r("string.lastIndexOfChar", stack -> stack.push(stack.peek(2).toString().lastIndexOf(stack.pull(2).asChar())), "Push the last index of the first character in the first stack value in the second stack value, else -1.");
		r("string.lastIndexOf", stack -> stack.push(stack.peek(2).toString().lastIndexOf(stack.pull(2).toString())), "Push the last index of the first stack value in the second stack value, else -1.");
		r("string.length", stack -> stack.push(stack.pop().toString().length()), "Push the length of the first stack value interpreted as a string.");
		r("string.matches", stack -> stack.push(stack.peek(2).toString().matches(stack.pull(2).toString())), "Push true if the second stack value matches the first stack value interpreted as a regular expression, else false.");
		r("string.offsetByCodePoints", stack -> stack.push(stack.peek(3).toString().offsetByCodePoints(stack.peek(2).asInt(), stack.pull(3).asInt())), "Push the index in the third stack value at the index of the second stack value offset by the first stack value interpreted as an integer.");
		r("string.replace", stack -> stack.push(stack.peek(3).toString().replace(stack.peek(2).toString(), stack.pull(3).toString())), "Push the third stack value with instances of the second stack value replaced with the first stack value.");
		r("string.replaceAll", stack -> stack.push(stack.peek(3).toString().replaceAll(stack.peek(2).toString(), stack.pull(3).toString())), "Push the third stack value with instances of the second stack value interpreted as a regular expression replaced with the first stack value.");
		r("string.replaceFirst", stack -> stack.push(stack.peek(3).toString().replaceFirst(stack.peek(2).toString(), stack.pull(3).toString())), "Push the third stack value with the first instance of the second stack value interpreted as a regular expression replaced with the first stack value.");
		r("string.split", stack -> stack.push(Arrays.asList((Object[]) stack.peek(2).toString().split(stack.pull(2).toString()))), "Push a list of strings obtained by splitting the second stack value with the first stack value.");
		r("string.startsWith", stack -> stack.push(stack.peek(2).toString().startsWith(stack.pull(2).toString())), "Push a true if the second stack value starts with the first stack value, else false.");
		r("string.subSequence", stack -> stack.push(stack.peek(3).toString().subSequence(stack.peek(2).asInt(), stack.pull(3).asInt())), "Push a subsequence of characters from the third stack value indexed from the second stack value to the first stack value.");
		r("string.substring", stack -> stack.push(stack.peek(3).toString().substring(stack.peek(2).asInt(), stack.pull(3).asInt())), "Push the parts of the third stack value in the range of the second stack value to the first stack value.");
		r("string.substr", stack -> stack.push(stack.peek(2).toString().substring(stack.pull(2).asInt())), "Push the parts of the second stack value indexed past the first stack value.");
		r("string.toLowerCase", stack -> stack.push(stack.pop().toString().toLowerCase()), "Push the first stack value with all characters uppercased.");
		r("string.toUpperCase", stack -> stack.push(stack.pop().toString().toUpperCase()), "Push the first stack value with all characters lowercased.");
		r("string.trim", stack -> stack.push(stack.pop().toString().trim()), "Push the first stack value sans extra whitespace.");

		r("string.subzero", stack -> stack.push(stack.peek(2).toString().substring(0, stack.pull(2).asInt())), "Push the characters of the second stack value indexed before or at the first stack value.");
		r("string.reverse", stack -> stack.push(new StringBuilder(stack.pop().toString()).reverse()), "Push the first stack value with characters in reverse order.");
		r("string.reverseconcat", stack -> stack.push(stack.pop().toString().concat(stack.pop().toString())), "Push the first stack value concatenated with the second stack value.");
		r("string.charValue", stack -> stack.push(((int) stack.pop().asChar())), "Push the integer value of the first character of the first stack value.");
		r("string.deleteMatches", stack -> stack.push(stack.peek(2).toString().replaceAll(stack.pull(2).toString(), "")), "Push the second stack value with occurences of the first stack value interpreted as a regular expression removed.");
		r("string.deleteAfter", stack -> stack.push(stack.peek(2).toString().indexOf(stack.peek().toString()) > -1 ? stack.peek(2).toString().substring(0, stack.peek().toString().length() + stack.peek(2).toString().indexOf(stack.pull(2).asInt())) : stack.pop(2)), "Push the second stack value with characters occuring after the first stack value removed.");
		r("string.deleteAndAfter", stack -> stack.push(stack.peek(2).toString().indexOf(stack.peek().toString()) > -1 ? stack.peek(2).toString().substring(0, stack.peek(2).toString().indexOf(stack.pull(2).asInt())) : stack.pop(2)), "Push the second stack value with characters occuring at or after the first stack value removed.");
		r("string.instancesOf", stack -> stack.push(stack.peek(2).toString().split(stack.pull(2).toString(), -1).length - 1), "Push the number of instances of the first stack value in the second stack value.");
		r("string.unique", stack -> stack.push(stack.pop().toString().chars().mapToObj(c -> (char) c).distinct().map(Object::toString).collect(Collectors.joining())), "Push the first stack value with duplicate characters removed.");
		r("string.repeat", stack -> stack.push(String.join("", Collections.nCopies(2, stack.pop().toString()))), "Push the first stack value repeated twice.");
		r("string.delete", stack -> stack.push(stack.peek(2).toString().replace(stack.pull(2).toString(), "")), "Push the second stack value with occurences of the first stack value removed.");
		r("string.blank", stack -> stack.push(stack.peek(2).toString().replace(stack.peek(1).toString(), Collections.nCopies(stack.pull(2).toString().length(), " ").stream().collect(Collectors.joining()))), "Push the second stack value with occurences of the first stack value replaced with whitespace.");

		r("string.deleteEnd", stack -> {
			String target = stack.pop().toString();
			String str = stack.pop().toString();
			if (str.endsWith(target)) {
				stack.push(str.substring(0, str.length() - target.length()));
				return;
			}
			stack.push(str);
		}, "Push the second stack value with instances of the first stack value removed if they occur at the end of the second stack value.");

		r("string.titleCase", stack -> {
			char[] chars = stack.pop().toString().toCharArray();
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < chars.length; i++) {
				result.append(i == 0 || String.valueOf(chars[i - 1]).equals(" ") ? String.valueOf(chars[i]).toUpperCase() : String.valueOf(chars[i]).toLowerCase());
			}
			stack.push(result.toString());
		}, "Push the first stack value transformed to Title Case.");

		r("string.lowerCamelCase", stack -> {
			char[] chars = stack.pop().toString().toCharArray();
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < chars.length; i++) {
				result.append(i != 0 && String.valueOf(chars[i - 1]).equals(" ") ? String.valueOf(chars[i]).toUpperCase() : String.valueOf(chars[i]).toLowerCase());
			}
			stack.push(result.toString());
		}, "Push the first stack value transformed to lowerCamelCase.");

		r("string.camelCase", stack -> {
			char[] chars = stack.pop().toString().toCharArray();
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < chars.length; i++) {
				result.append(i == 0 || String.valueOf(chars[i - 1]).equals(" ") ? String.valueOf(chars[i]).toUpperCase() : String.valueOf(chars[i]).toLowerCase());
			}
			stack.push(result.toString());
		}, "Push the first stack value transformed to CamelCase.");

		r("string.sentenceCase", stack -> {
			char[] chars = stack.pop().toString().toCharArray();
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < chars.length; i++) {
				result.append(i == 0 ? String.valueOf(chars[i]).toUpperCase() : String.valueOf(chars[i]).toLowerCase());
			}
			stack.push(result.toString());
		}, "Push the first stack value transformed to Sentence case.");

		r("string.shiftChars", stack -> {
			StringBuilder result = new StringBuilder();
			int amount = stack.pop().asInt();
			for (char c : stack.pop().toString().toCharArray()) {
				result.append((char) (c + amount));
			}
			stack.push(result);
		}, "Push the second stack value with all characters arithmetically shifted by the first stack value.");

		r("string.snakeCase", stack -> stack.push(stack.pop().toString().toLowerCase().replace(" ", "_")), "Push the first stack value transformed to snake_case.");
	}
}
