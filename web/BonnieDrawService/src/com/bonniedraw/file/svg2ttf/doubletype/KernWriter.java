package com.bonniedraw.file.svg2ttf.doubletype;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class writes the kerning table to the TTF font.
 * 
 * This class is part of Svg2Ttf, an SVG to TTF Font Converter. It is based on
 * DoubleType, a graphical typeface designer. Hence, this code is made available
 * under a GNU General Public License as published by the Free Software
 * Foundation.
 * 
 * @author Fabian M. Suchanek
 * 
 */
public class KernWriter extends FontFormatWriter {

    /** We have a cross reference here */
    protected CmapWriter cmapWriter;

    /** Constructs a kern writer */
    public KernWriter(CmapWriter cmapWriter) {
        super();
        this.cmapWriter = cmapWriter;
    }

    /** Maps each kerned character to the following character and the kerning */
    protected Map<Character, Map<Character, Integer>> kerning = new TreeMap<>();

    /** Total number of mappings */
    protected int numKern = 0;

    /** Add a kerning */
    public void addKerning(char c1, char c2, int kern) {
        Map<Character, Integer> map = kerning.get(c1);
        if (map == null) kerning.put(c1, map = new TreeMap<Character, Integer>());
        if (map.put(c2, kern) == null) numKern++;
    }

    /** Main method */
    public void write() throws IOException {
        // We use the Microsoft format here so that this will work on Windows,
        // too

        // Version
        writeUInt16(0);
        // NumTables
        writeUInt16(1);

        // Subtable version number
        writeUInt16(0);
        // Length of table, including this header
        writeUInt16(7 * 2 + numKern * 3 * 2);
        // Coverage
        writeUInt16(1);
        // Number of pairs
        writeUInt16(numKern);
        // Search Range
        writeUInt16(getSearchRange(numKern) * 6);
        // entrySelector
        writeUInt16(getEntrySelector(numKern));
        // rangeShift
        writeUInt16((numKern - getSearchRange(numKern)) * 6);
        int counter = 0;
        for (Character c1 : kerning.keySet()) {
            Map<Character, Integer> map = kerning.get(c1);
            for (Character c2 : map.keySet()) {
                int i1 = (int) cmapWriter.getGlyfIndex((long) c1);
                if (i1 == 0) {
                    warning("Undefined first character in kerning:", toString(c1));
                }
                writeUInt16(i1);
                int i2 = (int) cmapWriter.getGlyfIndex((long) c2);
                if (i2 == 0) {
                    warning("Undefined second character in kerning:", toString(c2));
                }
                writeUInt16(i2);
                int kern = map.get(c2);
                writeFWord(kern);
                counter++;
            }
        }
        if (counter != numKern) {
            warning("Kern counting problem:", counter, numKern);
        }
        pad();
    }

    /** Compute Search Range as defined by TTF */
    public static int getSearchRange(int a_value) {
        int retval = (int) (Math.pow(2, Math.floor(Math.log(a_value) / Math.log(2))));
        return retval;
    }

    /** Compute Entry Selector as defined by TTF */
    public static int getEntrySelector(int a_value) {
        int retval = (int) Math.floor(Math.log(a_value) / Math.log(2));
        return retval;
    }

    protected String getTag() {
        return "kern";
    }
}
