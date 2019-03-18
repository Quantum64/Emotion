package co.q64.emotion.lang.value.graphics;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import com.google.auto.factory.AutoFactory;

import co.q64.emotion.compression.png.ImageInfo;
import co.q64.emotion.compression.png.ImageLineHelper;
import co.q64.emotion.compression.png.ImageLineInt;
import co.q64.emotion.compression.png.PngHelperInternal;
import co.q64.emotion.compression.png.PngWriter;
import co.q64.emotion.lang.value.Value;
import co.q64.emotion.types.Comparison;
import co.q64.emotion.types.Operation;
import co.q64.emotion.util.Base64;
import co.q64.emotion.util.Color;
import lombok.Getter;

@AutoFactory
public class Image implements Value {
	private @Getter int width, height;
	private @Getter int[][] pixels;

	protected Image(int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = new int[height][width];
	}

	@Override
	public boolean compare(Value value, Comparison type) {
		if (value instanceof Image) {
			Image target = (Image) value;
			if (type == Comparison.EQUAL) {
				if (Arrays.deepEquals(pixels, target.getPixels())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		PngHelperInternal.charsetLatin1name = "UTF-8";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageInfo imageInfo = new ImageInfo(width, height, 8, true);
		PngWriter png = new PngWriter(outputStream, imageInfo);
		for (int y = 0; y < png.imgInfo.rows; y++) {
			ImageLineInt line = new ImageLineInt(imageInfo);
			for (int x = 0; x < imageInfo.cols; x++) {
				ImageLineHelper.setPixelRGBA8(line, x, getR(x, y), getG(x, y), getB(x, y), getA(x, y));
			}
			png.writeRow(line);
		}
		png.end();
		try {
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
		return "data:image/png;base64," + Base64.encode(outputStream.toByteArray());
	}

	public void fill(Color color) {
		int r = color.getR(), g = color.getG(), b = color.getB();
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				setRGB(x, y, r, g, b);
			}
		}
	}

	public void setRGBA(int x, int y, int r, int g, int b, int a) {
		pixels[y][x] = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
	}

	public void setRGB(int x, int y, int r, int g, int b) {
		setRGBA(x, y, r, g, b, 255);
	}

	public int getR(int x, int y) {
		return (pixels[y][x] >> 16) & 0xFF;
	}

	public int getG(int x, int y) {
		return (pixels[y][x] >> 8) & 0xFF;
	}

	public int getB(int x, int y) {
		return (pixels[y][x]) & 0xFF;
	}

	public int getA(int x, int y) {
		return (pixels[y][x] >> 24) & 0xFF;
	}

	@Override
	public Value operate(Value value, Operation type) {
		return this;
	}

	@Override
	public List<Value> iterate() {
		return null;
	}

	@Override
	public int asInt() {
		return 0;
	}

	@Override
	public long asLong() {
		return 0;
	}

	@Override
	public char asChar() {
		return 0;
	}

	@Override
	public double asDouble() {
		return 0;
	}

	@Override
	public boolean asBoolean() {
		return false;
	}

	@Override
	public boolean isBoolean() {
		return false;
	}

	@Override
	public boolean isFloat() {
		return false;
	}

	@Override
	public boolean isInteger() {
		return false;
	}
}
