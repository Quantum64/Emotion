package co.q64.emotion.lang.value.graphics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import co.q64.emotion.compression.png.IImageLine;
import co.q64.emotion.compression.png.IImageLineSet;
import co.q64.emotion.compression.png.ImageInfo;
import co.q64.emotion.compression.png.ImageLineHelper;
import co.q64.emotion.compression.png.ImageLineInt;
import co.q64.emotion.compression.png.PngHelperInternal;
import co.q64.emotion.compression.png.PngReader;
import co.q64.emotion.compression.png.PngWriter;
import co.q64.emotion.factory.ColorFactoryFactory;
import co.q64.emotion.lang.value.Value;
import co.q64.emotion.types.Comparison;
import co.q64.emotion.types.Operation;
import co.q64.emotion.util.Base64;
import co.q64.emotion.util.Color;
import lombok.Getter;

@AutoFactory
public class Image implements Value {
	private @Getter int width, height, alpha = 255;
	private @Getter int[][] pixels;
	private Color color;

	protected Image(@Provided ColorFactoryFactory colorFactory, int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = new int[height][width];
		this.color = colorFactory.getFactory().create(0, 0, 0);
	}

	protected Image(@Provided ColorFactoryFactory colorFactory, String source) {
		String header = "data:image/png;base64,";
		if (!source.startsWith(header)) {
			throw new IllegalArgumentException("Value not a valid Base64 PNG image.");
		}
		byte[] data = Base64.decode(source.substring(header.length()));
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		PngReader reader = new PngReader(stream);
		IImageLineSet<? extends IImageLine> lines = reader.readRows();
		ImageInfo info = reader.getImgInfo();
		this.width = info.cols;
		this.height = info.rows;
		this.pixels = new int[height][width];
		this.color = colorFactory.getFactory().create(0, 0, 0);
		for (int y = 0; y < height; y++) {
			ImageLineInt line = (ImageLineInt) lines.getImageLine(y);
			int[] scanline = line.getScanline();
			for (int x = 0; x < width; x++) {
				pixels[y][x] = scanline[x];
			}
		}
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

	public Image fillRect(int x, int y, int width, int height) {
		x = checkX(x);
		y = checkY(y);
		int xf = checkX(x + width);
		int yf = checkY(y + height);
		for (int cx = x; cx <= xf; cx++) {
			for (int cy = y; cy < yf; cy++) {
				setRGB(cx, cy);
			}
		}
		return this;
	}

	public Image fill() {
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				setRGB(x, y, color);
			}
		}
		return this;
	}

	public Image setColor(Color color) {
		this.color = color;
		return this;
	}

	public Image setAlpha(int alpha) {
		this.alpha = alpha;
		return this;
	}

	public int checkX(int x) {
		return x < 0 ? 0 : x >= width ? width - 1 : x;
	}

	public int checkY(int y) {
		return y < 0 ? 0 : y >= height ? height - 1 : y;
	}

	public void setRGBA(int x, int y, int a) {
		setRGBA(x, y, color, a);
	}

	public void setRGBA(int x, int y, Color color, int a) {
		setRGBA(x, y, color, a);
	}

	public void setRGBA(int x, int y, int r, int g, int b, int a) {
		pixels[y][x] = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
	}

	public void setRGB(int x, int y) {
		setRGB(x, y, color);
	}

	public void setRGB(int x, int y, Color color) {
		setRGB(x, y, color.getR(), color.getG(), color.getB());
	}

	public void setRGB(int x, int y, int r, int g, int b) {
		setRGBA(x, y, r, g, b, alpha);
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
		return Collections.emptyList();
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

	@Override
	public boolean isList() {
		return false;
	}
}
