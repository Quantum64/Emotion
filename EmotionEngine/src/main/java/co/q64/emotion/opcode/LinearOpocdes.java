package co.q64.emotion.opcode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.ejml.simple.SimpleMatrix;

import co.q64.emotion.lang.Stack;
import co.q64.emotion.lang.opcode.OpcodeMarker;
import co.q64.emotion.lang.opcode.OpcodeRegistry;
import co.q64.emotion.lang.value.Matrix;
import co.q64.emotion.lang.value.MatrixFactory;
import co.q64.emotion.lang.value.Value;

@Singleton
public class LinearOpocdes extends OpcodeRegistry {
	protected @Inject MatrixFactory matrix;

	protected @Inject LinearOpocdes() {
		super(OpcodeMarker.LINEAR);
	}

	@Override
	public void register() {
		r("vector.dot", stack -> stack.push(matrix.create(stack.peek(2)).dot(matrix.create(stack.pull(2)))), "Push the dot product of the second and first stack vectors.");

		r("matrix.det", stack -> stack.push(matrix(stack.pop()).determinant()));

		r("matrix.zeros", stack -> stack.push(genZeroMatrix(stack.pop().asInt())), "Push the zero matrix with width and height of the first stack value.");
		for (int i = 2; i < 10; i++) {
			final int lock = i;
			r("matrix.zeros" + i + "x" + i, stack -> stack.push(genZeroMatrix(lock)), "Push the " + i + "x" + i + " zero matrix.");
		}

		r("matrix.identity", stack -> stack.push(matrix.create(Matrix.identity(stack.pop().asInt()))), "Push the identity matrix with the width of the first stack value.");
		for (int i = 2; i < 10; i++) {
			final int lock = i;
			r("matrix.identity" + i + "x" + i, stack -> stack.push(matrix.create(Matrix.identity(lock))), "Push the " + i + "x" + i + " identity matrix.");
		}

		for (int i = 2; i < 10; i++) {
			final int lock = i;
			r("matrix." + i + "x" + i, stack -> stack.push(popSquareMatrix(stack, lock)), "Pop the first " + i * i + " stack values into a " + i + "x" + i + " matrix.");
		}
	}

	private Matrix matrix(SimpleMatrix m) {
		return matrix.create(m);
	}

	private Matrix matrix(Value v) {
		return matrix.create(v);
	}

	private Matrix genZeroMatrix(int size) {
		return genZeroMatrix(size, size);
	}

	private Matrix genZeroMatrix(int width, int height) {
		double[][] data = new double[height][width];
		return matrix.create(data);
	}

	private List<List<Double>> popSquareMatrix(Stack stack, int size) {
		return popMatrix(stack, size, size);
	}

	private List<List<Double>> popMatrix(Stack stack, int width, int height) {
		List<List<Double>> result = new ArrayList<>(height);
		for (int row = 0; row < height; row++) {
			result.add(popRow(stack, width));
		}
		return result;
	}

	private List<Double> popRow(Stack stack, int size) {
		List<Double> result = new ArrayList<Double>(size);
		for (int i = 0; i < size; i++) {
			result.add(stack.pop().asDouble());
		}
		return result;
	}
}
