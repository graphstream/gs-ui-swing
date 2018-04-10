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
  
package org.graphstream.ui.swing.renderer.shape.swing.shapePart;

import java.awt.Color;
import java.awt.Graphics2D;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.swing.renderer.shape.swing.ShapePaint;
import org.graphstream.ui.swing.renderer.shape.swing.ShapePaint.ShapeAreaPaint;
import org.graphstream.ui.swing.renderer.shape.swing.ShapePaint.ShapeColorPaint;
import org.graphstream.ui.swing.renderer.shape.swing.ShapePaint.ShapePlainColorPaint;

public class Fillable {
	
	/** The fill paint. */
	ShapePaint fillPaint = null;
 
	/** Value in [0..1] for dyn-colors. */
	double theFillPercent = 0.0;
	
	Color theFillColor = null;
	
	boolean plainFast = false;
	
	/** Fill the shape.
	 * @param g The Java2D graphics.
	 * @param dynColor The value between 0 and 1 allowing to know the dynamic plain color, if any.
	 * @param shape The awt shape to fill. */
	public void fill(Graphics2D g, double dynColor, Color optColor, java.awt.Shape shape, DefaultCamera2D camera) {
		if(plainFast) {
			g.setColor(theFillColor);
			g.fill(shape);
	    } 
		else {
			if ( fillPaint instanceof ShapeAreaPaint ) {
				g.setPaint(((ShapeAreaPaint)fillPaint).paint(shape, camera.getMetrics().ratioPx2Gu));   
				g.fill(shape);
			}
			else if (fillPaint instanceof ShapeColorPaint ) {
				g.setPaint(((ShapeColorPaint)fillPaint).paint(dynColor, optColor));
				g.fill(shape);
			}
	    }
	}
	
	/** Fill the shape.
	 * @param g The Java2D graphics.
	 * @param shape The awt shape to fill. */
 	public void fill(Graphics2D g, java.awt.Shape shape, DefaultCamera2D camera) { fill( g, theFillPercent, theFillColor, shape, camera ); }

    /** Configure all static parts needed to fill the shape. */
 	public void configureFillableForGroup(Backend bck, Style style, DefaultCamera2D camera ) {
 		fillPaint = ShapePaint.apply(style);
 
 		if(fillPaint instanceof ShapePlainColorPaint) {
 			ShapePlainColorPaint paint = (ShapePlainColorPaint)fillPaint;
 		    plainFast = true;
 		    theFillColor = paint.color;
 		    bck.graphics2D().setColor(theFillColor);
 		    // We prepare to accelerate the filling process if we know the color is not dynamic
 		    // and is plain: no need to change the paint at each new position for the shape.
 		} 
 		else {
 		    plainFast = false;
 		}
 	}
 	
    /** Configure the dynamic parts needed to fill the shape. */
  	public void configureFillableForElement( Style style, DefaultCamera2D camera, GraphicElement element ) {
  	  	if( style.getFillMode() == StyleConstants.FillMode.DYN_PLAIN && element != null ) {
  	  		if ( element.getAttribute( "ui.color" ) instanceof Number ) {
  	  			theFillPercent = (float)((Number)element.getAttribute( "ui.color" ));
  	  			theFillColor = null;
  	  		}
  	  		else if ( element.getAttribute( "ui.color" ) instanceof Color ) {
  	  			theFillColor = ((Color)element.getAttribute( "ui.color" )); 
  	  			theFillPercent = 0;
  	  		}
  	  		else {
  	  			theFillPercent = 0; 
  	  			theFillColor = null;
  	  		}
  	  	}
  	  	else {
  	  		theFillPercent = 0;
  	  	}
  	}
}