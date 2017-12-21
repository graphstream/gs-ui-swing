package org.graphstream.ui.swing.util;

import java.awt.Color;

import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.stylesheet.Style;

/**
 * Static class used for translate awt color to javafx
 */
public class ColorManager {
	/**
	 * Get fill awt color in styleGroup and convert to javafx Color
	 * @param stylegroup
	 * @param id
	 * @return javafx.scene.paint.Color
	 */
	public static Color getFillColor(StyleGroup group, int id) {
		return getColor(group.getFillColor(id));
	}
	
	/**
	 * Get fill awt color in group and convert to javafx Color
	 * @param style
	 * @param id
	 * @return javafx.scene.paint.Color
	 */
	public static Color getFillColor(Style group, int id) {
		return getColor(group.getFillColor(id));
	}
	
	/**
	 * Get stroke awt color in styleGroup and convert to javafx Color
	 * @param stylegroup
	 * @param id
	 * @return javafx.scene.paint.Color
	 */
	public static Color getStrokeColor(StyleGroup group, int id) {
		return getColor(group.getStrokeColor(id));
	}
	
	/**
	 * Get stroke awt color in group and convert to javafx Color
	 * @param style
	 * @param id
	 * @return javafx.scene.paint.Color
	 */
	public static Color getStrokeColor(Style group, int id) {
		return getColor(group.getStrokeColor(id));
	}
	
	/**
	 * Get canvas awt color in styleGroup and convert to javafx Color
	 * @param stylegroup
	 * @param id
	 * @return javafx.scene.paint.Color
	 */
	public static Color getCanvasColor(StyleGroup group, int id) {
		return getColor(group.getCanvasColor(id));
	}
	
	/**
	 * Get shadow awt color in styleGroup and convert to javafx Color
	 * @param styleGroup
	 * @param id
	 * @return javafx.scene.paint.Color
	 */
	public static Color getShadowColor(StyleGroup group, int id) {
		return getColor(group.getShadowColor(id));
	}
	
	/**
	 * Get shadow awt color in group and convert to javafx Color
	 * @param style
	 * @param id
	 * @return javafx.scene.paint.Color
	 */
	public static Color getShadowColor(Style group, int id) {
		return getColor(group.getShadowColor(id));
	}

	/**
	 * Get text awt color in group and convert to javafx Color
	 * @param style
	 * @param id
	 * @return javafx.scene.paint.Color
	 */
	public static Color getTextColor(Style group, int id) {
		return getColor(group.getTextColor(id));
	}
	
	/**
	 * Get text background awt color in group and convert to javafx Color
	 * @param style
	 * @param id
	 * @return javafx.scene.paint.Color
	 */
	public static Color getTextBackgroundColor(Style group, int id) {
		return getColor(group.getTextBackgroundColor(id));
	}

	/**
	 * Convert java.awt.Color to javafx.scene.paint.Color
	 * @param java.awt.Color 
	 * @return javafx.scene.paint.Color
	 */
	public static Color getColor(org.graphstream.ui.graphicGraph.stylesheet.Color gscoreColor) {
		int r = gscoreColor.getRed();
		int g = gscoreColor.getGreen();
		int b = gscoreColor.getBlue();
		int a = gscoreColor.getAlpha();
		
		return new Color(r, g, b, a);
	}
}
