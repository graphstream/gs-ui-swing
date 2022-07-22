import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import javax.swing.*;
import java.awt.*;

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

        graph = new SingleGraph("Clicks");
        graph.setAttribute("ui.stylesheet",
                "node {\n" +
                        "    fill-color: yellow;\n" +
                        "    text-color: blue;\n" +
                        "    text-size: 15;\n" +
                        "    shape: box;\n" +
//                        "    size: 50px;\n" +
                        "    size-mode: fit;\n" +
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
        a.setAttribute("ui.label", "\n {       \n       Line1\n          ABVCC DDF ADSF ADSF ASD\na\n\nc");
        graph.addNode("B");
        graph.addNode("C");
        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CA", "C", "A");

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

        ViewerPipe fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(new MouseOptions());
        fromViewer.addSink(graph);

        while(loop) {
            fromViewer.pump();
            if (!frame.isVisible()) {
                break;
            }
        }

    }



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
