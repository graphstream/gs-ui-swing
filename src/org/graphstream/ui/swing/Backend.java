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
