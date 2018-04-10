/*
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */

 /**
  * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
  * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
  * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
  */
  
package org.graphstream.ui.swing.util;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.TextStyle;

public class FontCache {
	
	protected static Font defFont = new Font("SansSerif", Font.PLAIN, 11);
	
	protected static HashMap<String, FontSlot> cache = new HashMap<>();
	
	public static Font getDefaultFont() {
		return defFont;
	}
	
	public static Font getDefaultFont(TextStyle style, int size) {
		return getFont( "SansSerif", style, size );
	}

	public static Font getFont(String name, TextStyle style, int size) {
		if ( cache.get(name) == null ) {
			FontSlot slot = new FontSlot(name, style, size);
			cache.put(name, slot);
			return slot.getFont(style, size);
		}
		else {
			return cache.get(name).getFont(style, size);
		}
	}
}

class FontSlot {
	
	HashMap<Integer, Font> normal = null ;
	HashMap<Integer, Font> bold = null ;
	HashMap<Integer, Font> italic = null ;
	HashMap<Integer, Font> boldItalic = null ;
	
	private String name ;
	
	public FontSlot(String name, TextStyle style, int size) {
		this.name = name ;
		insert(style, size);
	}

	private Font insert(TextStyle style, int size) {
		return insert(mapFromStyle(style), toJavaStyle(style), size);
	}

	private Font insert(Map<Integer, Font> map, int style, int size) {
		if (map.get(size) == null) {
			Font font = new Font(name, style, size);
			map.put(size, font);
			return font ;
		}
		else {
			return map.get(size);
		}
	}
	
	public Font getFont(TextStyle style, int size) {
		Map<Integer, Font> map = mapFromStyle(style);
		
		if (map.get(size) == null) {
			return insert( map, toJavaStyle( style ), size );
		}
		else {
			return map.get(size);
		}
	}

	private int toJavaStyle(TextStyle style) {
		switch (style) {
		case BOLD:
			return Font.BOLD;
		case ITALIC:
			return Font.ITALIC;
		case BOLD_ITALIC:
			return Font.BOLD + Font.ITALIC;
		case NORMAL:
			return Font.PLAIN;
		default:
			return Font.PLAIN;
		}
	}

	private Map<Integer, Font> mapFromStyle(TextStyle style) {
		switch (style) {
			case BOLD:
				if (bold == null) {
					bold = new HashMap<Integer, Font>();
				}
				return bold ;
			case ITALIC:
				if (italic == null) {
					italic = new HashMap<Integer, Font>();
				}
				return italic ;
			case BOLD_ITALIC:
				if (boldItalic == null) {
					boldItalic = new HashMap<Integer, Font>();
				}
				return boldItalic ;
			case NORMAL:
				if (normal == null) {
					normal = new HashMap<Integer, Font>();
				}
				return normal ;
			default:
				if (normal == null) {
					normal = new HashMap<Integer, Font>();
				}
				return normal ;
		}
	}
}