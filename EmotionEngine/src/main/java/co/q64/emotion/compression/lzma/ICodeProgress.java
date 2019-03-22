package co.q64.emotion.compression.lzma;

public interface ICodeProgress {
	public void SetProgress(long inSize, long outSize);
}
