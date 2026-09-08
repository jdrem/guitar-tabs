package net.remgant.music;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 * User: jdr
 * Date: May 19, 2010
 * Time: 7:18:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class DrawTabs {
    static void main(String[] args) throws Exception {
        new DrawTabs(args).run();
    }

    private final String imageFileName;
    private final String inputFileName;

    public DrawTabs(String[] args) {
        if (args.length >= 2) {
            inputFileName = args[0];
            imageFileName = args[1];
        } else if (args.length == 1) {
            inputFileName = null;
            imageFileName = args[0];
        } else {
            throw new RuntimeException("must specify at least one argument");
        }
    }

    Tablature sampleTab = Tablature.parse(new String[]{
            "Name: E",
            "Tuning: DADGAD",
            "Fret: 12",
            "Tab:",
            "X01==1",
            "---32-"});

    public void run() throws Exception {
        List<Tablature> tabs;
        if (inputFileName != null){
            tabs = readFile(inputFileName);
        } else {
            tabs = List.of(
                    Tablature.parse(new String[]{"Name: F",
                            "Tab:",
                            "XX--11",
                            "---2--",
                            "--3---"}),
                    Tablature.parse(new String[]{"Name: E",
                            "Tuning: EADGBE",
                            "Fret: 0",
                            "Tab:",
                            "0--100",
                            "-32---"}),
                    Tablature.parse(new String[]{"Name: C7",
                            "Fret: 7",
                            "Tab:",
                            "1====1",
                            "---2--",
                            "-3----"}),
                    Tablature.parse(new String[]{"Name: A9",
                            "Fret: 3",
                            "Tab:",
                            "-1=1-X",
                            "4-3-2-"}),
                    Tablature.parse(new String[]{"Name: D",
                            "Tuning: DADGAD",
                            "Tab:",
                            "00--00",
                            "---1--",
                            "------",
                            "--2---"}),
                    Tablature.parse(new String[]{"Name: Gm",
                            "Tab:",
                            "XX----",
                            "------",
                            "---1=1",
                            "------",
                            "--3---"}),
                    Tablature.parse(new String[]{"Name: F#",
                            "Tab:",
                            "------",
                            "1====1",
                            "---2--",
                            "-43---"}),
                    Tablature.parse(new String[]{"Name: Bb",
                            "Fret: 5",
                            "Tab:",
                            "1====1",
                            "------",
                            "-43---"}),
                    Tablature.parse(new String[]{"Name: D",
                            "Tab:",
                            "XX0---",
                            "---2-1",
                            "----3-"}));
        }

        DOMImplementation domImpl =
                GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        if (!Tablature.isDefaultFontUnicode()) {
            String osName = System.getProperty("os.name");
            if (osName.equals("Linux"))
                Tablature.setDefaultFont("DejaVu Sans");
        }
        Area sample = sampleTab.draw(svgGenerator);
        System.out.println(sample.getBounds());
        double margin = Math.min(sample.getBounds2D().getWidth(), sample.getBounds2D().getHeight()) / 2.0;

        double x = margin;
        double y = margin;
        int columns = 3;
        int colCnt = 0;
        for (Tablature t : tabs) {
            Area a = t.draw(svgGenerator);
            AffineTransform at = AffineTransform.getTranslateInstance(x, y);
            a.transform(at);
            svgGenerator.fill(a);
            colCnt++;
            if (colCnt >= columns) {
                x = margin;
                y += sample.getBounds2D().getHeight() * 1.25;
                colCnt = 0;
            } else {
                x += sample.getBounds2D().getWidth() * 1.25;
            }
        }

        FileOutputStream imageFileStream = new FileOutputStream(imageFileName);
        Writer out = new OutputStreamWriter(imageFileStream, StandardCharsets.UTF_8);
        svgGenerator.stream(out, true);
        }

    public java.util.List<Tablature> readFile(String fileName) throws IOException {
        java.util.List<Tablature> list = new ArrayList<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String line = in.readLine();
        java.util.List<String> strList = null;
        while (line != null) {
            if (line.isEmpty()) {
                list.add(Tablature.parse(Objects.requireNonNull(strList)));
                strList = null;
                line = in.readLine();
                continue;
            }
            if (strList == null)
                strList = new ArrayList<>();
            strList.add(line);
            line = in.readLine();
        }
        if (strList != null)
            list.add(Tablature.parse(strList));
        return list;
    }
}
