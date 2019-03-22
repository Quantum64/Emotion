package co.q64.emotion.compression;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.q64.emotion.compression.deflate.JavaDeflate;
import co.q64.emotion.compression.deflate.JavaInflate;
import co.q64.emotion.compression.lzma.UTF8;

@Singleton
public class Deflate {
	protected @Inject Deflate() {}

	public boolean canCompress(String str) {
		return str.length() > 25;
	}

	public byte[] compress(String str) {
		return compressBytes(UTF8.encode(str));
	}

	public String decompress(byte[] data) {
		return UTF8.decode(decompressBytes(data));
	}

	private byte[] compressBytes(byte[] data) {
		JavaDeflate deflate = new JavaDeflate();
		byte[] compressedBuffer = new byte[4096];
		deflate.setInput(data);
		deflate.finish();
		deflate.deflate(compressedBuffer, 0, compressedBuffer.length);
		byte[] result = new byte[(int) deflate.getBytesWritten()];
		System.arraycopy(compressedBuffer, 0, result, 0, result.length);
		return result;
	}

	private byte[] decompressBytes(byte[] data) {
		JavaInflate inflate = new JavaInflate();
		byte[] inflateBuffer = new byte[4096];
		inflate.setInput(data);
		inflate.inflate(inflateBuffer);
		byte[] result = new byte[(int) inflate.getBytesWritten()];
		System.arraycopy(inflateBuffer, 0, result, 0, result.length);
		return result;
	}
}
