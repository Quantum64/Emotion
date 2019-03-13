package co.q64.emotion.compression.lzma.impl;

public interface ICodeProgress {
	public void SetProgress(long inSize, long outSize);
}
