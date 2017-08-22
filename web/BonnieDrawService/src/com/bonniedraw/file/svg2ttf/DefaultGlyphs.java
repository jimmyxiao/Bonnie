package com.bonniedraw.file.svg2ttf;

import java.awt.Point;

import com.bonniedraw.file.svg2ttf.doubletype.TTGlyph;




/**
 * This class contains some default glyphs. Caveat: the scaling might not work.
 * 
 * This class is part of Svg2Ttf, an SVG to TTF Font Converter. It is based on
 * DoubleType, a graphical typeface designer. Hence, this code is made available
 * under a GNU General Public License as published by the Free Software
 * Foundation.
 * 
 * @author Fabian M. Suchanek
 * 
 */

public class DefaultGlyphs {

    /** Returns the undefined glyph */
    public static TTGlyph undef() {
        TTGlyph g = new TTGlyph();
        g.setAdvanceWidth(200 * Svg2Ttf.scale);
        g.addPoint(new Point(0, 200 * Svg2Ttf.scale));
        g.addFlag(1);
        g.addPoint(new Point(200 * Svg2Ttf.scale, 200 * Svg2Ttf.scale));
        g.addFlag(1);
        g.addPoint(new Point(200 * Svg2Ttf.scale, 0));
        g.addFlag(1);
        g.addPoint(new Point(0, 0));
        g.addFlag(1);
        g.addEndPoint(3);
        return (g);
    }

    /** Returns the NULL glyph */
    public static TTGlyph nullGlyph() {
        TTGlyph g = new TTGlyph();
        g.setAdvanceWidth(0);
        return (g);
    }

    /** Returns the SPACE glyph */
    public static TTGlyph spaceGlyph() {
        TTGlyph g = new TTGlyph();
        g.setAdvanceWidth(200 * Svg2Ttf.scale);
        return (g);
    }

}
