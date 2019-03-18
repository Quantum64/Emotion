package co.q64.emotion.compression.png;

/**
 * Image Line factory.
 */
public interface IImageLineFactory<T extends IImageLine> {
	public T createImageLine(ImageInfo iminfo);
}
