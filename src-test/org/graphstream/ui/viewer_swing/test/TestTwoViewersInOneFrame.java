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

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.Viewer;

@SuppressWarnings("serial")
public class TestTwoViewersInOneFrame extends JFrame {
	public static void main(String[] args) {
		(new TestTwoViewersInOneFrame()).run();
	}

	private void run() {
		MultiGraph graph1 = new MultiGraph("g1");
		MultiGraph graph2 = new MultiGraph("g2");
		
		ThreadProxyPipe pipe1 = new ThreadProxyPipe() ;
		pipe1.init(graph1);
		ThreadProxyPipe pipe2 = new ThreadProxyPipe() ;
		pipe2.init(graph2);
		
		Viewer viewer1 = new SwingViewer(pipe1);
		Viewer viewer2 = new SwingViewer(pipe2);

	    graph1.setAttribute("ui.quality");
	    graph2.setAttribute("ui.quality");
	    graph1.setAttribute("ui.antialias");
	    graph2.setAttribute("ui.antialias");
		graph1.setAttribute("ui.stylesheet", styleSheet1);
		graph2.setAttribute("ui.stylesheet", styleSheet2);

		DefaultView view1 = new DefaultView(viewer1, "view1", new SwingGraphRenderer());
		DefaultView view2 = new DefaultView(viewer2, "view2", new SwingGraphRenderer());
		viewer1.addView(view1);
		viewer2.addView(view2);
		viewer1.enableAutoLayout();
		viewer2.enableAutoLayout();

		DorogovtsevMendesGenerator gen = new DorogovtsevMendesGenerator();

		gen.addSink(graph1);
		gen.addSink(graph2);
		gen.begin();
		for(int i = 0 ; i < 100; i++)
			gen.nextEvents();
		gen.end();

		gen.removeSink(graph1);
		gen.removeSink(graph2);

		setLayout(new GridLayout(1, 2));

		JPanel panelView1 = new JPanel(new BorderLayout()); // prevent UI shift issues
		panelView1.add(view1, BorderLayout.CENTER);
		
		JPanel panelView2 = new JPanel(new BorderLayout()); // prevent UI shift issues
		panelView2.add(view2, BorderLayout.CENTER);
		
		add(panelView1);
		add(panelView2);
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	protected String styleSheet1 =
			"graph { padding: 40px; }" +
			"node { fill-color: red; stroke-mode: plain; stroke-color: black; }";
	
	protected String styleSheet2 =
		"graph { padding: 40px; }" +
		"node { fill-color: blue; stroke-mode: plain; stroke-color: black; }";
}
