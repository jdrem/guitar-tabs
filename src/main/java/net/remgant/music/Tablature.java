package net.remgant.music;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tablature
{
    static String fontName = "SansSerif";

    enum TabSymbols {DOT, BR_ST, BR, BR_END}
    String name;
    String[] tuning;
    int fret;
    TabSymbols tab[][];
    char finger[];
    /*
   Name: E
   Tuning: EADGBE
   Fret: 0
   Tab:
   0--100
   -32---

   Name: A
   Tab:
   X0---0
   --321-

   Name: Gm
   Tab:
   XX----
   ------
   ---111
   ------
   --3---

   Name: C7
   Fret: 8
   Tab:
   1====1
   ---2--
   -3----

   Name: Gm
   Fret: 3
   Tab:
   1====1
   ------
   -32---

    Name: A9
    Fret: 4
    Tab:
    -1=1-X
    4-3-2-

    Name: A
    Tuning: DADGAD
    Tab:
    X0----
    --1==1
    ------
    ----3-

    Name: D
    Tuning: DADGAD
    Tab:
    00--00
    ---1--
    ------
    --2---
    */

    protected Tablature()
    {
         finger = new char[6];
    }
    static Pattern namePtrn = Pattern.compile("Name:\\s+(.*)");
    @SuppressWarnings({"UnusedDeclaration"})
    static Pattern tuningPtrn = Pattern.compile("Tuning:\\s+((?:[A-G][#b]?){6})");
    static Pattern notePtnr = Pattern.compile("([A-G][\u266f\u266d]?)");
    static Pattern fretPtrn = Pattern.compile("Fret:\\s+(\\d+)");
    static Pattern tabPattern = Pattern.compile("([=X01234//-]{6})");
    static public Tablature parse(String input[])
    {
        Tablature t = new Tablature();
        List<TabSymbols[]> tabArray = new ArrayList<TabSymbols[]>();
        boolean inTab = false;
        for (String line : input)
        {
           if (!inTab)
           {
               Matcher m = namePtrn.matcher(line);
               if (m.matches())
               {
                   t.name = m.group(1).replace('#','\u266f');
                   t.name = t.name.replace('b','\u266d');
               }
               m = fretPtrn.matcher(line);
               if (m.matches())
                   t.fret = Integer.parseInt(m.group(1));
               m = tuningPtrn.matcher(line);
               if (m.matches() && !m.group(1).equals("EADGBE"))
               {
                   String tt = m.group(1).replace('#','\u266f');
                   tt = tt.replace('b','\u266d');
                   Matcher mm = notePtnr.matcher(tt);
                   t.tuning = new String[6];
                   int i=0;
                   while (mm.find())
                       t.tuning[i++] = mm.group(1);
               }

           }
           if (!inTab && line.equals("Tab:"))
           {
               inTab = true;
           }
            else if (inTab)
           {
               Matcher m = tabPattern.matcher(line);
               if (m.matches())
               {
                   String s = m.group(1);
                   Character c[] = new Character[]{' ',' ',' ',' ',' ',' '};
                   TabSymbols ts[] = new TabSymbols[6];
                   for (int j = 0; j<6; j++)
                   {
                       c[j] = s.charAt(j);
                       if (c[j] != '-' && c[j] != '=')
                       {
                           t.finger[j] = c[j];
                       }
                       else if (c[j] == '=')
                       {
                           t.finger[j] = '1';
                           ts[j] = TabSymbols.BR;
                       }
                       if (c[j] == '1')
                       {
                           if (j < 5 && s.charAt(j+1) == '=')
                               ts[j] = TabSymbols.BR_ST;
                           else if (j > 0 && s.charAt(j-1) == '=')
                               ts[j] = TabSymbols.BR_END;
                           else if (j > 0 && s.charAt(j-1) == '1')
                           {
                               ts[j-1] = TabSymbols.BR_ST;
                               ts[j] = TabSymbols.BR_END;
                           }
                           else
                               ts[j] = TabSymbols.DOT;

                       }
                       if (c[j] == '2' || c[j] == '3' || c[j] == '4')
                       {
                            ts[j] = TabSymbols.DOT;
                       }
                   }
                   tabArray.add(ts);
               }
               else
               {
                   System.out.println("no match: "+line);
               }
           }
       }

        t.tab = new TabSymbols[tabArray.size()][];
        int i = 0;
        for (TabSymbols ts[] : tabArray)
        {
            t.tab[i++] = ts;
        }
        return t;
    }

    public static Tablature parse(List<String> list)
    {
        String[] array = list.toArray(new String[0]);
        return parse(array);
    }
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (char c : finger)
        {
            sb.append(c);
            sb.append(" ");
        }
        sb.append("\n");
        for (TabSymbols tsa[] : tab)
        {
            for (TabSymbols ts : tsa)
            {
                sb.append(ts);
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public Area draw(Graphics2D g)
    {
        Area a = new Area();
        int x = 0;
        int y = 0;
        int lineWidth = 1;
        int h = 85 + lineWidth;
        int w = 85 + lineWidth;
        //Font font = Font.decode("Helvetica");
        Font font = Font.decode(fontName);
        FontRenderContext frc = g.getFontRenderContext();
        font = font.deriveFont(18.0f);
        GlyphVector gv = font.createGlyphVector(frc,name);
        Area n = new Area(gv.getOutline());
        n.transform(AffineTransform.getTranslateInstance(-n.getBounds2D().getX(),-n.getBounds2D().getY()));
        double hh = n.getBounds2D().getWidth();
        double xxx = (86.0 - hh) / 2.0;
        AffineTransform at = AffineTransform.getTranslateInstance(xxx,0);
        n.transform(at);
        a.add(n);
        y += 2 * n.getBounds().getHeight();

        font = font.deriveFont(12.0f);
        Area X = new Area(font.createGlyphVector(frc,"X").getOutline());
        X.transform(AffineTransform.getTranslateInstance(-(X.getBounds2D().getWidth()/2.0),(double)y));
        Area O = new Area(font.createGlyphVector(frc,"O").getOutline());
        O.transform(AffineTransform.getTranslateInstance(-(X.getBounds2D().getWidth()/2.0),(double)y));
        for (int i=0; i<finger.length; i++)
        {

            if (finger[i] == 'X')
            {
               a.add(X);
            }
            if (finger[i] == 'O' || finger[i] == '0')
            {
                a.add(O);
            }
            X.transform(AffineTransform.getTranslateInstance(17.0,0.0));
            O.transform(AffineTransform.getTranslateInstance(17.0,0.0));
        }

        y += (int)(X.getBounds2D().getHeight() * 0.25);

        if (fret == 0)
        {
            a.add(new Area(new Rectangle2D.Float((float)x,(float)y,(float)w,(float)(lineWidth*2))));
            y += lineWidth * 2;
        }
        else
        {
            font = font.deriveFont(16.0f);
            Area f = new Area(font.createGlyphVector(frc,Integer.toString(fret)+"fr.").getOutline());
            f.transform(AffineTransform.getTranslateInstance((double)(w+3),y+f.getBounds2D().getHeight()));
            a.add(f);
        }
        int xx = x;
        int yy = y;
        for (int i=0; i<6; i++)
        {
            Rectangle2D.Float rx = new Rectangle.Float((float)xx,(float)y,
						     (float)lineWidth,(float)h);
            Rectangle2D.Float ry = new Rectangle.Float((float)x,(float)yy,
						     (float)w,(float)lineWidth);
            xx += 17;
            yy += 17;
            a.add(new Area(rx));
            a.add(new Area(ry));
         }
        for (int i=0; i< tab.length; i++)
        {
            int fret = i+1;
            for (int j=0; j<6; j++)
            {
                int string = j+1;
                if (tab[i][j] != null)
                {
                    printDot(a,string,fret,x,y);
                }
                if (tab[i][j] == TabSymbols.BR_ST)
                {
                    int barreEnd = 0;
                    for (int k=j; k<6; k++)
                    {
                        if (tab[i][k] == TabSymbols.BR_END)
                        {
                            barreEnd = k+1;
                            break;
                        }
                    }
                    if (barreEnd > 0)
                        printBarre(a,string,barreEnd,fret,x,y);
                }
            }
        }

        y += 5 * 17;

        if (tuning != null)
        {
            Area l = new Area(font.createGlyphVector(frc,"E").getOutline());
            y += l.getBounds().getHeight();
            double xAdj = l.getBounds2D().getWidth() / 2.0;
           font = font.deriveFont(8.0f);
            for (int i=0; i<6; i++)
            {
                l = new Area(font.createGlyphVector(frc,tuning[i]).getOutline());
                AffineTransform lt = AffineTransform.getTranslateInstance((double)(i*17)-xAdj,(double)y);
                l.transform(lt);
                a.add(l);
            }

            y += 4;
        }


        font = font.deriveFont(12.0f);
        Area one = new Area(font.createGlyphVector(frc,"1").getOutline());
        // y += 5 * 17 + (int)(one.getBounds2D().getHeight() * 1.33);
        y += (int)(one.getBounds2D().getHeight() * 1.33);
        one.transform(AffineTransform.getTranslateInstance(-(one.getBounds2D().getWidth()/2.0),(double)y));
        Area two = new Area(font.createGlyphVector(frc,"2").getOutline());
        two.transform(AffineTransform.getTranslateInstance(-(two.getBounds2D().getWidth()/2.0),(double)y));
         Area three = new Area(font.createGlyphVector(frc,"3").getOutline());
        three.transform(AffineTransform.getTranslateInstance(-(three.getBounds2D().getWidth()/2.0),(double)y));
         Area four = new Area(font.createGlyphVector(frc,"4").getOutline());
        four.transform(AffineTransform.getTranslateInstance(-(four.getBounds2D().getWidth()/2.0),(double)y));

          for (int i=0; i<finger.length; i++)
        {
            switch(finger[i])
            {
            case '1':
                a.add(one);
                break;
            case '2':
                a.add(two);
                break;
            case '3':
                a.add(three);
                break;
            case '4':
                a.add(four);
                break;
            }
             one.transform(AffineTransform.getTranslateInstance(17.0,0.0));
            two.transform(AffineTransform.getTranslateInstance(17.0,0.0));
            three.transform(AffineTransform.getTranslateInstance(17.0,0.0));
            four.transform(AffineTransform.getTranslateInstance(17.0,0.0));
        }

        showNotes(a,font,frc,x,y);

        at = AffineTransform.getTranslateInstance(-a.getBounds2D().getX(),-a.getBounds2D().getY());
        a.transform(at);
        return a;
    }

    @Deprecated
    public void draw(Graphics2D g,int x, int y)
    {
        Area a = new Area();

        int lineWidth = 1;
        int h = 85 + lineWidth;
        int w = 85 + lineWidth;

        int xx = x;
        int yy = y;
        for (int i=0; i<6; i++)
        {
            Rectangle2D.Float rx = new Rectangle.Float((float)xx,(float)y,
						     (float)lineWidth,(float)h);
            Rectangle2D.Float ry = new Rectangle.Float((float)x,(float)yy,
						     (float)w,(float)lineWidth);
            xx += 17;
            yy += 17;
            a.add(new Area(rx));
            a.add(new Area(ry));
         }
        for (int i=0; i< tab.length; i++)
        {
            int fret = i+1;
            for (int j=0; j<6; j++)
            {
                int string = j+1;
                if (tab[i][j] != null)
                {
                    printDot(a,string,fret,x,y);
                }
                if (tab[i][j] == TabSymbols.BR_ST)
                {
                    int barreEnd = 0;
                    for (int k=j; k<6; k++)
                    {
                        if (tab[i][k] == TabSymbols.BR_END)
                        {
                            barreEnd = k+1;
                            break;
                        }
                    }
                    if (barreEnd > 0)
                        printBarre(a,string,barreEnd,fret,x,y);

                }
            }
        }
        g.setColor(Color.BLACK);
        g.fill(a);
    }
    @Deprecated
    public void draw(Area a,int x, int y)
    {
        int lineWidth = 1;
        int h = 85 + lineWidth;
        int w = 85 + lineWidth;

        int xx = x;
        int yy = y;
        for (int i=0; i<6; i++)
        {
            Rectangle2D.Float rx = new Rectangle.Float((float)xx,(float)y,
						     (float)lineWidth,(float)h);
            Rectangle2D.Float ry = new Rectangle.Float((float)x,(float)yy,
						     (float)w,(float)lineWidth);
            xx += 17;
            yy += 17;
            a.add(new Area(rx));
            a.add(new Area(ry));
         }
        for (int i=0; i< tab.length; i++)
        {
            int fret = i+1;
            for (int j=0; j<6; j++)
            {
                int string = j+1;
                if (tab[i][j] != null)
                {
                    printDot(a,string,fret,x,y);
                }
                if (tab[i][j] == TabSymbols.BR_ST)
                {
                    int barreEnd = 0;
                    for (int k=j; k<6; k++)
                    {
                        if (tab[i][k] == TabSymbols.BR_END)
                        {
                            barreEnd = k+1;
                            break;
                        }
                    }
                    if (barreEnd > 0)
                        printBarre(a,string,barreEnd,fret,x,y);

                }
            }
        }
    }
       private void printDot(Area a,int string,int fret,int xOffset, int yOffset)
        {
            Ellipse2D.Float dot = new Ellipse2D.Float(-5.0f,-5.0f,10.0f,10.0f);
            AffineTransform at = new AffineTransform();
            at.translate((double)xOffset,(double)yOffset);
            at.translate((double)((string-1)*17),(double)(fret * 17)-(17.0/2.0));
            Area d = new Area(dot);
            d.transform(at);
            a.add(d);
        }

        private void printBarre(Area a, int start, int end, int fret, int xOffset,int yOffset)
        {
            for (int i=0; i<end-start; i++)
            {
                printDot(a,start+i,fret,xOffset,yOffset);
            }

            Area e1 = new Area(new Ellipse2D.Float(-8.5f,-8.5f,17.0f,17.0f));
            Area e2 = new Area(new Ellipse2D.Float(-8.5f,-5.5f,17.0f,17.0f));
            e1.subtract(e2);
            AffineTransform at;
            int scaleFactor = end - start;
            if (scaleFactor > 1)
            {
                at = AffineTransform.getScaleInstance((double)scaleFactor,1.0);
                e1.transform(at);
            }
            at = AffineTransform.getTranslateInstance(xOffset+e1.getBounds2D().getWidth()/2.0+(start-1)*17.0,
                                                      yOffset-2.0+((fret-1)*17.0));
            e1.transform(at);
            a.add(e1);
        }

        private void printBarreOld(Area a, int start, int end, int fret, int xOffset,int yOffset)
        {
            printDot(a,start,fret,xOffset,yOffset);
            printDot(a,end,fret,xOffset,yOffset);
            int w = (end - start) * 17;
            Rectangle2D.Float barre = new Rectangle2D.Float(0.0f,-5.0f,(float)w,10.0f);
            AffineTransform at = new AffineTransform();
            at.translate((double)xOffset,(double)yOffset);
            at.translate((double)((start-1)*17),(double)(fret * 17)-(17.0/2.0));
            Area b = new Area(barre);
            b.transform(at);
            a.add(b);
        }
    static String sharp = "\u266f";
    static String flat = "\u266d";
    static String noteList[] = new String[]{"A","B"+flat,"B","C","C"+sharp,"D","E"+flat,"E","F","F"+sharp,"G","G"+sharp};
    private String findNote(String note,int steps)
    {
        int idx = -1;
        for (int i=0; i<noteList.length; i++)
        {
            if (noteList[i].equals(note))
            {
               idx = i;
                break;
            }
        }

        if (idx < 0)
            return null;
        idx += steps;
        idx %= 12;
        return noteList[idx];
    }
    private void showNotes(Area a, Font font, FontRenderContext frc, int xOffset, int yOffset)
    {
        String note[] = new String[]{" "," "," "," "," "," "};
        if (tuning == null)
        {
            tuning = new String[]{"E","A","D","G","B","E"};
        }

        for (int i=0; i<finger.length; i++)
        {
              System.out.print(finger[i]+" ");
              if (finger[i] == '0' || finger[i] == 'O')
                  note[i] = tuning[i];
        }
         System.out.println();
        for (int i=0; i<tab.length; i++)
        {

            for (int j=0; j<tab[i].length; j++)
            {
                System.out.print(tab[i][j]+" ");
                if (tab[i][j] != null)
                {
                    note[j] = findNote(tuning[j],i+(fret==0?1:fret));
                }
            }
            System.out.println();
        }
        for (int i=0; i<note.length; i++)
        {
            System.out.print(note[i]+" ");
        }
        System.out.println();

        Area l = new Area(font.createGlyphVector(frc,"E").getOutline());
        yOffset += 2.0 + l.getBounds().getHeight();
        double xAdj = l.getBounds2D().getWidth() / 2.0;
        font = font.deriveFont(12.0f);
        for (int i=0; i<6; i++)
        {
                l = new Area(font.createGlyphVector(frc,note[i]).getOutline());
                AffineTransform lt = AffineTransform.getTranslateInstance((double)(i*17)-xAdj,(double)yOffset);
                l.transform(lt);
                a.add(l);
        }


    }
    public static void main(String args[])
    {
        String e[] = {"Name: E",
                      "Tuning: EADGBE",
                      "Fret: 0",
                      "Tab:",
                      "0--100",
                      "-32---"};
        @SuppressWarnings({"UnusedDeclaration"}) Tablature t = Tablature.parse(e);
    }

    public static void setDefaultFont(String fontName)
    {
        Tablature.fontName = fontName;
    }

    public static boolean isDefaultFontUnicode()
    {
        Font f = Font.decode(fontName);
        return f.canDisplay('\u266f') && f.canDisplay('\u266d');
    }
}
