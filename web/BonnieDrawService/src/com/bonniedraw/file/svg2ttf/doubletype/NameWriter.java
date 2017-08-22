/*
 * $Id: NameWriter.java,v 1.9 2004/06/16 07:02:52 eed3si9n Exp $
 * 
 * $Copyright: copyright (c) 2003, e.e d3si9n $
 * $License: 
 * This source code is a modified part of DoubleType.
 * DoubleType is a graphical typeface designer.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This Program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * In addition, as a special exception, e.e d3si9n gives permission to
 * link the code of this program with any Java Platform that is available
 * to public with free of charge, including but not limited to
 * Sun Microsystem's JAVA(TM) 2 RUNTIME ENVIRONMENT (J2RE),
 * and distribute linked combinations including the two.
 * You must obey the GNU General Public License in all respects for all 
 * of the code used other than Java Platform. If you modify this file, 
 * you may extend this exception to your version of the file, but you are not
 * obligated to do so. If you do not wish to do so, delete this exception
 * statement from your version.
 * $
 */

package com.bonniedraw.file.svg2ttf.doubletype;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author e.e and Fabian M. Suchanek (marked with "FMS")
 */
public class NameWriter extends FontFormatWriter {
    public static final String k_regular = "Regular";
    private static final String k_utf16be = "UTF-16BE";
    private static final String k_iso8859_1 = "ISO-8859-1";

    // FMS: Let's make this customizable

//    String m_copyright = "\u00A9 Copyright";
//    String m_familyName = "Temp";
//    String m_subFamilyName = k_regular;
//    String m_unique = "eed3si9n: Temp Regular: 2003";
//    String m_fullFontName = "Temp";
//    String m_version = "0.00";
//    String m_psName = "Temp";
//    String m_tradeMark = "";
//    String m_manufacturer = "eed3si9n";
//    String m_year = "2004";
    String m_sample = "The quick brown fox jumps over the lazy dog.";

    protected String fontName="Test";
    protected String designer=null;
    protected String designerUrl=null;
    protected String license = "CC-BY";
    protected String licenseUrl = "http://creativecommons.org/licenses/by/4.0/";

    private ArrayList<TTName> m_names = new ArrayList<TTName>();

    public NameWriter() {
        super();
    }

    public void setNames(String fontName, String designer, String designerUrl) {
        this.fontName = fontName;
        this.designer = designer;
        this.designerUrl = designerUrl;
    }

    private void prepare() {
        // FMS: Let's take the input form setNames()
/*
        m_copyright = "\u00A9 Copyright " + m_year + ", " + m_manufacturer + ".";
        m_unique = "dtype: " + m_manufacturer + ": " + m_familyName + " " + m_subFamilyName + ": " + "Version "
                + m_version + ": " + m_year;
        m_fullFontName = m_familyName;
        m_psName = m_fullFontName;

        if (m_tradeMark.length() == 0) {
            m_tradeMark = "n/a";
        } // if

*/        m_names.clear();
        addNames();
    }

    private void addNames() {
        // if (designer != null) addName(0, "\u00A9 " + designer);
        // addName(1, "sans-serif");
        // addName(2, "Regular");
        // addName(3, fontName+" Regular");
        // if (fontName != null) addName(4, fontName);
        // addName(5,"Version "+Calendar.getInstance());
        // if (fontName != null) addName(6,
        // fontName.replaceAll("[A-Za-z0-9]", ""));
        // addName(7, ""); // Trademark
        // addName(8, ""); // Manufacturer
        // if (designer != null) addName(9, designer);
        // if (designerUrl != null) addName(12, designerUrl);
        // if (designer != null) addName(13, license);
        // if (designer != null) addName(14, licenseUrl);

        Calendar now=Calendar.getInstance();
        int year=now.get(Calendar.YEAR);
        int month=now.get(Calendar.MONTH);
        int day=now.get(Calendar.DAY_OF_MONTH);
        addName(0, "\u00A9 "+year+" "+designer);
        addName(1, fontName);
        addName(2, "regular");
        addName(3, fontName+" regular");
        addName(4, fontName);
        addName(5, "Version "+year+"-"+month+"-"+day);
        addName(6, fontName.replaceAll("[^A-Za-z0-9]", ""));
        addName(7, "n/a");
        addName(8, "n/a");
        if (designer != null) addName(9, designer);
        if (designerUrl != null) addName(12, designerUrl);
        if (designer != null) addName(13, license);
        if (designer != null) addName(14, licenseUrl);
        addName(19, m_sample);

        // addMicrosoftUnicodeEnglish(0, m_copyright);
        // addMicrosoftUnicodeEnglish(1, m_familyName);
        // addMicrosoftUnicodeEnglish(2, m_subFamilyName);
        // addMicrosoftUnicodeEnglish(3, m_unique);
        // addMicrosoftUnicodeEnglish(4, m_fullFontName);
        // addMicrosoftUnicodeEnglish(5, m_version);
        // addMicrosoftUnicodeEnglish(6, m_psName);
        // addMicrosoftUnicodeEnglish(7, m_tradeMark);
        // addMicrosoftUnicodeEnglish(8, m_manufacturer);
        // addMicrosoftUnicodeEnglish(19, m_sample);
    }

    /** Adds a name in Unicode, Mac and Windows. Adding only Unicode produces an error on Mac. */
    private void addName(int a_nameId, String a_value) {
        // FMS: Empty values generate an error on Mac
        if(a_value.isEmpty()) a_value="NA";
        try {
            add(TTName.k_unicode, 0, 0, a_nameId, a_value.getBytes(k_utf16be));
            add(TTName.k_macintosh, TTName.k_macRomanEncode, TTName.k_macEnglishLang, a_nameId,
                    a_value.getBytes(k_iso8859_1));
            add(TTName.k_microsoft, TTName.k_winUnicodeEncode, TTName.k_winEnglishLang, a_nameId,
                    a_value.getBytes(k_utf16be));
        } catch (IOException e) {
            e.printStackTrace();
        } // try-catch
    }

    @SuppressWarnings("unused")
	private void addMicrosoftUnicodeEnglish(int a_nameId, String a_value) {
        try {
            add(TTName.k_microsoft, TTName.k_winUnicodeEncode, TTName.k_winEnglishLang, a_nameId,
                    a_value.getBytes(k_utf16be));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void add(int a_platformId, int a_encodingId, int a_languageId, int a_nameId, byte a_bytes[]) {
        TTName name = new TTName(a_platformId, a_encodingId, a_languageId, a_nameId, a_bytes);
        m_names.add(name);
    }

    public void write() throws IOException {
        prepare();

        // table version number
        writeUInt16(0);

        // number of name records
        writeUInt16(m_names.size());

        // Offset to start of string storage (from start of table).
        writeUInt16(12 * m_names.size() + 6);

        int offset = 0;
        for (TTName name : m_names) {
            writeUInt16(name.getPlatformId());
            writeUInt16(name.getEncodingId());
            writeUInt16(name.getLanguageId());
            writeUInt16(name.getNameId());
            writeUInt16(name.getStringLength());
            writeUInt16(offset);
            offset += name.getStringLength();
        } // for i

        for (TTName name : m_names) {
            m_buffer.write(name.getBytes());
        } // for i

        pad();
    }

    protected String getTag() {
        return "name";
    }
}
