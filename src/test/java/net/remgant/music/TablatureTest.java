package net.remgant.music;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TablatureTest {
    @Test
    public void test1() throws IOException {
        Tablature tablature = Tablature.parse(new String[]{
                "Name: E",
                "Tuning: EADGBE",
                "Fret: 0",
                "Tab:",
                "0--100",
                "-32---"
        });
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();
        Area area = tablature.draw(graphics2D);
        AffineTransform transform = AffineTransform.getScaleInstance(1.5, 1.5);
        area.transform(transform);
        image = new BufferedImage(area.getBounds().width+1, area.getBounds().height+1, BufferedImage.TYPE_INT_ARGB);
        graphics2D = image.createGraphics();
        graphics2D.setColor(Color.WHITE);
        graphics2D.fill(new Rectangle2D.Double(0.0, 0.0,  area.getBounds2D().getWidth(), area.getBounds2D().getHeight()));
        graphics2D.setColor(Color.BLACK);
        graphics2D.fill(area);
        System.out.println(area.getBounds2D());
        ImageIO.write(image,"PNG", new File("e.png"));
    }
}
