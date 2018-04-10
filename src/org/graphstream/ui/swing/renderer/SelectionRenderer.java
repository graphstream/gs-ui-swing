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
  
package org.graphstream.ui.swing.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.swing.util.Selection;

public class SelectionRenderer {
	
	private Selection selection;
	private GraphicGraph graph;
	
	protected Rectangle2D.Double shape = new Rectangle2D.Double();
	protected Color linesColor   = new Color(240, 240, 240);
	protected Color linesColorQ  = new Color(  0,   0,   0, 64);
	protected Color fillColor    = new Color( 50,  50, 200, 32);
	protected Color strokeColorQ = new Color( 50,  50, 200, 64);
	protected Color strokeColor  = new Color(128, 128, 128);
			
	public SelectionRenderer(Selection selection, GraphicGraph graph) {
		this.selection = selection ;
		this.graph = graph ;
	}
	
	public void render(Backend bck, DefaultCamera2D camera, int panelWidth, int panelHeight) {
	    // XXX
	    // TODO make this an abstract class whose implementation are create by the back-end
	    // XXX
		if(selection.isActive() && selection.x1() != selection.x2() && selection.y1() != selection.y2()) {
			Graphics2D g = bck.graphics2D();
			boolean quality = (graph.hasAttribute("ui.quality") || graph.hasAttribute("ui.antialias"));
			double x1 = selection.x1();
			double y1 = selection.y1();
			double x2 = selection.x2();
			double y2 = selection.y2();
			double t = 0.0;
			
			if(x1 > x2) { t = x1; x1 = x2; x2 = t; }
			if(y1 > y2) { t = y1; y1 = y2; y2 = t; }
   
			if(quality)
			     g.setColor(linesColorQ);
			else g.setColor(linesColor) ;

			g.setStroke(new BasicStroke(1));
   
			g.drawLine(0,(int) y1, panelWidth,(int) y1);
			g.drawLine(0, (int)y2, panelWidth,(int) y2);
			g.drawLine((int)x1, 0, (int)x1, panelHeight);
			g.drawLine((int)x2, 0, (int)x2, panelHeight);
	
			shape.setFrame(x1, y1, x2-x1, y2-y1);
			
			if(quality) {
				g.setColor(fillColor);
				g.fill(shape);
				g.setColor(strokeColorQ);
			}
			else {
				g.setColor(strokeColor);
			}
   
			g.draw(shape);
		}
	}
}
