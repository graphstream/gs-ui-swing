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
  
package org.graphstream.ui.viewer_swing.test;

import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.stylesheet.Values;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteFactory;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class TestManySprite implements ViewerListener {
	public static void main(String[] args) {
		System.setProperty("org.graphstream.ui", "org.graphstream.ui.swing.util.Display");

		(new TestManySprite()).run();
	}
	
	/** The application runs while this is true. */
	boolean loop = true;

	/** The graph at hand. */
	Graph graph = null;

	/** The set of sprites. */
	SpriteManager sprites = null;

	int NODE_COUNT = 1000;
	int SPRITE_COUNT = 500;
	
	private void run() {
		graph  = new MultiGraph( "TestSprites" );
		Viewer viewer = graph.display( true );
		ViewerPipe pipeIn = viewer.newViewerPipe();
		DorogovtsevMendesGenerator gen    = new DorogovtsevMendesGenerator();

		pipeIn.addAttributeSink( graph );
		pipeIn.addViewerListener( this );
		pipeIn.pump();

		System.out.println( NODE_COUNT+" nodes, "+SPRITE_COUNT+" sprites%n" );

		graph.setAttribute( "ui.default.title", "Layout Test" );
		graph.setAttribute( "ui.antialias" );
		graph.setAttribute( "ui.stylesheet", styleSheet );

		gen.addSink( graph );
//				gen.setDirectedEdges( true, true )
		gen.begin();
		int i = 0;
		while ( i < NODE_COUNT ) {
			gen.nextEvents(); i += 1 ;
		}
		gen.end();

		sleep( 1000 );
		addSprites();

		while( loop ) {
			pipeIn.pump();
			moveSprites();
			sleep( 10 );
		}
		System.out.println( "bye bye" );
		System.exit(0);
	}
	

	protected void sleep( long ms ) {
		try {
			Thread.sleep( ms );
		} catch (InterruptedException e) { e.printStackTrace(); }
	}

// Viewer Listener Interface

	public void viewClosed( String id ) { loop = false ;}

	public void buttonPushed( String id ) {}

 	public void buttonReleased( String id ) {}
 	

	private void moveSprites() {
		 sprites.forEach( s -> ((TestSprite)s).move() );	
	}
	

	private void addSprites() {
		sprites = new SpriteManager( graph );
		sprites.setSpriteFactory( new TestSpriteFactory() );

		for( int i = 0 ; i < SPRITE_COUNT ; i++ ) {
			sprites.addSprite( i+"" );
		}

		sprites.forEach ( s ->s.attachToEdge( randomEdge( graph ).getId() ));
	}


	private Edge randomEdge(Graph graph) {
		int min = 0 ;
		int max = (int) graph.edges().count();
		
		int rand = (int) (min + (Math.random() * (max - min)));
		
		return graph.getEdge(rand);
	}
	
	private String styleSheet = 
			"graph {"+
				"fill-mode: plain;"+
				"fill-color: white, gray;"+
				"padding: 60px;"+
			"}"+
			"node {"+
				"shape: circle;"+
				"size: 4px;"+
				"fill-mode: plain;"+
				"fill-color: grey;"+
				"stroke-mode: none;"+
				"text-visibility-mode: zoom-range;"+
				"text-visibility: 0, 0.9;"+
			"}"+
			"edge {"+
				"size: 1px;"+
				"shape: line;"+
				"fill-color: grey;"+
				"fill-mode: plain;"+
				"stroke-mode: none;"+
			"}"+
			"sprite {"+
				"shape: circle;"+
				"size: 6px;"+
				"fill-mode: plain;"+
				"fill-color: red;"+
				"stroke-mode: none;"+
			"}";
	
  	public void mouseOver(String id){}

	public void mouseLeft(String id){}
}

class TestSpriteFactory extends SpriteFactory {
	@Override
	public Sprite newSprite(String identifier, SpriteManager manager, Values position) {
		return new TestSprite(identifier, manager);
	}
}

class TestSprite extends Sprite {
	double dir = 0.01f;
	
	public TestSprite( String identifier, SpriteManager manager ) {
		super( identifier, manager );
	}
	
	public void move() {
		double p = getX();

		p += dir;

		if( p < 0 || p > 1 )
			chooseNextEdge();
		else
			setPosition( p );
	}

	public void chooseNextEdge() {
		Edge edge = (Edge) getAttachment();
		Node node = edge.getSourceNode(); 
		if( dir > 0 )
			node = edge.getTargetNode() ;
		
		
		Edge next = randomEdge( node );
		double pos = 0;

		if( node == next.getSourceNode() ) { 
			dir =  0.01f; 
			pos = 0;
		}
		else { 
			dir = -0.01f; 
			pos = 1; 
		}

		attachToEdge( next.getId() );
		setPosition( pos );
	}

	private Edge randomEdge(Node node) {
		int min = 0 ;
		int max = (int) node.edges().count();
		
		int rand = (int) (min + (Math.random() * (max - min)));
		
		return node.getEdge(rand);
	}
}
