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
package org.graphstream.stream.file.images;

import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.view.GraphRenderer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class SwingImageRenderer implements ImageRenderer {
	protected BufferedImage image;
	protected Graphics2D g2d;
	protected SwingGraphRenderer renderer;

	public SwingImageRenderer() {
		this.renderer = new SwingGraphRenderer();
	}

	public void clear(int color) {
		for (int x = 0; x < image.getWidth(); x++)
			for (int y = 0; y < image.getHeight(); y++)
				image.setRGB(x, y, color);
	}

	public GraphRenderer<?, ?> getGraphRenderer() {
		return renderer;
	}

	public void init(Resolution resolution, FileSinkImages.OutputType outputType) {
		image = new BufferedImage(resolution.getWidth(), resolution.getHeight(), outputType.imageType);
		g2d = image.createGraphics();
	}

	public BufferedImage getRenderedImage() {
		image.flush();
		return image;
	}

	public void render(int x, int y, int width, int height) {
		renderer.render(g2d, x, y, width, height);
	}
}
