package com.xsheetgames.genericElements;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * object-fit:cover fuer Sprites - skaliert uniform bis der Viewport komplett
 * gefuellt ist und schneidet den Ueberstand ab. Einheiten-agnostisch: Pixel
 * fuer die Menu-Screens, Meter fuer die Parallax-Layer.
 */
public final class CoverLayout {

	public static final float LEFT = 0f;
	public static final float BOTTOM = 0f;
	public static final float CENTER = 0.5f;
	public static final float RIGHT = 1f;
	public static final float TOP = 1f;

	private CoverLayout() {
	}

	public static void apply(Sprite s, float nativeW, float nativeH, float viewW, float viewH) {
		apply(s, nativeW, nativeH, viewW, viewH, CENTER, CENTER);
	}

	public static void apply(Sprite s, float nativeW, float nativeH,
							 float viewW, float viewH, float anchorX, float anchorY) {
		float scale = Math.max(viewW / nativeW, viewH / nativeH);
		float w = nativeW * scale;
		float h = nativeH * scale;
		s.setSize(w, h);
		s.setPosition((viewW - w) * anchorX, (viewH - h) * anchorY);
	}
}