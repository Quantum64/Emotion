package co.q64.emotion.runtime.system;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.annotations.GwtIncompatible;

import co.q64.emotion.annotation.GWT;
import co.q64.emotion.lang.value.Value;
import co.q64.emotion.runtime.Graphics;
import co.q64.emotion.util.Color;

// TODO maybe some day...
@Singleton
@GwtIncompatible(GWT.MESSAGE)
public class SystemGraphics implements Graphics {
	protected @Inject SystemGraphics() {}

	@Override
	public Value createImage(int x, int y, Color color) {
		return null;
	}

	@Override
	public Value createImage(String encoded) {
		return null;
	}

	@Override
	public void setColor(Value image, Color color) {

	}

	@Override
	public void setStroke(Value image, int stroke) {

	}

	@Override
	public void drawEllipse(Value image, int x, int y, int i, int j, boolean fill) {

	}

	@Override
	public void drawRectangle(Value image, int x, int y, int w, int h, boolean fill) {

	}

	@Override
	public void drawText(Value image, String text, int x, int y, boolean fill) {

	}

	@Override
	public int getWidth(Value image) {
		return 0;
	}

	@Override
	public int getHeight(Value image) {
		return 0;
	}
}
