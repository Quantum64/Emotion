package co.q64.emotion.compression.png;

import java.io.OutputStream;

public interface IPngWriterFactory {
	public PngWriter createPngWriter(OutputStream outputStream, ImageInfo imgInfo);
}
