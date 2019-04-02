package co.q64.emotion.lang;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import co.q64.emotion.factory.LiteralFactoryFactory;
import co.q64.emotion.factory.MatrixFactoryFactory;
import co.q64.emotion.lang.value.LiteralFactory;
import co.q64.emotion.lang.value.MatrixFactory;
import co.q64.emotion.lang.value.Null;
import co.q64.emotion.lang.value.Value;
import lombok.Getter;

@AutoFactory
public class Stack {
	private Null nul;
	private @Getter Program program;
	private @Getter List<Value> stack = new ArrayList<>();
	private LiteralFactory literal;
	private MatrixFactory matrix;

	protected @Inject Stack(@Provided Null nul, @Provided LiteralFactoryFactory literal, @Provided MatrixFactoryFactory matrix, Program program) {
		this.nul = nul;
		this.program = program;
		this.literal = literal.getFactory();
		this.matrix = matrix.getFactory();
	}

	public void dup(int depth) {
		for (int i = 0; i < depth; i++) {
			if (stack.size() > 0) {
				stack.add(stack.get(stack.size() - 1));
			}
		}
	}

	public void dup() {
		dup(1);
	}

	public Value pop(int depth) {
		Value result = nul;
		for (int i = 0; i < depth; i++) {
			if (stack.size() > 0) {
				result = stack.remove(stack.size() - 1);
			}
		}
		return result;
	}

	public Value pull(int depth) {
		Value result = nul;
		for (int i = 0; i < depth; i++) {
			if (stack.size() > 0) {
				Value buffer = stack.remove(stack.size() - 1);
				if (result == nul) {
					result = buffer;
				}
			}
		}
		return result;
	}

	public Value pop() {
		return pop(1);
	}

	public void clr() {
		stack.clear();
	}

	public Value peek(int depth) {
		if (stack.size() >= depth) {
			return stack.get(stack.size() - depth);
		}
		return nul;
	}

	public Value peek() {
		return peek(1);
	}

	public Stack swap() {
		if (stack.size() > 1) {
			Value buffer = stack.get(stack.size() - 1);
			stack.set(stack.size() - 1, stack.get(stack.size() - 2));
			stack.set(stack.size() - 2, buffer);
		}
		return this;
	}

	public int size() {
		return stack.size();
	}

	public Stack push(Value value) {
		add(value);
		return this;
	}

	public Stack push(Object value) {
		add(literal.create(value.toString()));
		return this;
	}

	public Stack push(List<?> values) {
		add(literal.create(values));
		return this;
	}

	private void add(Value value) {
		if (value.isList()) {
			boolean isMatrix = true;
			double[][] data = null;
			int rowLen = 0;
			List<Value> rows = value.iterate();
			for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
				Value row = rows.get(rowIndex);
				if (!row.isList()) {
					isMatrix = false;
					break;
				}
				List<Value> rowData = row.iterate();
				if (data == null) {
					rowLen = rowData.size();
					data = new double[rows.size()][rowLen];
				}
				if (rowData.size() != rowLen) {
					isMatrix = false;
					break;
				}
				for (int colIndex = 0; colIndex < rowData.size(); colIndex++) {
					Value rowVal = rowData.get(colIndex);
					if (!rowVal.isFloat()) {
						isMatrix = false;
						break;
					}
					data[rowIndex][colIndex] = rowVal.asDouble();
				}
			}
			if(isMatrix) {
				stack.add(matrix.create(value));
				return;
			}
		}
		stack.add(value);
	}
}
