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
import java.awt.Stroke;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.swing.renderer.shape.swing.ShapePaint;
import org.graphstream.ui.swing.renderer.shape.swing.ShapeStroke;
import org.graphstream.ui.swing.util.ColorManager;

public class FillableLine {
	ShapeStroke fillStroke = null ;
	double theFillPercent = 0.0 ;
	Color theFillColor = null ;
	boolean plainFast = false ;
  
	public void fill(Graphics2D g, double width, double dynColor, java.awt.Shape shape) {
		if(fillStroke != null) {
		    if(plainFast) {
				g.setColor(theFillColor);
		        g.draw(shape);
		    }
		    else {
				Stroke stroke = fillStroke.stroke(width);
   
				g.setColor(theFillColor);
				g.setStroke(stroke);
				g.draw(shape);
			}
		}
	}
 
	public void fill(Graphics2D g, double width, java.awt.Shape shape) { fill(g, width, theFillPercent, shape); }
 
	public void configureFillableLineForGroup(Backend bck, Style style, DefaultCamera2D camera, double theSize) {
		fillStroke = ShapeStroke.strokeForConnectorFill( style );
  	  	plainFast = (style.getSizeMode() == StyleConstants.SizeMode.NORMAL); 
		theFillColor = ColorManager.getFillColor(style, 0);
		bck.graphics2D().setColor(theFillColor);
		if(fillStroke != null)
			bck.graphics2D().setStroke(fillStroke.stroke(theSize));
	}

	public void configureFillableLineForElement( Style style, DefaultCamera2D camera, GraphicElement element ) {
		theFillPercent = 0 ;
  	  	if( style.getFillMode() == StyleConstants.FillMode.DYN_PLAIN && element != null ) {
  	  		
	  	  	if ( element.getAttribute( "ui.color" ) instanceof Number ) {
  	  			theFillPercent = (float)((Number)element.getAttribute( "ui.color" ));
  	  			theFillColor = ShapePaint.interpolateColor( style.getFillColors(), theFillPercent ) ;
  	  		}
  	  		else if ( element.getAttribute( "ui.color" ) instanceof Color ) {
  	  			theFillColor = ((Color)element.getAttribute( "ui.color" )); 
  	  			theFillPercent = 0;	
  	  		}
  	  		else {
  	  			theFillPercent = 0f;
  	  			theFillColor = ColorManager.getFillColor(style, 0);
  	  		}
	  	  	
  	  		plainFast = false;
  	  	}
	}
}