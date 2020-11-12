package net.remgant.music;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


public class TabGui extends JFrame {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel
                    ("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ignored) {
        }
        TabGui frame = new TabGui();
        frame.pack();
        frame.setVisible(true);
    }

    int screenSizeX;
    int screenSizeY;
    GuiPanel panel;

    public TabGui() {
        super("Gui");
        screenSizeX = 600;
        screenSizeY = 200;
        setBounds(0, 0, screenSizeX, screenSizeY);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        JMenuBar myMenuBar = new JMenuBar();
        setJMenuBar(myMenuBar);
        JMenu FileMenu = new JMenu("File");
        myMenuBar.add(FileMenu);

        JMenuItem ExitMenuItem = new JMenuItem("Exit");
        ExitMenuItem.addActionListener(e -> System.exit(1));
        FileMenu.add(ExitMenuItem);

        Dimension size = new Dimension(screenSizeX, screenSizeY);
        panel = new GuiPanel(size);
        getContentPane().add("Center", panel);
        panel.setPreferredSize(size);
        // panel.draw();
        String[] e = {"Name: E",
                "Tuning: EADGBE",
                "Fret: 0",
                "Tab:",
                "0--100",
                "-32---"};
        String[] c7 = {"Name: C7",
                "Fret: 8",
                "Tab:",
                "1====1",
                "---2--",
                "-3----"};
        String[] a9 = {"Name: A9",
                "Fret: 4",
                "Tab:",
                "-1=1-X",
                "4-3-2-"};
        String[] d_dadgad = {"Name: D",
                "Tuning: DADGAD",
                "Tab:",
                "00--00",
                "---1--",
                "------",
                "--2---"};
        String[] gm = {"Name: Gm",
                "Tab:",
                "XX----",
                "------",
                "---111",
                "------",
                "--3---"};
        String[] fsh = {"Name: F#",
                "Tab:",
                "------",
                "1====1",
                "---2--",
                "-43---"};
//        Tablature t = Tablature.parse(a9);
//        System.out.println(t.toString());
//        panel.drawTab(t);

        Tablature[] t = new Tablature[]{Tablature.parse(fsh), Tablature.parse(a9), Tablature.parse(e), Tablature.parse(c7),
                Tablature.parse(d_dadgad), Tablature.parse(gm)};
        panel.drawTabs(t, 3, 2);
    }

    static class GuiPanel extends JPanel {
        private final Dimension d;
        private final BufferedImage image;

        public GuiPanel(Dimension d) {
            this.d = d;
            image = new BufferedImage(d.width, d.height,
                    BufferedImage.TYPE_INT_ARGB);
        }

        public void drawTabs(Tablature[] t, int cols, int rows) {
            Graphics2D g = image.createGraphics();
            g.setColor(Color.WHITE);
            g.fill(new Rectangle2D.Float(0.0f, 0.0f, (float) d.width,
                    (float) d.height));
            g.setColor(Color.BLACK);
            double x = 0.0;
            double y = 0.0;
            for (Tablature tablature : t) {
                Area a = tablature.draw(g);
                AffineTransform at = AffineTransform.getTranslateInstance(x, y);
                a.transform(at);
                g.fill(a);
                x += a.getBounds2D().getWidth() * 1.25;
            }
        }

        public void drawTab(Tablature t) {
            Graphics2D g = image.createGraphics();
            g.setColor(Color.WHITE);
            g.fill(new Rectangle2D.Float(0.0f, 0.0f, (float) d.width,
                    (float) d.height));
            int x = 50;
            int y = 50;
            Area a = t.draw(g);
            //a.transform(AffineTransform.getTranslateInstance((double)x,(double)y));
            g.setColor(Color.BLACK);
            g.fill(a);
            repaint();
        }

        public void draw() {
            Graphics2D g = image.createGraphics();
            int x = 50;
            int y = 50;
            int lineWidth = 1;
            int h = 85 + lineWidth;
            int w = 85 + lineWidth;

            g.setColor(Color.WHITE);
            g.fill(new Rectangle2D.Float(0.0f, 0.0f, (float) d.width,
                    (float) d.height));

            Area a = new Area();
            int xx = x;
            int yy = y;
            for (int i = 0; i < 6; i++) {
                Rectangle2D.Float rx = new Rectangle.Float((float) xx, (float) y,
                        (float) lineWidth, (float) h);
                Rectangle2D.Float ry = new Rectangle.Float((float) x, (float) yy,
                        (float) w, (float) lineWidth);
                xx += 17;
                yy += 17;
                a.add(new Area(rx));
                a.add(new Area(ry));
            }
//            printDot(a,4,1,x,y);
            printDot(a, 5, 2, x, y);
//            printDot(a,6,1,x,y);
            printBarre(a, 3, 6, 1, x, y);
            g.setColor(Color.BLACK);
            g.fill(a);
            repaint();
        }

        private void printDot(Area a, int string, int fret, int xOffset, int yOffset) {
            Ellipse2D.Float dot = new Ellipse2D.Float(-5.0f, -5.0f, 10.0f, 10.0f);
            AffineTransform at = new AffineTransform();
            at.translate(xOffset, yOffset);
            at.translate((string - 1) * 17, (double) (fret * 17) - (17.0 / 2.0));
            Area d = new Area(dot);
            d.transform(at);
            a.add(d);
        }

        private void printBarre(Area a, int start, int end, int fret, int xOffset, int yOffset) {
            printDot(a, start, fret, xOffset, yOffset);
            printDot(a, end, fret, xOffset, yOffset);
            int w = (end - start) * 17;
            Rectangle2D.Float barre = new Rectangle2D.Float(0.0f, -5.0f, (float) w, 10.0f);
            AffineTransform at = new AffineTransform();
            at.translate(xOffset, yOffset);
            at.translate((start - 1) * 17, (double) (fret * 17) - (17.0 / 2.0));
            Area b = new Area(barre);
            b.transform(at);
            a.add(b);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            synchronized (image) {
                g.drawImage(image, 0, 0, Color.gray, this);
            }
        }
    }

}
