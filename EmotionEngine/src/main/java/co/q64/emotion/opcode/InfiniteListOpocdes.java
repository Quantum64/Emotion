package co.q64.emotion.opcode;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.emotion.lang.opcode.OpcodeMarker;
import co.q64.emotion.lang.opcode.OpcodeRegistry;
import co.q64.emotion.lang.value.InfiniteListFactory;
import co.q64.emotion.lang.value.LiteralFactory;
import co.q64.emotion.lang.value.Value;
import co.q64.emotion.util.MathUtil;

@Singleton
public class InfiniteListOpocdes extends OpcodeRegistry {
	private static final int[] SIZES = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000 };

	protected @Inject InfiniteListFactory infiniteList;
	protected @Inject MathUtil mathUtil;
	protected @Inject LiteralFactory literal;

	private Map<Integer, BigInteger> fibonacci = new HashMap<>();

	protected @Inject InfiniteListOpocdes() {
		super(OpcodeMarker.LINEAR);
	}

	@Override
	public void register() {
		r("infinilist.primes", stack -> stack.push(infiniteList.create(n -> literal.create(mathUtil.nthPrime(n + 1)))), "Push the infinite list of primes.");
		r("infinilist.fib", stack -> stack.push(infiniteList.create(n -> literal.create(fibonacci(n).toString()))), "Push the infinite list of fibonacci numbers.");
		r("infinilist.squares", stack -> stack.push(infiniteList.create(n -> literal.create((n + 1) * (n + 1)))), "Push the infinite list of perfect squares.");
		r("infinilist.even", stack -> stack.push(infiniteList.create(n -> literal.create(n * 2))), "Push the infinite list of even numbers.");
		r("infinilist.odd", stack -> stack.push(infiniteList.create(n -> literal.create(n * 2 + 1))), "Push the infinite list of odd numbers.");
		r("infinilist.pow2", stack -> stack.push(infiniteList.create(n -> literal.create(new BigInteger("2").pow(n + 1).toString()))), "Push the infinite list of powers of two.");
		r("infinilist.sign", stack -> stack.push(infiniteList.create(n -> literal.create(n % 2 == 0 ? 1 : -1))), "Push an infinite list of alternating positive and negative one.");

		r("infinilist.toFixed", stack -> stack.push(convertToFixed(stack.pop().asInt(), 0, stack.pop())), "Push a list with the size of the first stack value of values in the infinite list on the second stack value.");
		for (int size : SIZES) {
			r("infinilist.toFixed" + size, stack -> stack.push(convertToFixed(size, 0, stack.pop())), "Push a list with size " + size + " of values in the infinite list on the second stack value.");
		}
	}

	private List<Value> convertToFixed(int end, int start, Value val) {
		List<Value> list = val.iterate();
		return IntStream.range(start, end).mapToObj(list::get).collect(Collectors.toList());
	}

	private BigInteger fibonacci(int n) {
		if (n == 0 || n == 1) {
			return BigInteger.ONE;
		}
		if (fibonacci.containsKey(n)) {
			return fibonacci.get(n);
		}
		BigInteger result = fibonacci(n - 2).add(fibonacci(n - 1));
		if (n < 100) {
			fibonacci.put(n, result);
		}
		return result;
	}
}
