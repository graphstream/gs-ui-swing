import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import javax.swing.*;
import java.awt.*;

import static java.awt.Dialog.ModalityType.DOCUMENT_MODAL;

public class Clicks {

    static Graph graph;
    static JDialog mainFrame;
    static JPanel mainPanel;

    static JTextArea codeArea;


    public static void main(String args[]) {
        System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("sun.java2d.uiScale", "1.0");
        new Clicks();
        System.out.println("Doneee");

    }


    boolean loop;
    public Clicks() {
        loop = true;
        mainFrame = new JDialog();
        JDialog frame = mainFrame;
        mainPanel = new JPanel(){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(640, 480);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        JPanel panel = mainPanel;
        codeArea = new JTextArea();
        codeArea.setEditable(false);






        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel.setBorder(BorderFactory.createLineBorder(Color.blue, 5));
        mainFrame.add(panel);

        // We do as usual to display a graph. This
        // connect the graph outputs to the viewer.
        // The viewer is a sink of the graph.
        graph = new SingleGraph("Clicks");
        graph.setAttribute("ui.stylesheet",
                "node {\n" +
                        "    fill-color: #f0f0f0;\n" +
                        "    text-color: blue;\n" +
                        "    text-size: 15;\n" +
                        "    shape: box;\n" +
                        "    size: 50px;\n" +
                        "}\n" +
                        "sprite.basicBlock {\n" +
                        "    shape: rounded-box;\n" +
                        "    stroke-mode: plain;\n" +
                        "    stroke-color: #000000;\n" +
                        "    fill-color: green;\n" +
                        "    size-mode: fit;\n" +
                        "    text-alignment: center;\n" +
                        "}");

        Node a = graph.addNode("A");
        a.setAttribute("ui.label", "Line1\nLine2\nLine3");
        graph.addNode("B");
        graph.addNode("C");
        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CA", "C", "A");
//        graph.display();

//        SpriteManager spriteManager = new SpriteManager(graph);
//
//        Sprite sprite = spriteManager.addSprite("sprite");

//        sprite.attachToNode("A");
//        String bb = "A\nB\nC\nD\nE";
//        sprite.setAttribute("ui.label", bb.toString());
//        sprite.setPosition(0,0,0);
//        sprite.setAttribute("ui.class", "basicBlock");

//        Sprite sprite1 = spriteManager.addSprite("sprite");
//        sprite1.attachToNode("A");
//        String bb = "A\nB\nC";
//        sprite1.setAttribute("ui.label", "EDG");
//        sprite1.setPosition(1,1,0);
//        sprite1.setAttribute("ui.class", "basicBlock");

        Viewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);


        ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);
        viewer.getDefaultView().enableMouseOptions();
        viewer.enableAutoLayout();

        panel.add(viewPanel, BorderLayout.CENTER);
        panel.add(codeArea, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setModal(true);
//        Viewer viewer = graph.display();
//        viewer.getDefaultView().enableMouseOptions();
//
//        // The default action when closing the view is to quit
//        // the program.
//        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);

        // We connect back the viewer to the graph,
        // the graph becomes a sink for the viewer.
        // We also install us as a viewer listener to
        // intercept the graphic events.
        ViewerPipe fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(new MouseOptions());
        fromViewer.addSink(graph);

        // Then we need a loop to do our work and to wait for events.
        // In this loop we will need to call the
        // pump() method before each use of the graph to copy back events
        // that have already occurred in the viewer thread inside
        // our thread.

//        fromViewer.pump();

        while(loop) {
            fromViewer.pump(); // or fromViewer.blockingPump(); in the nightly builds
            if (!frame.isVisible()) {
//                frame.dispose();
                break;
            }
            // here your simulation code.

            // You do not necessarily need to use a loop, this is only an example.
            // as long as you call pump() before using the graph. pump() is non
            // blocking.  If you only use the loop to look at event, use blockingPump()
            // to avoid 100% CPU usage. The blockingPump() method is only available from
            // the nightly builds.
        }

    }

//    public void viewClosed(String id) {
//        loop = false;
//    }
//
//    public void buttonPushed(String id) {
//        codeArea.setText((String) graph.getNode(id).getAttribute("ui.label"));
//        codeArea.setEditable(false);
//    }
//
//    public void buttonReleased(String id) {
//        System.out.println("Button released on node "+id);
//    }
//
//    public void mouseOver(String id) {
//        System.out.println("Need the Mouse Options to be activated");
//    }
//
//    public void mouseLeft(String id) {
//        System.out.println("Need the Mouse Options to be activated");
//    }

    public class MouseOptions implements ViewerListener {
        public void viewClosed(String id) {

            loop = false;
        }

        public void buttonPushed(String id) {
            codeArea.setText((String) graph.getNode(id).getAttribute("ui.label"));
            codeArea.setEditable(false);
        }

        public void buttonReleased(String id) {
            System.out.println("Button released on node "+id);
        }

        public void mouseOver(String id) {
            System.out.println("Need the Mouse Options to be activated");
        }

        public void mouseLeft(String id) {
            System.out.println("Need the Mouse Options to be activated");
        }
    }
}
