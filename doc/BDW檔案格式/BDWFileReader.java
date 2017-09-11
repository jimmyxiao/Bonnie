package com.sctw.bonniedraw.utility;

/**
 * Created by Jimmy on 2017/9/6.
 */
import com.sctw.bonniedraw.paint.TagPoint;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class BDWFileReader {
    public static final int BDWTAG_PAINT_INFO = 0xA101;

    public ArrayList<TagPoint> m_tagArray;

    public boolean readFromFile(File in_bdwFile)
    {
        m_tagArray = new ArrayList<TagPoint>();
        int iNetworkType =0;
        long loffset = 0;
        byte[] buf = new byte[4];
        boolean bres = false;
        try
        {
            RandomAccessFile rf_bdwFile = new RandomAccessFile(in_bdwFile,"r");
            while(true)
            {
                int iread = rf_bdwFile.read(buf, 0, 2);
                if(!(iread >0))
                    break;
                int ilength = FileDataFormat.buf2ToInt(buf, 0, true);
                if(ilength==0)
                    break;
                loffset = loffset + ilength;
                rf_bdwFile.read(buf, 0, 2);
                int itag = FileDataFormat.buf2ToInt(buf, 0, true);

                switch(itag)
                {
                    case BDWTAG_PAINT_INFO:
                        TagPoint tagPoint = readPointInfo(rf_bdwFile);
                        if(tagPoint !=null)
                            m_tagArray.add(tagPoint);
                        break;


                    default:
                       // readUnknowTag(itag,rf_bdwFile);
                        break;
                }
                rf_bdwFile.seek(loffset);
            }
        }
        catch (Exception e)
        {
            bres = false;
        }
        return bres;
    }


    public TagPoint readPointInfo(RandomAccessFile bdwFile)
    {
        TagPoint tagPoint = new TagPoint();
        byte[] buf = new byte[4];
        try
        {
            int iPosX = 0;
            int iPosY = 0;
            int iTouchType = 0;
            int iColor =  0xFF000000;
            int iAction = 0;
            int iSize = 0;
            int iPaintType = 0;
            int iReserved = 0;
			int iOther = 0;

            bdwFile.read(buf, 0, 2);
            iPosX = FileDataFormat.buf2ToInt(buf, 0, true);
            tagPoint.setiPosX(iPosX);

            bdwFile.read(buf, 0, 2);
            iPosY = FileDataFormat.buf2ToInt(buf, 0, true);
            tagPoint.setiPosY(iPosY);

            bdwFile.read(buf, 0, 4);
            iColor= FileDataFormat.buf4ToInt(buf, 0, true);
            tagPoint.setiColor(iColor);

            bdwFile.read(buf, 0, 3);
            iAction = FileDataFormat.byteToInt(buf[0]);
            tagPoint.setiAction(iAction);

            iSize = FileDataFormat.byteToInt(buf[1]);
            iPaintType = FileDataFormat.byteToInt(buf[2]);
            tagPoint.setiPaintType(iPaintType);

            bdwFile.read(buf, 0, 2);
            iReserved =  FileDataFormat.buf2ToInt(buf, 0, true);
            tagPoint.setiReserved(iReserved);
			
			bdwFile.read(buf, 0, 1);
	        iOther =  FileDataFormat.byteToInt(buf[0]);
	        tagPoint.setiOther(iOther);

        }
        catch (Exception e)
        {
            tagPoint = null;
        }
        return tagPoint;
    }

}
