package co.q64.emotion.opcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.emotion.lang.Stack;
import co.q64.emotion.lang.opcode.OpcodeMarker;
import co.q64.emotion.lang.opcode.OpcodeRegistry;
import co.q64.emotion.lang.value.LiteralFactory;
import co.q64.emotion.lang.value.Null;
import co.q64.emotion.lang.value.Value;

@Singleton
public class ListOpcodes extends OpcodeRegistry {
	protected @Inject Null nul;
	protected @Inject LiteralFactory literal;
	protected @Inject ValueSorter sorter;

	protected @Inject ListOpcodes() {
		super(OpcodeMarker.LIST);
	}

	@Override
	public void register() {
		r("list.flatten", stack -> stack.push(stack.pop().iterate().stream().map(Object::toString).collect(Collectors.joining())), "Push all elements from the first stack value as a string.");
		r("list.flattenSoft", stack -> stack.push(stack.pop().iterate().stream().map(Object::toString).collect(Collectors.joining(" "))), "Push all elements from the first stack value as a string seperated by a space.");
		r("list.singleton", stack -> stack.push(listFromStack(stack, 1)), "Push the first stack value as a list.");
		r("list.join", stack -> stack.push(stack.peek(2).iterate().stream().map(Object::toString).collect(Collectors.joining(stack.pull(2).toString()))), "Push all elements from the second stack value as a string seperated by the first stack value.");
		r("list.unique", stack -> stack.push(stack.pop().iterate().stream().distinct().collect(Collectors.toList())), "Push the first stack value with duplicate elements removed.");
		r("list.explode", stack -> stack.pop().iterate().forEach(x -> stack.push(x)), "Push every value from the first stack value, interpreted as a list.");
		r("list.size", stack -> stack.push(stack.pop().iterate().size()), "Push the size of the first stack value.");
		r("list.length", stack -> stack.push(stack.pop().iterate().stream().filter(o -> o != nul).count()), "Push the size of the first stack value, excluding null elements.");
		r("list.of", stack -> stack.push(IntStream.range(0, stack.pop().asInt()).mapToObj(i -> stack.pop()).collect(Collectors.toList())), "Push stack values into a list of the size of the first stack value starting with the second stack value.");
		r("list.empty", stack -> stack.push(Collections.emptyList()), "Push an empty list.");
		r("list.uniform", stack -> stack.push(stack.pop().iterate().stream().distinct().limit(2).count() <= 1), "Push true if all elements in the list are equal, else false.");
		r("list.sum", stack -> stack.push(stack.pop().iterate().stream().mapToLong(Value::asLong).sum()), "Push the sum of an integer list.");
		r("list.sumf", stack -> stack.push(stack.pop().iterate().stream().mapToLong(Value::asLong).sum()), "Push the sum of a floating point list.");
		r("list.product", stack -> stack.push(stack.pop().iterate().stream().mapToLong(Value::asLong).reduce(1, (a, b) -> a * b)), "Push the product of an integer list.");
		r("list.productf", stack -> stack.push(stack.pop().iterate().stream().mapToLong(Value::asLong).reduce(1, (a, b) -> a * b)), "Push the product of a floating point integer list.");
		//r("list.mode", stack -> stack.push(stack.pop().iterate().stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream().max(Comparator.comparing(Entry::getValue)).map(Entry::getKey).orElse(nul)), "Push the most common element in the first stack value, the mode of the list.");
		r("list.mean", stack -> stack.push(stack.pop().iterate().stream().mapToDouble(Value::asDouble).average().orElse(Double.NaN)), "Push the average of the elements in the first stack value, the mean of the list.");
		r("list.max", stack -> stack.push(stack.pop().iterate().stream().mapToInt(Value::asInt).max().orElse(nul.asInt())), "Push the greatest integer in the first stack value.");
		r("list.maxf", stack -> stack.push(stack.pop().iterate().stream().mapToDouble(Value::asDouble).max().orElse(nul.asInt())), "Push the greatest floating point number in the first stack value.");
		r("list.min", stack -> stack.push(stack.pop().iterate().stream().mapToInt(Value::asInt).min().orElse(nul.asInt())), "Push the least integer in the first stack value.");
		r("list.minf", stack -> stack.push(stack.pop().iterate().stream().mapToDouble(Value::asDouble).min().orElse(nul.asInt())), "Push the least floating point number in the first stack value.");
		r("list.reverse", stack -> stack.push(apply(stack.pop().iterate(), Collections::reverse)), "Reverses the list in the first stack value.");
		r("list.range", stack -> stack.push(IntStream.range(stack.peek(2).asInt(), stack.pull(2).asInt()).boxed().map(literal::create).collect(Collectors.toList())), "Push a list of integers in the range of the second stack value to the first stack value minus one.");
		r("list.reverseRange", stack -> stack.push(apply(IntStream.range(stack.peek(2).asInt(), stack.pull(2).asInt()).boxed().map(literal::create).collect(Collectors.toList()), Collections::reverse)), "Push a list of integers in decending order in the range of the second stack value to the first stack value minus one.");
		r("list.rangeClosed", stack -> stack.push(IntStream.rangeClosed(stack.peek(2).asInt(), stack.pull(2).asInt()).boxed().map(literal::create).collect(Collectors.toList())), "Push a list of integers in the range of the second stack value to the first stack value.");
		r("list.reverseRangeClosed", stack -> stack.push(apply(IntStream.rangeClosed(stack.peek(2).asInt(), stack.pull(2).asInt()).boxed().map(literal::create).collect(Collectors.toList()), Collections::reverse)), "Push a list of integers in decending order in the range of the second stack value to the first stack value.");
		r("list.set", stack -> stack.push(set(stack.pop().asInt(), stack.pop(), stack.pop().iterate())), "Set the value in the list in the third stack value to the second stack value at the index of the first stack value.");
		r("list.get", stack -> stack.push(get(stack.pop().asInt(), stack.pop().iterate())), "Push the value in the list of the second stack value at the index of the first stack value.");
		r("list.add", stack -> stack.push(apply(stack.peek(2).iterate(), list -> list.add(stack.pull(2)))), "Add the first stack value to the list in the second stack value. Does not remove the list from the stack.");
		r("list.remove", stack -> stack.push(apply(stack.peek(2).iterate(), list -> list.remove(stack.pull(2).asInt()))), "Remove the first stack value from the list in the second stack value. Does not remove the list from the stack.");
		r("list.sort", stack -> stack.push(apply(stack.pop().iterate(), list -> Collections.sort(list, sorter))), "Push the first stack value sorted in ascending order.");
		r("list.reverseSort", stack -> stack.push(apply(stack.pop().iterate(), list -> Collections.sort(list, Collections.reverseOrder(sorter)))), "Push the first stack value sorted in decending order.");
		r("list.shuffle", stack -> stack.push(apply(stack.pop().iterate(), Collections::shuffle)), "Push the list on the first stack value with elements in random order.");

		r("list.pair", stack -> stack.push(listFromStack(stack, 2)), "Push the second and first stack values as a list.");
		r("list.triad", stack -> stack.push(listFromStack(stack, 3)), "Push the third, second, and first stack values as a list.");
		r("list.quad", stack -> stack.push(listFromStack(stack, 4)), "Push the first four stack values in reverse order as a list.");
		r("list.quint", stack -> stack.push(listFromStack(stack, 5)), "Push the first five stack values in reverse order as a list.");
		r("list.hextuple", stack -> stack.push(listFromStack(stack, 6)), "Push the first six stack values in reverse order as a list.");
		r("list.heptuple", stack -> stack.push(listFromStack(stack, 7)), "Push the first seven stack values in reverse order as a list.");
		r("list.octuple", stack -> stack.push(listFromStack(stack, 8)), "Push the first eight stack values in reverse order as a list.");
		r("list.nonuple", stack -> stack.push(listFromStack(stack, 9)), "Push the first nine stack values in reverse order as a list.");
		r("list.decuple", stack -> stack.push(listFromStack(stack, 10)), "Push the first 10 stack values in reverse order as a list.");
		r("list.undecuple", stack -> stack.push(listFromStack(stack, 11)), "Push the first 11 stack values in reverse order as a list.");
		r("list.duodecuple", stack -> stack.push(listFromStack(stack, 12)), "Push the first 12 stack values in reverse order as a list.");
		r("list.tredecuple", stack -> stack.push(listFromStack(stack, 13)), "Push the first 13 stack values in reverse order as a list.");
		r("list.quattuordecuple", stack -> stack.push(listFromStack(stack, 14)), "Push the first 14 stack values in reverse order as a list.");
		r("list.quindecuple", stack -> stack.push(listFromStack(stack, 15)), "Push the first 15 stack values in reverse order as a list.");
		r("list.sexdecuple", stack -> stack.push(listFromStack(stack, 16)), "Push the first 16 stack values in reverse order as a list.");
		r("list.septendecuple", stack -> stack.push(listFromStack(stack, 17)), "Push the first 17 stack values in reverse order as a list.");
		r("list.octodecuple", stack -> stack.push(listFromStack(stack, 18)), "Push the first 18 stack values in reverse order as a list.");
		r("list.novemdecuple", stack -> stack.push(listFromStack(stack, 19)), "Push the first 19 stack values in reverse order as a list.");
		r("list.vigintuple", stack -> stack.push(listFromStack(stack, 20)), "Push the first 20 stack values in reverse order as a list.");
		r("list.unvigintuple", stack -> stack.push(listFromStack(stack, 21)), "Push the first 21 stack values in reverse order as a list.");
		r("list.duovigintuple", stack -> stack.push(listFromStack(stack, 22)), "Push the first 22 stack values in reverse order as a list.");
		r("list.trevigintuple", stack -> stack.push(listFromStack(stack, 23)), "Push the first 23 stack values in reverse order as a list.");
		r("list.quattuorvigintuple", stack -> stack.push(listFromStack(stack, 24)), "Push the first 24 stack values in reverse order as a list.");
		r("list.quinvigintuple", stack -> stack.push(listFromStack(stack, 25)), "Push the first 25 stack values in reverse order as a list.");
	}

	private List<Value> apply(List<Value> list, Consumer<List<Value>> func) {
		func.accept(list);
		return list;
	}

	private Value get(int index, List<Value> list) {
		if (index >= list.size()) {
			return nul;
		}
		return list.get(index);
	}

	private List<Value> set(int index, Value value, List<Value> list) {
		if (list.size() > index) {
			list.set(index, value);
		} else {
			int size = list.size();
			for (int i = size; i < index; i++) {
				list.add(nul);
			}
			list.add(value);
		}
		return list;
	}

	private List<String> listFromStack(Stack stack, int elements) {
		List<String> result = new ArrayList<>();
		for (int i = 0; i < elements; i++) {
			result.add(stack.pop().toString());
		}
		Collections.reverse(result);
		return result;
	}

	@Singleton
	protected static class ValueSorter implements Comparator<Value> {
		protected @Inject ValueSorter() {}

		@Override
		public int compare(Value o1, Value o2) {
			if (o1.isInteger() && o2.isInteger()) {
				return Long.compare(o1.asLong(), o2.asLong());
			}
			if (o1.isFloat() && o2.isFloat()) {
				return Double.compare(o1.asDouble(), o2.asDouble());
			}
			if (o1.isBoolean() && o2.isBoolean()) {
				return Boolean.compare(o1.asBoolean(), o2.asBoolean());
			}
			return o1.toString().compareTo(o2.toString());
		}
	}
}
