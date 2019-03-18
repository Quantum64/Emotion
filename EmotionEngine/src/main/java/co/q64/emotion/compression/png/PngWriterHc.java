package co.q64.emotion.compression.png;

import java.io.File;
import java.io.OutputStream;

import co.q64.emotion.compression.png.pixels.PixelsWriter;
import co.q64.emotion.compression.png.pixels.PixelsWriterMultiple;

/** Pngwriter with High compression EXPERIMENTAL */
public class PngWriterHc extends PngWriter {

	public PngWriterHc(File file, ImageInfo imgInfo, boolean allowoverwrite) {
		super(file, imgInfo, allowoverwrite);
		setFilterType(FilterType.FILTER_SUPER_ADAPTIVE);
	}

	public PngWriterHc(File file, ImageInfo imgInfo) {
		super(file, imgInfo);
	}

	public PngWriterHc(OutputStream outputStream, ImageInfo imgInfo) {
		super(outputStream, imgInfo);
	}

	@Override
	protected PixelsWriter createPixelsWriter(ImageInfo imginfo) {
		PixelsWriterMultiple pw = new PixelsWriterMultiple(imginfo);
		return pw;
	}

	public PixelsWriterMultiple getPixelWriterMultiple() {
		return (PixelsWriterMultiple) pixelsWriter;
	}

}
