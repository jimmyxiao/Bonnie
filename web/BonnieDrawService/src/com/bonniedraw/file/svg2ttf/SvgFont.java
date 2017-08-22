package com.bonniedraw.file.svg2ttf;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bonniedraw.file.svg2ttf.doubletype.FontFormatWriter;


/**
 * This class represents an SVG font. Caveat: this code can understand only a
 * very limited form of SVG files. These include the SVG files produced by
 * PowerLine, the free SVG slide editor.
 * 
 * This class is part of Svg2Ttf, an SVG to TTF Font Converter. It is based on
 * DoubleType, a graphical typeface designer. Hence, this code is made available
 * under a GNU General Public License as published by the Free Software
 * Foundation.
 * 
 * @author Fabian M. Suchanek
 * 
 */
public class SvgFont {

    public static final String NOPATH = " \u00A0\u00AD";
    public static Pattern glyphPattern = Pattern.compile("<glyph([^>]*)>");
    public static Pattern glyphContentPattern = Pattern
            .compile("unicode='([^']+)' horiz-adv-x='([^']+)'(?: d='([^']+)')?");
    public static Pattern pathPattern = Pattern.compile("([a-zA-Z])\\s*([-0-9\\. ,]++)");
    public static Pattern numberPattern = Pattern.compile("[-0-9\\.]+");
    public static Pattern kernPattern = Pattern.compile("<hkern u1='([^']+)' u2='([^']+)' k='([^']+)'");
    public static Pattern fontPattern = Pattern
            .compile("<font-face (?:font-(?:weight|style)='([^']+)' )?font-family='([^']+)' units-per-em='([^']+)' ascent='([^']+)' descent='([^']+)'");

    /** Holds the paths for the characters */
    protected TreeMap<Character, String> paths = new TreeMap<>();
    /** Holds the widths for the characters */
    protected TreeMap<Character, Double> widths = new TreeMap<>();
    /** Holds the kerning */
    protected TreeMap<Character, Map<Character, Double>> kerns = new TreeMap<>();

    /** Holds the ascent */
    protected double ascent = 550;
    /** Holds the descend */
    protected double descent = -150;
    /** Holds the name */
    protected String name = "Test Font";

    /** Returns the ascent */
    public double getAscent() {
        return ascent;
    }

    /** Returns the descent */
    public double getDescent() {
        return descent;
    }

    /** Returns the name */
    public String getName() {
        return name;
    }

    /** Returns the set of characters */
    public Set<Character> characters() {
        return (paths.keySet());
    }

    /** Returns the SVG path of a character */
    public String pathOf(char c) {
        return (paths.get(c));
    }

    /** Returns the width of a character */
    public double widthFor(char c) {
        return (widths.get(c));
    }

    /** Returns all characters that have following characters with kerning */
    public Set<Character> kernings() {
        return (kerns.keySet());
    }

    /** Returns all following characters that are kerned */
    public Set<Character> kerningsOf(char c) {
        return (kerns.get(c).keySet());
    }

    /** Returns the kerning between two characters */
    public Double kerning(char c1, char c2) {
        Map<Character, Double> map = kerns.get(c1);
        if (map == null) return (null);
        return (map.get(c2));
    }

    /**
     * Loads an SVG font file. This works only for a very limited set of SVG
     * font files, including those produced by PowerLine.
     */
    public SvgFont(File file) throws IOException {
        if (!file.getName().endsWith(".svg")) {
            FontFormatWriter.warning("Not an SVG font file:", file.getCanonicalPath());
            throw new IOException();
        }
        String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        Matcher fontMatcher = fontPattern.matcher(content);
        if (fontMatcher.find()) {
            if (fontMatcher.group(1) == null) name = fontMatcher.group(2);
            else name = fontMatcher.group(2) + " " + fontMatcher.group(1);
            ascent = getDouble(fontMatcher.group(3));
            descent = getDouble(fontMatcher.group(4));
        } else {
            FontFormatWriter.warning("Cound not find pattern",
                    "<font-face font-family='...' units-per-em='...' ascent='...' descent='...'");
        }
        Matcher glypthMatcher = SvgFont.glyphPattern.matcher(content);
        while (glypthMatcher.find()) {
            Matcher glyphContentMatcher = SvgFont.glyphContentPattern.matcher(glypthMatcher.group(1));
            if (!glyphContentMatcher.find()) {
                FontFormatWriter.warning("Glyph content should be unicode='...' horiz-adv-x='...' d='...', and not",
                        glypthMatcher.group());
                continue;
            }
            char c = getChar(glyphContentMatcher.group(1));
            double width = getDouble(glyphContentMatcher.group(2));
            String path = glyphContentMatcher.group(3);
            if (path == null) {
                if (NOPATH.indexOf(c) == -1)
                    FontFormatWriter.warning("No path for character", FontFormatWriter.toString(c));
                path = "";
            }
            if (paths.containsKey(c)) {
                FontFormatWriter.warning("Duplicate character:", FontFormatWriter.toString(c));
                continue;
            }
            paths.put(c, path);
            widths.put(c, width);
        }
        if (paths.isEmpty()) {
            FontFormatWriter.warning("No <glyph> declaration found in file");
        }
        Matcher kernMatcher = kernPattern.matcher(content);
        while (kernMatcher.find()) {
            char c1 = getChar(kernMatcher.group(1));
            char c2 = getChar(kernMatcher.group(2));
            double kern = getDouble(kernMatcher.group(3));
            Map<Character, Double> map = kerns.get(c1);
            if (map == null) kerns.put(c1, map = new TreeMap<Character, Double>());
            map.put(c2, kern);
        }
    }

    /**
     * Transforms an XML character to a char. Works only with numeric
     * references.
     */
    public static char getChar(String s) {
        try {
            if (s.startsWith("&#x")) {
                return ((char) Integer.parseInt(s.substring(3, s.length() - 1), 16));
            }
            if (s.startsWith("&#")) {
                return ((char) Integer.parseInt(s.substring(2, s.length() - 1), 10));
            }
            if (s.length() == 1) {
                return (s.charAt(0));
            }
        } catch (NumberFormatException e) {
        }
        FontFormatWriter.warning("Cannot parse char", s);
        return (0);
    }

    /** Returns the double value of a string */
    public static double getDouble(String s) {
        try {
            return (Double.parseDouble(s));
        } catch (NumberFormatException e) {
            FontFormatWriter.warning("Unparsable number:", s);
            return (0);
        }

    }
}
