package com.bonniedraw.file.svg2ttf;
import java.io.File;
import java.io.RandomAccessFile;

import com.bonniedraw.file.svg2ttf.doubletype.FontFileWriter;
import com.bonniedraw.file.svg2ttf.doubletype.TTCodePage;
import com.bonniedraw.file.svg2ttf.doubletype.TTGlyph;
import com.bonniedraw.file.svg2ttf.doubletype.TTUnicodeRange;


/**
 * This class converts an SVG font to a TTF font -- at least in theory. In
 * practice, the converter can understand only a very limited form of SVG files.
 * These include the SVG files produced by PowerLine, the free SVG slide editor.
 * Unfortunately, this code produces faulty TTF files, which work only on Mac. To
 * use the TTF files under Windows, use any converter to convert TTF to TTF (to
 * the same file format), because this usually cleans out errors.
 * 
 * This class is part of Svg2Ttf, an SVG to TTF Font Converter. It is based on
 * DoubleType, a graphical typeface designer. Hence, this code is made available
 * under a GNU General Public License as published by the Free Software
 * Foundation.
 * 
 * @author Fabian M. Suchanek
 * 
 */
public class Svg2Ttf {
    /**
     * Scaling for the SVG sizes. Not sure what value is good here in general. 2 works fine.
     */
    public static final int scale = 2;

    /** Adds a glyph */
    protected static boolean addGlyph(char c, SvgFont svgFont, FontFileWriter writer) {
        // Space character is already there
        if (c == ' ') return (false);
        TTUnicodeRange range = TTUnicodeRange.of(c);
        if (range != null) writer.addUnicodeRange(range);
        TTGlyph glyph = GlyphCreator.makeGlyph(c, svgFont);
        if (glyph == null) return (false);
        int index = writer.addGlyph(glyph);
        writer.addCharacterMapping(c, index);
        return (true);
    }

    /**
     * Converts an SVG font file to TTF
     */
    public static void convert(File fol, String designer, String designerUrl) throws Exception {
        System.out.println("Converting " + fol.getCanonicalPath());
        SvgFont svgFont = new SvgFont(fol);
        try (RandomAccessFile out = new RandomAccessFile(fol.toString().replaceAll(".svg$", ".ttf"), "rw")) {
            FontFileWriter writer = new FontFileWriter(out);
            writer.setAscent((int) (svgFont.getAscent() * scale));
            writer.setDescent((int) (svgFont.getDescent() * -scale));
            writer.setLineGap(0 * scale);
            writer.setOffset(0);
            writer.setXHeight((int) (svgFont.getAscent() * 8 / 10 * scale));
            writer.setCodeRangeFlag(TTCodePage.forName("US-ASCII").getOsTwoFlag());
            writer.setCodeRangeFlag(TTCodePage.forName("windows-1252").getOsTwoFlag());
            writer.setCodeRangeFlag(TTCodePage.forName("windows-1251").getOsTwoFlag());
            writer.setNames(svgFont.getName(), designer, designerUrl);
            // We put the characters here in a specific order to comply
            // with the POST 1 table convention. However, we will finally use
            // POST 3.
            // At position 0, there must be a special character "undefined"
            writer.addCharacterMapping(TTUnicodeRange.k_notDef, writer.addGlyph(DefaultGlyphs.undef()));
            // At position 1, we want the NULL character
            writer.addCharacterMapping(TTUnicodeRange.k_null, writer.addGlyph(DefaultGlyphs.nullGlyph()));
            // At position 2, we want the CR character, which we render like
            // space
            TTGlyph space = GlyphCreator.makeGlyph(' ', svgFont);
            if (space == null) space = DefaultGlyphs.spaceGlyph();
            writer.addCharacterMapping(TTUnicodeRange.k_cr, writer.addGlyph(space));
            // At position 3, we want the SPACE character
            writer.addCharacterMapping(TTUnicodeRange.k_space, writer.addGlyph(space));
            // Now put the basic Latin ones
            for (char c = 0x20; c < 0x80; c++) {
                addGlyph(c, svgFont, writer);
            }
            // Now put all the other characters in order
            for (char c : svgFont.characters()) {
                // Basic Latin is already there
                if (c >= 0x20 && c < 0x80) continue;
                addGlyph(c, svgFont, writer);
            }
            // Add all the kernings
            for (char c1 : svgFont.kernings()) {
                for (char c2 : svgFont.kerningsOf(c1)) {
                    // Watch out, these have to be negative!
                    writer.addKern(c1, c2, svgFont.kerning(c1, c2).intValue() * -scale);
                }
            }
            writer.write();
        }
        System.out.println("done");
    }

    /**
     * Converts a given SVG font file to TTF
     */
//    public static void main(String[] args) throws Exception {
//        //args = new String[] { "/Users/suchanek/Sync/Homepage/programs/fabiana/Fabiana/Fabiana-italic-font.svg", "Fabian M. Suchanek", "http://suchanek.name" };
//        if (args == null || args.length != 3) {
//            System.out.println("Converts an SVG font file generated by PowerLine to TTF\n");
//            System.out.println("Call: Svg2ttf file.svg designer designerUrl");
//            System.exit(63);
//        }
//        convert(new File(args[0]), args[1], args[2]);
//    }
}
