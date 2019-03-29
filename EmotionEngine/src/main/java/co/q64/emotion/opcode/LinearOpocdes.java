package co.q64.emotion.opcode;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.ejml.simple.SimpleMatrix;

import co.q64.emotion.lang.opcode.OpcodeMarker;
import co.q64.emotion.lang.opcode.OpcodeRegistry;
import co.q64.emotion.lang.value.Value;

@Singleton
public class LinearOpocdes extends OpcodeRegistry {

	protected @Inject LinearOpocdes() {
		super(OpcodeMarker.LINEAR);
	}

	@Override
	public void register() {
		r("vector.dot", stack -> stack.push(vector(stack.peek(2)).dot(vector(stack.pull(2)))), "Push the dot product of the second and first stack vectors.");
	}
	
	private Value convert(SimpleMatrix sm) {
		return null;
	}

	private SimpleMatrix vector(Value value) {
		List<Value> values = value.iterate();
		double[][] data = new double[values.size()][1];
		for(int i = 0; i < values.size(); i++) {
			data[i][0] = values.get(i).asDouble();
		}
		return new SimpleMatrix(data);
	}
}
