package org.graphstream.ui.swing.util;

import org.graphstream.graph.Graph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.images.FileSinkImagesFactory;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.Layouts;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.GraphRenderer;
import org.graphstream.ui.view.Viewer;

public class Display implements org.graphstream.util.Display, FileSinkImagesFactory {

	@Override
	public Viewer display(Graph graph, boolean autoLayout) {
		SwingViewer viewer = new SwingViewer(graph,
				SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		GraphRenderer renderer = new SwingGraphRenderer();
		viewer.addView(SwingViewer.DEFAULT_VIEW_ID, renderer);
		if (autoLayout) {
			Layout layout = Layouts.newLayoutAlgorithm();
			viewer.enableAutoLayout(layout);
		}
		return viewer;
	}

	@Override public FileSinkImages createFileSinkImages() {
		return new SwingFileSinkImages();
	}
}
