package co.q64.emotion.lang.value.math;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import co.q64.emotion.lang.value.Value;
import co.q64.emotion.lang.value.Values;
import co.q64.emotion.util.Rational;
import org.ejml.data.DMatrixRMaj;
import org.ejml.simple.SimpleMatrix;

import com.google.auto.factory.AutoFactory;

import co.q64.emotion.types.Comparison;
import co.q64.emotion.types.Operation;
import sun.java2d.pipe.SpanShapeRenderer.Simple;

public class Matrix extends SimpleMatrix implements Value {
    private static final long serialVersionUID = 1L;

    private Matrix(Value value) {
        if (value instanceof Matrix) {
            setMatrix(((Matrix) value).getMatrix());
            return;
        }
        List<Value> values = value.iterate();
        int rowLen = 1;
        for (Value v : value.iterate()) {
            if (v.isList()) {
                rowLen = Math.max(rowLen, v.iterate().size());
            }
        }
        double[][] data = new double[values.size()][rowLen];
        for (int i = 0; i < values.size(); i++) {
            Value v = values.get(i);
            if (v.isList()) {
                List<Value> rowData = v.iterate();
                for (int rowIndex = 0; rowIndex < rowData.size(); rowIndex++) {
                    data[i][rowIndex] = rowData.get(rowIndex).asDouble();
                }
            } else {
                data[i][0] = v.asDouble();
            }
        }
        setMatrix(new DMatrixRMaj(data));
    }

    private Matrix(double[][] data) {
        super(data);
    }

    private Matrix(SimpleMatrix matrix) {
        setMatrix(matrix.getMatrix());
    }

    public static Matrix of(double[][] data) {
        return new Matrix(data);
    }

    public static Matrix of(SimpleMatrix matrix) {
        return new Matrix(matrix);
    }

    public static Matrix of(Value value) {
        return new Matrix(value);
    }

    @Override
    public String toString() {
        List<String> results = new ArrayList<>();
        double[][] data = new double[numRows()][numCols()];
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {
                data[row][col] = get(row, col);
            }
        }
        for (double[] row : data) {
            results.add("[" + DoubleStream.of(row).mapToObj(String::valueOf).collect(Collectors.joining(",")) + "]");
        }
        return "[" + results.stream().collect(Collectors.joining(",")) + "]";
    }

    @Override
    public List<Value> iterate() {
        List<Value> result = new ArrayList<>();
        double[][] data = new double[numRows()][numCols()];
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {
                data[row][col] = get(row, col);
            }
        }
        for (double[] row : data) {
            result.add(Values.create(DoubleStream.of(row).mapToObj(String::valueOf).collect(Collectors.toList())));
        }
        return result;
    }

    @Override
    public Value operate(Value value, Operation type) {
        switch (type) {
            case MULTIPLY:
                if (value instanceof Matrix) {
                    return new Matrix(mult((Matrix) value));
                }
                return new Matrix(scale(value.asDouble()));
            default:
                return this;
        }
    }

    @Override
    public int compareTo(Value value) {
        if (value instanceof Matrix) {
            // TODO
        }
        return -1;
    }

    @Override
    public Rational asNumber() {
        return Rational.of(determinant());
    }

    @Override
    public boolean asBoolean() {
        return determinant() != 0;
    }
}
