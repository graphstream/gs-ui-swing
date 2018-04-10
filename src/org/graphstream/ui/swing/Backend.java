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
  
package org.graphstream.ui.swing;

import java.awt.Container;
import java.awt.Graphics2D;

import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.swing.renderer.GraphBackgroundRenderer;
import org.graphstream.ui.swing.renderer.shape.Shape;

public interface Backend extends org.graphstream.ui.view.camera.Backend {
    
    /** Called before any prior use of this back-end. */
    void open(Container drawingSurface);
    
    /** Called after finished using this object. */
    void close();
    
    /** Setup the back-end for a new rendering session. */
    void prepareNewFrame(Graphics2D g2);

    /** The Java2D graphics. */
    Graphics2D graphics2D();
    
    Shape chooseNodeShape(Shape oldShape, StyleGroup group);
    Shape chooseEdgeShape(Shape oldShape, StyleGroup group); 
    Shape chooseEdgeArrowShape(Shape oldShape, StyleGroup group); 
    Shape chooseSpriteShape(Shape oldShape, StyleGroup group); 
    GraphBackgroundRenderer chooseGraphBackgroundRenderer();
    
    /** The drawing surface.
     * The drawing surface may be different than the one passed as
     * argument to open(), the back-end is free to create a new surface
     * as it sees fit. */
    Container drawingSurface();
}	
