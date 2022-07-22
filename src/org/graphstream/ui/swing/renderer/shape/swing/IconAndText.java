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

package org.graphstream.ui.swing.renderer.shape.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.IconMode;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.TextStyle;
import org.graphstream.ui.graphicGraph.stylesheet.Value;
import org.graphstream.ui.graphicGraph.stylesheet.Values;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.swing.util.ColorManager;
import org.graphstream.ui.swing.util.FontCache;
import org.graphstream.ui.swing.util.ImageCache;

public abstract class IconAndText {
    /** Overall width of the icon and text with all space and padding included. */
    //protected double width;
    /** Overall height of the icon and text with all space and padding included. */
    //protected double height;
    /** Overall descent of the icon and text with all space and padding included. */
    protected double descent;
    /** Overall ascent of the icon and text with all space and padding included. */
    protected double ascent;

    protected TextBox text;
    protected double offx;
    protected double offy;
    protected double padx;
    protected double pady;

    public IconAndText(TextBox text, double offx, double offy, double padx, double pady) {
        this.descent = text.getDescent();
        this.ascent = text.getAscent();
        this.text = text;
        this.offx = offx;
        this.offy = offy;
        this.padx = padx;
        this.pady = pady;
    }

    public static IconAndText apply(Style style, DefaultCamera2D camera, GraphicElement element) {
        BufferedImage icon = null;
        TextBox text = TextBox.apply(camera, style);
        Values padd = style.getPadding();
        Values off = style.getTextOffset();
        double padx = camera.getMetrics().lengthToPx(padd, 0);
        double pady = padx;
        if (padd.size() > 1)
            pady = camera.getMetrics().lengthToPx(padd, 1);
        double offx = camera.getMetrics().lengthToPx(off, 0);
        double offy = padx;
        if (padd.size() > 1)
            offy = camera.getMetrics().lengthToPx(off, 1);

        if (style.getIconMode() != IconMode.NONE) {
            String url = style.getIcon();

            if (url.equals("dynamic")) {
                if (element.hasLabel("ui.icon"))
                    url = element.getLabel("ui.icon").toString();
                else
                    url = null;
            }

            if (url != null) {
                icon = ImageCache.loadImage(url);
            }
        }

        if (icon == null) {
            return new IconAndTextOnlyText(text, offx, offy, padx, pady);
        } else {
            switch (style.getIconMode()) {
                case AT_LEFT:
                    return new IconAtLeftAndText(icon, text, offx, offy, padx, pady);
                case AT_RIGHT:
                    return new IconAtLeftAndText(icon, text, offx, offy, padx, pady);
                case ABOVE:
                    return new IconAtLeftAndText(icon, text, offx, offy, padx, pady);
                case UNDER:
                    return new IconAtLeftAndText(icon, text, offx, offy, padx, pady);
                default:
                    throw new RuntimeException("???");
            }
        }
    }

    public abstract void render(Backend backend, DefaultCamera2D camera, double xLeft, double yBottom);

    public abstract void setIcon(Backend backend, String url);

    public abstract void setText(Backend backend, String text);

    public abstract double getWidth();

    public abstract double getHeight();

    public abstract String getText(Backend backend);
}

class IconAndTextOnlyText extends IconAndText {
    public IconAndTextOnlyText(TextBox text, double offx, double offy, double padx, double pady) {
        super(text, offx, offy, padx, pady);
    }

    public double getWidth() {
        return text.getWidth() + padx * 2;
    }

    public double getHeight() {
        return text.getAscent() + text.getDescent() + pady * 2 + text.getHeight();
    }

    public void setText(Backend backend, String text) {
        this.text.setText(text, backend);
    }

    public String getText(Backend backend) {
        return this.text.getText();
    }

    public void setIcon(Backend backend, String url) {
    }

    public void render(Backend backend, DefaultCamera2D camera, double xLeft, double yBottom) {
        this.text.render(backend, offx + xLeft,  offy + yBottom - this.getHeight());
    }
}

class IconAtLeftAndText extends IconAndText {
    private BufferedImage icon;

    public IconAtLeftAndText(BufferedImage icon, TextBox text, double offx, double offy, double padx, double pady) {
        super(text, offx, offy, padx, pady);
        //this.width = text.getWidth() + icon.getWidth(null) + 5 + padx*2 ;
        //this.height = Math.max(icon.getHeight(null), text.ascent + text.descent) + pady*2;
        this.icon = icon;
    }


    public void setText(Backend backend, String text) {
        this.text.setText(text, backend);
    }

    public String getText(Backend backend) {
        return this.text.getText();
    }

    public void setIcon(Backend backend, String url) {
        ImageCache.loadImage(url);
        if (icon == null) {
            icon = ImageCache.dummyImage();
        }
    }

    public void render(Backend backend, DefaultCamera2D camera, double xLeft, double yBottom) {
        Graphics2D g = backend.graphics2D();
        g.drawImage(icon, new AffineTransform(1f, 0f, 0f, 1f, offx + xLeft, offy + (yBottom - (getHeight() / 2)) - (icon.getHeight() / 2) + pady), null);
        double th = text.getAscent() + text.getDescent();
        double dh = 0f;
        if (icon.getHeight() > th)
            dh = ((icon.getHeight() - th) / 2f);

        this.text.render(backend, offx + xLeft + icon.getWidth() + 5, offy + yBottom - dh - descent);
    }

    public double getWidth() {
        return text.getWidth() + icon.getWidth(null) + 5 + padx * 2;
    }


    public double getHeight() {
        return Math.max(icon.getHeight(null), text.getAscent() + text.getDescent()) + pady * 2;
    }
}

/** A simple wrapper for a font and a text string. */
abstract class TextBox {
    /** The text string. */
    String textData;

    /** Renders the text at the given coordinates. */
    public abstract void render(Backend backend, double xLeft, double yBottom);

    /** Set the text string to paint. */
    public abstract void setText(String text, Backend backend);

    public abstract String getText();

    public abstract double getWidth();

    public abstract double getHeight();

    public abstract double getDescent();

    public abstract double getAscent();

    /**
     * Factory companion object for text boxes.
     */
    static FontRenderContext defaultFontRenderContext = new FontRenderContext(new AffineTransform(), true, true);

    public static TextBox apply(DefaultCamera2D camera, Style style) {
        String fontName = style.getTextFont();
        TextStyle fontStyle = style.getTextStyle();
        Value fontSize = style.getTextSize();
        Color textColor = ColorManager.getTextColor(style, 0);
        Color bgColor = null;
        boolean rounded = false;

        switch (style.getTextBackgroundMode()) {
            case NONE:
                break;
            case PLAIN:
                rounded = false;
                bgColor = ColorManager.getTextBackgroundColor(style, 0);
                break;
            case ROUNDEDBOX:
                rounded = true;
                bgColor = ColorManager.getTextBackgroundColor(style, 0);
                break;
            default:
                break;
        }

        Values padding = style.getTextPadding();
        double padx = camera.getMetrics().lengthToPx(padding, 0);
        double pady = padx;
        if (padding.size() > 1)
            camera.getMetrics().lengthToPx(padding, 1);

        return TextBox.apply(fontName, fontStyle, (int) fontSize.value, textColor, bgColor, rounded, padx, pady);
    }

    public static TextBox apply(String fontName, TextStyle style, int fontSize, Color textColor, Color bgColor,
                                boolean rounded, double padx, double pady) {
        return new SwingTextBox(FontCache.getFont(fontName, style, fontSize), textColor, bgColor, rounded, padx, pady);
    }
}

class SwingTextBox extends TextBox {

    Font font;
    Color textColor;
    Color bgColor;
    boolean rounded;
    double padx;
    double pady;

    List<TextLayout> text;
    Rectangle2D bounds;

    public SwingTextBox(Font font, Color textColor, Color bgColor, boolean rounded, double padx, double pady) {
        this.font = font;
        this.textColor = textColor;
        this.bgColor = bgColor;
        this.rounded = rounded;
        this.padx = padx;
        this.pady = pady;

        this.text = null;
        this.textData = null;
        this.bounds = new Rectangle2D.Double(0, 0, 0, 0);
    }


    /** Changes the text and compute its bounds. This method tries to avoid recomputing bounds
     *  if the text does not really changed. */
    public void setText(String text, Backend backend) {
        if (text != null && text.length() > 0) {
            if (textData != text || !textData.equals(text)) {
                // As the text is not rendered using the default affine transform, but using
                // the identity transform, and as the FontRenderContext uses the current
                // transform, we use a predefined default font render context initialized
                // with an identity transform here.
                this.textData = text;
                this.text = new ArrayList<TextLayout>();
                String[] textLines = text.split("\n");
                double maxWidth = 0;
                double maxHeight = 0;
                for (int i = 0; i < textLines.length; i++) {
                    if (textLines[i].length() == 0) {
                        this.text.add(new TextLayout("\b", font, TextBox.defaultFontRenderContext));
                        maxHeight += this.text.get(i).getBounds().getHeight();
                    } else {
                        this.text.add(new TextLayout(textLines[i], font, TextBox.defaultFontRenderContext));
                        maxWidth = Math.max(this.text.get(i).getAdvance(), maxWidth);
                        maxHeight += this.text.get(i).getBounds().getHeight();
                    }
                }
                this.bounds = new Rectangle2D.Double(
                        this.text.get(0).getBounds().getX(),
                        this.text.get(0).getBounds().getY(),
                        maxWidth,
                        maxHeight);
            } else {
                this.textData = null;
                this.text = null;
                this.bounds = new Rectangle2D.Double(0, 0, 0, 0);
            }
        }
    }

    @Override
    public String getText() {
        return textData;
    }

    public double getWidth() {
        if (bounds != null)
            return bounds.getWidth();
        else
            return 0;
    }

    public double getHeight() {
        if (bounds != null)
            return bounds.getHeight();
        else
            return 0;
    }

    public double getDescent() {
        if (text != null)
            return text.get(0).getDescent();
        else
            return 0;
    }

    public double getAscent() {
        if (text != null)
            return text.get(0).getAscent();
        else
            return 0;
    }

    public void render(Backend backend, double xLeft, double yBottom) {

        if (text != null) {
            Graphics2D g = backend.graphics2D();

            if (bgColor != null) {
                double a = getAscent();
                double h = a + getDescent();

                g.setColor(bgColor);
                if (rounded) {
                    g.fill(new RoundRectangle2D.Double(xLeft - padx, yBottom - (a + pady), getWidth() + 1 + (padx + padx), h + (pady + pady), 6, 6));
                } else {
                    g.fill(new Rectangle2D.Double(xLeft - padx, yBottom - (a + pady), getWidth() + 1 + (padx + padx), h + (pady + pady)));
                }
            }
            g.setColor(textColor);
            for (int i = 0; i < text.size(); i++) {
//				text.get(i).draw(g, xLeft, yBottom - getAscent());
//				yBottom -= getAscent() + getDescent();
                text.get(i).draw(g, (float) xLeft, (float) ((float) yBottom + i * getAscent()));
            }
//			text.get(0).draw(g, (float)xLeft, (float)yBottom);
        }
    }
}
