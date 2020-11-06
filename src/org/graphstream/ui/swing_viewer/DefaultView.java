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
  
package org.graphstream.ui.swing_viewer;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.EnumSet;

import javax.swing.JFrame;
import javax.swing.ToolTipManager;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swing_viewer.util.DefaultMouseManager;
import org.graphstream.ui.swing_viewer.util.DefaultShortcutManager;
import org.graphstream.ui.swing_viewer.util.MouseOverMouseManager;
import org.graphstream.ui.view.GraphRenderer;
import org.graphstream.ui.view.LayerRenderer;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.util.InteractiveElement;
import org.graphstream.ui.view.util.MouseManager;
import org.graphstream.ui.view.util.ShortcutManager;

/**
 * Base for constructing views.
 * 
 * <p>
 * This base view is an abstract class that provides mechanism that are
 * necessary in any view :
 * <ul>
 * <li>the painting and repainting mechanism.</li>
 * <li>the optional frame handling.</li>
 * <li>the frame closing protocol.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * This view also handle a current selection of nodes and sprites.
 * </p>
 * 
 * <h3>The painting mechanism</h3>
 * 
 * <p>
 * The main method to implement is {@link #render(Graphics2D)}. This method is
 * called each time the graph needs to be rendered anew in the canvas.
 * </p>
 * 
 * <p>
 * The {@link #render(Graphics2D)} is called only when a repainting is really
 * needed.
 * </p>
 * 
 * <p>
 * All the painting, by default, is deferred to a {@link GraphRenderer}
 * instance. This mechanism allows developers that do not want to mess with the
 * viewer/view mechanisms to render a graph in any Swing surface.
 * </p>
 * 
 * <h3>The optional frame handling</h3>
 * 
 * <p>
 * This abstract view is able to create a frame that is added around this panel
 * (each view is a JPanel instance). The frame can be removed at any time.
 * </p>
 * 
 * <h3>The frame closing protocol</h3>
 * 
 * <p>
 * This abstract view handles the closing protocol. This means that it will
 * close the view if needed, or only hide it to allow reopening it later.
 * Furthermore it adds the "ui.viewClosed" attribute to the graph when the view
 * is closed or hidden, and removes it when the view is shown. The value of this
 * graph attribute is the identifier of the view.
 * </p>
 */
public class DefaultView extends ViewPanel implements WindowListener, ComponentListener {
	private static final long serialVersionUID = -4489484861592064398L;

	private static final EnumSet<InteractiveElement> ALL_ELEMS
			= EnumSet.allOf(InteractiveElement.class);

	/**
	 * Parent viewer.
	 */
	protected Viewer viewer;

	/**
	 * The graph to render, shortcut to the viewers reference.
	 */
	protected GraphicGraph graph;

	/**
	 * The (optional) frame.
	 */
	protected JFrame frame;

	/**
	 * Manager for events with the keyboard.
	 */
	protected ShortcutManager shortcuts;

	/**
	 * Manager for events with the mouse.
	 */
	protected MouseManager mouseClicks;

	/**
	 * The graph renderer.
	 */
	protected GraphRenderer renderer;

	// Construction
	public DefaultView(Viewer viewer, String identifier, GraphRenderer renderer) {
		super(identifier);

		this.viewer = viewer;
		this.graph = viewer.getGraphicGraph();
		this.renderer = renderer;

		setOpaque(false);
		setDoubleBuffered(true);
		setMouseManager(null);
		setShortcutManager(null);
		renderer.open(graph, this);
	}

	// Access

	// Command

	public Camera getCamera() {
		return renderer.getCamera();
	}

	public void display(GraphicGraph graph, boolean graphChanged) {
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		checkTitle();
		Graphics2D g2 = (Graphics2D) g;
		render(g2);
	}

	protected void checkTitle() {
		if (frame != null) {
			String titleAttr = String.format("ui.%s.title", getIdView());
			String title = (String) graph.getLabel(titleAttr);

			if (title == null) {
				title = (String) graph.getLabel("ui.default.title");

				if (title == null)
					title = (String) graph.getLabel("ui.title");
			}

			if (title != null)
				frame.setTitle(title);
		}
	}

	public void close(GraphicGraph graph) {
		renderer.close();
		graph.setAttribute("ui.viewClosed", getIdView());

		shortcuts.release();
		mouseClicks.release();

		openInAFrame(false);
	}

	public void resizeFrame(int width, int height) {
		if (frame != null) {
			frame.setSize(width, height);
		}
	}

	public void openInAFrame(boolean on) {
		if (on) {
			if (frame == null) {
				frame = new JFrame("GraphStream");
				frame.setLayout(new BorderLayout());
				frame.add(this, BorderLayout.CENTER);
				frame.setSize(800, 600);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				frame.addWindowListener(this);
				frame.addComponentListener(this);
				frame.addKeyListener((DefaultShortcutManager)shortcuts);
			} else {
				frame.setVisible(true);
			}
		} else {
			if (frame != null) {
				frame.removeComponentListener(this);
				frame.removeWindowListener(this);
				frame.removeKeyListener((DefaultShortcutManager)shortcuts);
				frame.remove(this);
				frame.setVisible(false);
				frame.dispose();
			}
		}
	}

	/**
	 * Turns tool-tips on or off.
	 * They are off by default.
	 * This is most useful for tool-tips of individual graph elements (like nodes and edges),
	 * and it displays the hovered-over elements tooltip text,
	 * as taken from it's "ui.tooltip" attribute, if one is set.
	 * @param enabled whether tool-tips are enabled or disabled.
	 */
	public void setToolTips(final boolean enabled) {

		if (enabled) {
			ToolTipManager.sharedInstance().registerComponent(this);
		} else {
			ToolTipManager.sharedInstance().unregisterComponent(this);
		}
	}

	@Override
	public String getToolTipText(final MouseEvent evt) {
		String text = null;

		final GraphicElement graphElement = findGraphicElementAt(ALL_ELEMS, evt.getX(), evt.getY());
		if (graphElement != null) {
			text = graphElement.getAttribute("ui.tooltip", String.class);
		}

		if (text == null) {
			text = graph.getAttribute("ui.tooltip", String.class);
		}

		if (text == null) {
			text = super.getToolTipText(evt);
		}

		return text;
	}

	@Override
	public Point getToolTipLocation(final MouseEvent evt) {
		final Point p = evt.getPoint();
		p.y += 15;
		return p;
	}

	public void render(Graphics2D g) {
		// this might only be needed to fix a bug in JetBrains IntelliJ:
		// Even if this JComponent is part of a complex AWT layout, getX() and getY() always returns (0,0) - even if
		// the Graphics2D tranformation is not the identity (e.g. performs a translation). This leads to wrong
		// calculations in the camera/renderer, thus we must obtain the transformed "origin" to be consistent.
		Point2D.Double origin = new Point2D.Double(0,0);
		Point2D.Double topLeft = new Point2D.Double();
		g.getTransform().transform(origin, topLeft);
		
		renderer.render(g, (int)topLeft.getX(), (int)topLeft.getY(), getWidth(), getHeight());
		String screenshot = (String) graph.getLabel("ui.screenshot");

		if (screenshot != null) {
			graph.removeAttribute("ui.screenshot");
			renderer.screenshot(screenshot, getWidth(), getHeight());
		}
	}

	// Selection

	public void beginSelectionAt(double x1, double y1) {
		renderer.beginSelectionAt(x1, y1);
		repaint();
	}

	public void selectionGrowsAt(double x, double y) {
		renderer.selectionGrowsAt(x, y);
		repaint();
	}

	public void endSelectionAt(double x2, double y2) {
		renderer.endSelectionAt(x2, y2);
		repaint();
	}

	// Window Listener

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		graph.setAttribute("ui.viewClosed", getIdView());

		switch (viewer.getCloseFramePolicy()) {
		case CLOSE_VIEWER:
			viewer.removeView(getIdView());
			break;
		case HIDE_ONLY:
			if (frame != null)
				frame.setVisible(false);
			break;
		case EXIT:
			System.exit(0);
		default:
			throw new RuntimeException(String.format("The %s view is not up to date, do not know %s CloseFramePolicy.",
					getClass().getName(), viewer.getCloseFramePolicy()));
		}
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
		graph.removeAttribute("ui.viewClosed");
	}

	public void componentHidden(ComponentEvent e) {
		repaint();
	}

	public void componentMoved(ComponentEvent e) {
		repaint();
	}

	public void componentResized(ComponentEvent e) {
		repaint();
	}

	public void componentShown(ComponentEvent e) {
		repaint();
	}

	// Methods deferred to the renderer

	@Override
	public Collection<GraphicElement> allGraphicElementsIn(EnumSet<InteractiveElement> types, double x1, double y1, double x2, double y2) {
		return renderer.allGraphicElementsIn(types, x1, y1, x2, y2);
	}

	@Override
	public GraphicElement findGraphicElementAt(EnumSet<InteractiveElement> types, double x, double y) {
		return renderer.findGraphicElementAt(types,x, y);
	}


	public void moveElementAtPx(GraphicElement element, double x, double y) {
		// The feedback on the node positions is often off since not needed
		// and generating lots of events. We activate it here since the
		// movement of the node is decided by the viewer. This is one of the
		// only moment when the viewer really moves a node.
		boolean on = graph.feedbackXYZ();
		graph.feedbackXYZ(true);
		renderer.moveElementAtPx(element, x, y);
		graph.feedbackXYZ(on);
	}

	public void freezeElement(GraphicElement element, boolean frozen) {
		if (frozen) {
			element.setAttribute("layout.frozen");
		} else {
			element.removeAttribute("layout.frozen");
		}
	}

	public void setBackLayerRenderer(LayerRenderer<Graphics2D> renderer) {
		this.renderer.setBackLayerRenderer(renderer);
		repaint();
	}

	public void setForeLayoutRenderer(LayerRenderer<Graphics2D> renderer) {
		this.renderer.setForeLayoutRenderer(renderer);
		repaint();
	}

	public void setMouseManager(MouseManager manager) {
		if (mouseClicks != null)
			mouseClicks.release();

		if (manager == null)
			manager = new DefaultMouseManager();

		manager.init(graph, this);

		mouseClicks = manager;
	}
	
	/**
	 * This is a shortcut to a call setMouseManager with a MouseOverMouseManager instance and with
	 * (InteractiveElement.EDGE, InteractiveElement.NODE, InteractiveElement.SPRITE).
	 */
	public void enableMouseOptions() {
		setMouseManager(new MouseOverMouseManager(EnumSet.of(InteractiveElement.EDGE, InteractiveElement.NODE, InteractiveElement.SPRITE)));
	}

	public void setShortcutManager(ShortcutManager manager) {
		if (shortcuts != null)
			shortcuts.release();

		if (manager == null)
			manager = new DefaultShortcutManager();

		manager.init(graph, this);

		shortcuts = manager;
	}
	
	@Override
	public Object requireFocus() {
		requestFocus();
		return null;
	}
	
	@Override
	public <T, U> void addListener(T descriptor, U listener) {
		String description = (String)descriptor ;
		
		if (description.equals("Mouse")) {
			MouseListener mouse = (MouseListener) listener ;
			addMouseListener(mouse);
		}
		else if(description.equals("MouseMotion")) {
			MouseMotionListener mouse = (MouseMotionListener) listener ;
			addMouseMotionListener(mouse);
		}
		else if(description.equals("Key")) {
			KeyListener mouse = (KeyListener) listener ;
			addKeyListener(mouse);
		}
		else {
			throw new RuntimeException("Listener unknown !");
		}
	}
	
	@Override
	public <T, U> void removeListener(T descriptor, U listener) {
		String description = (String)descriptor ;
		
		if (description.equals("Mouse")) {
			MouseListener mouse = (MouseListener) listener ;
			removeMouseListener(mouse);
		}
		else if(description.equals("MouseMotion")) {
			MouseMotionListener mouse = (MouseMotionListener) listener ;
			removeMouseMotionListener(mouse);
		}
		else if(description.equals("Key")) {
			KeyListener mouse = (KeyListener) listener ;
			removeKeyListener(mouse);
		}
		else {
			throw new RuntimeException("Listener unknown !");
		}
	}
}