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
package org.graphstream.ui.swing.util;

import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.view.camera.Camera;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class SwingFileSinkImages extends FileSinkImages {
	protected BufferedImage image;
	protected Graphics2D g2d;
	protected SwingGraphRenderer renderer;

	public SwingFileSinkImages() {
		this.renderer = new SwingGraphRenderer();
		this.renderer.open(gg, null);
	}

	@Override protected Camera getCamera() {
		return renderer.getCamera();
	}

	@Override protected void render() {
		renderer.render(g2d, 0, 0, resolution.getWidth(), resolution.getHeight());
	}

	@Override protected BufferedImage getRenderedImage() {
		return image;
	}

	@Override protected void initImage() {
		image = new BufferedImage(resolution.getWidth(), resolution.getHeight(), outputType.imageType);
		g2d = image.createGraphics();
	}

	@Override protected void clearImage(int color) {
		for (int x = 0; x < image.getWidth(); x++)
			for (int y = 0; y < image.getHeight(); y++)
				image.setRGB(x, y, color);
	}
}
