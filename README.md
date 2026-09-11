# Guitar Tabs

Program to turn a simple description of a guitar tablature into an image.

## Settings
### Name
The name of the chord. Hash signs will be converted to sharp signs (# -> ♯) and lower case b to flat (b -> ♭). Required.

### Tuning
Alternate tuning to use. Optional, default is EADGBE.

### Fret
The top fret in the chord. Optional, default is 0. For fret 0, the top line representing the nut is thicker, for others it's the same size as all the other lines.

### Numbering
Where the finger numberings are shown. *Embedded* displays them in the fingering dot, *BelowStaff* shows them under the tablature. Default is Embedded.

### ShowNotes
Whether or not to show the note each string is sounding. Default is true.

### Tab
The next lines show what the fingerings should be. Each line shows the fingerings for each fret starting from the top. In the first line open and unplayed strings should be specified as 0 or X. The numbers between 1 and 4 are the fingerings. A hyphen (-) shows the string is not fingered. One or more equals signs (=) between the same number (usually 1) are a barre.

## Examples

### Chord with no options
This tab:
```
Name: E
Tab:
0--100
-32---
```

Produces:

![E chord](examples/e.svg)

### Alternate Tuning
This tab:
```
Name: D
Tuning: DADGAD
Tab:
00--00
---1--
------
--2---
```

Produces:

![D chord in DADGAD tuning](examples/d-dadgad.svg)

Since this is an alternate tuning, the note the strings are tuned to are
in smaller characters just below the grid. The note the string plays is in larger
characters below.

### Barre
This tab:
```
Name: F#
Tab:
------
1====1
---2--
-43---
```

Produces:

![F Sharp Chord](examples/f-sharp.svg)

This shows a barre on the second fret.

### Fret number
This tab:
```
Name: Bb
Fret: 5
Tab:
1====1
------
-43---
```
Produces:

![B Flat Chord](examples/b-flat.svg)

This shows a fret number of 5 to show it starts at the 5th fret below the nut.

### Not Displaying notes
This tab:
```
Name: E
ShowNotes: false
Tab:
0--100
-32---
```
Produces:

![E Chord](examples/e.svg)

This suppresses the display of the notes the strings play.

### Display fingerings below fret board
This tab:
```
Name: Gm
Numbering: BelowStaff
Tab:
XX----
------
---1=1
------
--3---
```
Produces:

![G minor Chord](examples/gm.svg)

This shows the finger numbers below the staff.

## How to Use
Create a Graphics2D implementation, for instance by creating an SVGGeneraor object:
```java
DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
String svgNS = "http://www.w3.org/2000/svg";
Document document = domImpl.createDocument(svgNS, "svg", null);
SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
```
Then read in the tablature string description from a file and parse it to a Tablature object:
```java
List<String> lines = Files.readAllLines(Paths.get("input.tab"), StandardCharsets.UTF_8);
Tablature tablature = Tablature.parse(lines);
```
And write it to a file in SVG format:
```java
Area area = tablature.draw(svgGenerator);
// Scale or translate with AffineTransform if needed
FileOutputStream imageFileStream = new FileOutputStream(imageFileName);
Writer out = new OutputStreamWriter("output.svg", StandardCharsets.UTF_8);
svgGenerator.stream(out, true);
```
