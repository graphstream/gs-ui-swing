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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.Values;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing.renderer.shape.swing.basicShapes.SquareShape;
import org.graphstream.ui.swing.util.ColorManager;
import org.graphstream.ui.swing.util.FontCache;
import org.graphstream.ui.swing.util.ImageCache;
import org.graphstream.ui.view.util.GraphMetrics;

public class JComponentRenderer extends StyleRenderer {
	
	private SwingGraphRenderer mainRenderer;
	private StyleGroup styleGroup;
	
	/** The size of components. */
	protected Values size = null;
	
	/** The size in PX of components. */
	protected int width = 0;
	
	/** The size in PX of components. */
 	protected int height = 0;
	
	/** Association between Swing components and graph elements. */
	protected HashMap<JComponent, ComponentElement> compToElement = new HashMap<>();

	/** The potential shadow. */
	protected SquareShape shadow = null;
 
	protected Object antialiasSetting = null;
	
	public JComponentRenderer(StyleGroup styleGroup, SwingGraphRenderer mainRenderer) {
		super(styleGroup);
		this.styleGroup = styleGroup ;
		this.mainRenderer = mainRenderer ;
	}

	@Override
	public void setupRenderingPass(Backend bck, DefaultCamera2D camera, boolean forShadow) {
		GraphMetrics metrics = camera.getMetrics();
		Graphics2D g = bck.graphics2D();

		size   = group.getSize();
		width  = (int)metrics.lengthToPx(size, 0);
		height = width ;
		if(size.size() > 1)
			height = (int)metrics.lengthToPx(size, 1) ;
  
		if(group.getShadowMode() != StyleConstants.ShadowMode.NONE)
			shadow = new SquareShape();
		else 
			shadow = null;
		
		antialiasSetting = g.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
		g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
	}

	@Override
	public void pushStyle(Backend bck, DefaultCamera2D camera, boolean forShadow) {
		if(shadow != null) {
			shadow.configureForGroup(bck, group, camera);
//		  	shadow.configure(bck, group, camera, null)
//		  	shadow.size(group, camera)
		}		
	}

	@Override
	public void pushDynStyle(Backend bck, DefaultCamera2D camera, GraphicElement element) {}

	@Override
	public void renderElement(Backend bck, DefaultCamera2D camera, GraphicElement element) {
		ComponentElement ce = getOrEquipWithJComponent(element);

		ce.setVisible(true);
		ce.updatePosition(camera);
		ce.updateLabel();
	
		if(ce.init == false)
		     checkStyle(camera, ce, true);
		else if(group.hasEventElements())
		     checkStyle(camera, ce, ! hadEvents);	// hadEvents allows to know if we just
		else checkStyle(camera, ce, hadEvents);		// changed the style due to an event	
	}												// and therefore must change the style.

	@Override
	public void renderShadow(Backend bck, DefaultCamera2D camera, GraphicElement element) {
		if(shadow != null) {
//			val pos = new Point2D.Double( element.getX, element.getY )
//
//			if( element.isInstanceOf[GraphicSprite] ) {
//				camera.getSpritePosition( element.asInstanceOf[GraphicSprite], pos, StyleConstants.Units.GU )
//			}
//			
////			shadow.setupContents( g, camera, element, null )
//			shadow.positionAndFit( g, camera, null, element, pos.x, pos.y )
			shadow.configureForElement(bck, element, null, camera);
			shadow.renderShadow(bck, camera, element, null);
		}
	}

	@Override
	public void elementInvisible(Backend bck, DefaultCamera2D camera, GraphicElement element) {
		getOrEquipWithJComponent(element).setVisible(false);		
	}

	@Override
	public void endRenderingPass(Backend bck, DefaultCamera2D camera, boolean forShadow) {
		bck.graphics2D().setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasSetting);
	}
	
	public void unequipElement(GraphicElement element) {
		if ( compToElement.get((JComponent)element.getComponent()) instanceof ComponentElement) {
			ComponentElement e = (ComponentElement)compToElement.get((JComponent)element.getComponent()) ;
			e.detach();
		}
	}
	
	/**
	 * Get the pair (swing component, graph element) corresponding to the given element. If the
	 * element is not yet associated with a Swing component, the binding is done.
	 */
	protected ComponentElement getOrEquipWithJComponent(GraphicElement element) {

		JComponent component = (JComponent)element.getComponent();
		ComponentElement ce = null;

		if(component == null) {
			switch (group.getJComponent()) {
				case BUTTON:
					ce = new ButtonComponentElement(element, new JButton(""));
					break;
				case TEXT_FIELD:
					ce = new TextFieldComponentElement(element, new JTextField(""));
					break;
				case PANEL:
					throw new RuntimeException("panel not yet available");
				default:
					 throw new RuntimeException("WTF ?!?");
			}
			
			if( ce != null ) {
				compToElement.put(ce.jComponent, ce);
			}
		}
		else {
			ce = compToElement.get(component);
		}
		
		return ce;
	}
	
	public void checkStyle(DefaultCamera2D camera, ComponentElement ce, boolean force) {
		if(force) {
			ce.checkIcon(camera);
			ce.checkBorder(camera, force);
			ce.setFill();
			ce.setTextAlignment();
			ce.setTextFont();
		}
	}
	
	
// Nested classes
 
	/**
	 * Represents the link between a JComponent and a GraphicElement.
	 * 
	 * Each of these component elements receive the action events of their button/text-field (for panel
	 * the user is free to do whatever he wants). They are in charge of adding and removing the
	 * component in the rendering surface, etc.
	 * 
	 * These elements also allow to push and remove the style to Swing components. We try to do this
	 * only when the style potentially changed, not at each redraw.
	 */
 	abstract class ComponentElement extends JPanel 
 	{
 		protected GraphicElement element ;
 		
 		/** Set to true if the element is not yet initialised with its style. */
		protected boolean init = false;

	// Access

		/** The Swing Component. */
 		protected JComponent jComponent ;

 	// Construction
		public ComponentElement(GraphicElement element) {
 			this.element = element ;
 	
 			setLayout(null);	// No layout in this panel, we set the component bounds ourselves.
 	 		mainRenderer.renderingSurface().add(this);
		}
 		
		/** Set of reset the fill mode and colour for the Swing component. */
		public void setFill() {
//			setBackground( group.getFillColor( 0 ) )
//			setOpaque( true )
//			if( group.getFillMode == StyleConstants.FillMode.PLAIN )
//				jComponent.setBackground( group.getFillColor( 0 ) )
		}
		
		/** Set or reset the text alignment for the Swing component. */
		public abstract void setTextAlignment();
		
		/** Set or reset the text font size, style and colour for the Swing component. */
		public abstract void setTextFont();
		
		/** Set or reset the label of the component. */
		public abstract void updateLabel();
		
		public void setBounds(int x, int y, int width, int height, DefaultCamera2D camera) {
			setBounds(x, y, width, height);
			
			int borderWidth = 0;
			
			if(group.getStrokeMode() != StyleConstants.StrokeMode.NONE && group.getStrokeWidth().value > 0)
				borderWidth = (int)camera.getMetrics().lengthToPx(group.getStrokeWidth());

			jComponent.setBounds(borderWidth, borderWidth, width-(borderWidth*2), height-(borderWidth*2));
		}
		
		/**
		 * Detach the Swing component from the graph element, remove the Swing component from its
		 * Swing container and remove any listeners on the Swing component. The ComponentElement
		 * is not usable after this.
		 */
		public void detach() { mainRenderer.renderingSurface().remove(this); }
		
	// Custom painting
		@Override
		public void paint(Graphics g) {
			paintComponent(g);	// XXX Remove this ??? XXX
			paintBorder(g);
			paintChildren(g);
		}
				
		/**
		 * Check the swing component follows the graph element position.
		 * @param camera The transformation from GU to PX.
		 */
		public void updatePosition(DefaultCamera2D camera) {
			if ( element instanceof GraphicNode) {
				positionNodeComponent( (GraphicNode)element, camera);
			}
			else if ( element instanceof GraphicSprite){
				positionSpriteComponent((GraphicSprite)element, camera);
			}
			else {
				throw new RuntimeException("WTF ?");
			}
		}

	// Command -- Utility, positioning
		private void positionNodeComponent(GraphicNode node, DefaultCamera2D camera) {
			Point3 pos = camera.transformGuToPx(node.getX(), node.getY(), 0);
					
			setBounds((int)(pos.x-(width/2)), (int)(pos.y-(height/2)), width, height, camera);
		}
		
		private void positionSpriteComponent(GraphicSprite sprite, DefaultCamera2D camera) {
			Point3 pos = camera.getSpritePosition( sprite, new Point3(), StyleConstants.Units.PX);
			
			setBounds((int)(pos.x-(width/2)), (int)(pos.y-(height/2)), width, height, camera);
		}
	
	// Command -- Utility, applying CSS style to Swing components
		public void checkBorder( DefaultCamera2D camera, boolean force ) {
			if(force) {
				if(group.getStrokeMode() != StyleConstants.StrokeMode.NONE && group.getStrokeWidth().value > 0)
					setBorder(createBorder(camera));
				else 
					setBorder(null);
			} 
			else {
				updateBorder(camera);
			}
		}

		private void updateBorder(DefaultCamera2D camera) {}

		private Border createBorder(DefaultCamera2D camera) {
			int width = (int)camera.getMetrics().lengthToPx( group.getStrokeWidth() );
			
			switch (group.getStrokeMode()) {
				case PLAIN: return BorderFactory.createLineBorder( ColorManager.getStrokeColor( group, 0 ), width );
				case DOTS: throw new RuntimeException( "TODO create dots and dashes borders for component to respect stroke-mode." );
				case DASHES: throw new RuntimeException( "TODO create dots and dashes borders for component to respect stroke-mode." );
				default:	return null ;
			}
		}
		
		public abstract void checkIcon(DefaultCamera2D camera) ;
 	}
 	
 	class TextFieldComponentElement extends ComponentElement implements ActionListener {
 		private JTextField comp ;
 		
		public TextFieldComponentElement(GraphicElement element, JTextField comp) {
			super(element);
			this.comp = comp ;
			this.element = element ;
			
			element.setComponent( comp );
			comp.addActionListener( this );
			add( comp );
			this.jComponent = comp ;
		}
 		
		@Override
		public void detach() {
			super.detach();
			comp.removeActionListener( this );
			remove( comp );
			element.setComponent( null );
	
			//component = null
			//element   = null
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			element.label = ((JTextField)comp).getText();
			element.setAttribute( "ui.label", element.label );
			element.setAttribute( "ui.clicked" );
		}
		
		@Override
		public void setTextAlignment() {
			switch (group.getTextAlignment()) {
				case ABOVE: comp.setHorizontalAlignment(SwingConstants.CENTER); break;
				case UNDER: comp.setHorizontalAlignment(SwingConstants.CENTER); break;
				case ALONG: comp.setHorizontalAlignment(SwingConstants.CENTER); break;
				case JUSTIFY: comp.setHorizontalAlignment(SwingConstants.CENTER); break;
				case CENTER: comp.setHorizontalAlignment(SwingConstants.CENTER); break;
				case AT_RIGHT: comp.setHorizontalAlignment(SwingConstants.RIGHT); break;
				case RIGHT: comp.setHorizontalAlignment(SwingConstants.RIGHT); break;
				case AT_LEFT: comp.setHorizontalAlignment(SwingConstants.LEFT); break;
				case LEFT: comp.setHorizontalAlignment(SwingConstants.LEFT); break;
				default: break;
			}
		}
		
		@Override
		public void setTextFont() {
			Font font = FontCache.getDefaultFont( group.getTextStyle(), (int)group.getTextSize().value );
			if( ! group.getTextFont().equals( "default" ) )
				font = FontCache.getFont( group.getTextFont(), group.getTextStyle(), (int)group.getTextSize().value );
           
			comp.setFont( font );
			comp.setForeground( ColorManager.getTextColor( group, 0 ) );
		}
		
		@Override
		public void updateLabel() {
			if( ! comp.hasFocus() )
				comp.setText( element.getLabel() );
		}
		
		@Override
		public void checkIcon(DefaultCamera2D camera) {}
 	}
 	
 	class ButtonComponentElement extends ComponentElement implements ActionListener {
 		
 		private JButton comp ;
 		
 		public ButtonComponentElement(GraphicElement element, JButton comp) {
			super(element);
			this.comp = comp ;
			this.element = element ;
			
			element.setComponent( comp );
			comp.addActionListener( this );
			add( comp );
			this.jComponent = comp ;
		}
 		
 		@Override
 		public void detach() {
 			super.detach();
 			
 			comp.removeActionListener( this );
			remove(comp);
			element.setComponent( null );
	
//			component = null;
//			element   = null;
 		}
 		
 		@Override
 		public void actionPerformed(ActionEvent e) {
 			element.label = comp.getText();
 			element.setAttribute( "ui.label", element.label );
 			element.setAttribute( "ui.clicked" );
 			element.myGraph().setAttribute( "ui.clicked", element.getId() );
 		}
 		
 		@Override
		public void setTextAlignment() {
			switch (group.getTextAlignment()) {
				case ALONG: comp.setHorizontalAlignment(SwingConstants.CENTER); break;
				case JUSTIFY: comp.setHorizontalAlignment(SwingConstants.CENTER); break;
				case CENTER: comp.setHorizontalAlignment(SwingConstants.CENTER); break;
				case AT_RIGHT: comp.setHorizontalAlignment(SwingConstants.RIGHT); break;
				case RIGHT: comp.setHorizontalAlignment(SwingConstants.RIGHT); break;
				case AT_LEFT: comp.setHorizontalAlignment(SwingConstants.LEFT); break;
				case LEFT: comp.setHorizontalAlignment(SwingConstants.LEFT); break;
				case ABOVE: comp.setHorizontalAlignment(SwingConstants.TOP); break;
				case UNDER: comp.setHorizontalAlignment(SwingConstants.BOTTOM); break;
				default: break;
			}
		}
 		
 		@Override
		public void setTextFont() {
			Font font = FontCache.getDefaultFont( group.getTextStyle(), (int)group.getTextSize().value );
			if( ! group.getTextFont().equals( "default" ) )
				font = FontCache.getFont( group.getTextFont(), group.getTextStyle(), (int)group.getTextSize().value );
           
			comp.setFont( font );
			comp.setForeground( ColorManager.getTextColor( group, 0 ) );
		}
 		
 		@Override
 		public void updateLabel() {
 			String label = element.getLabel();
 					
 			if( label != null )
 				comp.setText( label ); 			
 		}
 		
 		@Override
 		public void checkIcon(DefaultCamera2D camera) {
 			if( group.getIconMode() != StyleConstants.IconMode.NONE ) {
 				String url   = group.getIcon();
 				BufferedImage image = ImageCache.loadImage( url );
				
				if( image != null ) {
					comp.setIcon( new ImageIcon( image ) );
					
					switch (group.getIconMode()) {
						case AT_LEFT:
							comp.setHorizontalTextPosition( SwingConstants.RIGHT );  
							comp.setVerticalTextPosition( SwingConstants.CENTER );
							break;
						case AT_RIGHT:
							comp.setHorizontalTextPosition( SwingConstants.LEFT  );
							comp.setVerticalTextPosition( SwingConstants.CENTER );
							break;
						case ABOVE:
							comp.setHorizontalTextPosition( SwingConstants.CENTER );
							comp.setVerticalTextPosition( SwingConstants.BOTTOM );
							break;
						case UNDER:
							comp.setHorizontalTextPosition( SwingConstants.CENTER );
							comp.setVerticalTextPosition( SwingConstants.TOP );
							break;
						default:
							throw new RuntimeException( "unknown image mode "+group.getIconMode() );
					}
				}
			} 			
 		}
 	}
}
