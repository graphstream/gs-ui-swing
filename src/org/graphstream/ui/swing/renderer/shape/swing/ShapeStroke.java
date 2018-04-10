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
  
package org.graphstream.ui.swing.renderer.shape.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.swing.util.ColorManager;

public abstract class ShapeStroke {
	public abstract Stroke stroke(double width) ;

	public static ShapeStroke strokeForArea(Style style) {
		switch (style.getStrokeMode()) {
			case PLAIN: return new PlainShapeStroke();
			case DOTS: return new DotsShapeStroke();
			case DASHES: return new DashesShapeStroke();
			case DOUBLE: return new DoubleShapeStroke();
			default: return null ;
		}
	}
	
	public static ShapeStroke strokeForConnectorFill(Style style) {
		switch (style.getFillMode()) {
			case PLAIN: return new PlainShapeStroke();
			case DYN_PLAIN: return new PlainShapeStroke();
			case NONE: return null	; // Gracefully handled by the drawing part.
			default: return new PlainShapeStroke() ;
		}
	}
	
	public ShapeStroke strokeForConnectorStroke(Style style) {
		return strokeForArea(style);
	}
	
	public static Color strokeColor(Style style) {
		if( style.getStrokeMode() != org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.StrokeMode.NONE ) {
			return ColorManager.getStrokeColor( style, 0 );
		} 
		else {
			return null;
		}
	}
	
}

class PlainShapeStroke extends ShapeStroke {
	private double oldWidth = 0.0 ;
	private Stroke oldStroke = null ;
	
	@Override
	public Stroke stroke(double width) {
		if( width == oldWidth ) {
			if( oldStroke == null ) 
				oldStroke = new BasicStroke( (float)width/*, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL*/ )	;// WTF ??
			return oldStroke;
		} 
		else {
			oldWidth  = width;
			oldStroke = new BasicStroke( (float)width/*, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL*/ );
			return oldStroke;
		}
	}
}

class DotsShapeStroke extends ShapeStroke {
	private double oldWidth = 0.0 ;
	private Stroke oldStroke = null ;
	
	@Override
	public Stroke stroke(double width) {
		if( width == oldWidth ) {
			if( oldStroke == null ) {
				float[] f = {(float)width, (float)width} ;
				oldStroke = new BasicStroke( (float)width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, f, 0); // WTF ??
			}
			return oldStroke;
		} else {
			oldWidth = width;
			float[] f = {(float)width, (float)width} ;
			oldStroke = new BasicStroke( (float)width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, f, 0);
			return oldStroke;
		}
	}
}

class DashesShapeStroke extends ShapeStroke {
	private double oldWidth = 0.0 ;
	private Stroke oldStroke = null ;
	
	@Override
	public Stroke stroke(double width) {
		if( width == oldWidth ) {
			if( oldStroke == null ){
				float[] f = {(float)(3*width), (float)(3*width)};
				oldStroke = new BasicStroke( (float)width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, f, 0); // WTF ??
			}
			return oldStroke ;
		} else {
			oldWidth = width ;
			float[] f = {(float)(3*width), (float)(3*width)};
			oldStroke = new BasicStroke( (float)width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, f, 0);
			return oldStroke ;
		}
	}	
}

class DoubleShapeStroke extends ShapeStroke {
	private double oldWidth = 0.0 ;
	private Stroke oldStroke = null ;
	
	@Override
	public Stroke stroke(double width) {
		if(width == oldWidth) {
            if(oldStroke == null) 
            	oldStroke = new CompositeStroke(new BasicStroke((float)width*3), new BasicStroke((float)width));
            return oldStroke;
        } else {
            oldWidth = width;
            oldStroke = new CompositeStroke(new BasicStroke((float)width*2), new BasicStroke((float)width));
            return oldStroke;
        }
	}
	
	class CompositeStroke implements Stroke {
		private Stroke stroke1 ;
		private Stroke stroke2 ;
		
		public CompositeStroke(Stroke stroke1, Stroke stroke2) {
			this.stroke1 = stroke1 ;
			this.stroke2 = stroke2 ;
		}
    	public java.awt.Shape createStrokedShape(java.awt.Shape shape) {
    		return stroke2.createStrokedShape(stroke1.createStrokedShape(shape));
    	}
    }
}
