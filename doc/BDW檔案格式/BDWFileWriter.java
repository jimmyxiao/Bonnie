package com.sctw.bonniedraw.utility;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.sctw.bonniedraw.utility.TagPoint;

/**
 * Created by Jimmy on 2017/9/7.
 */

public class BDWFileWriter {

    public boolean WriteToFile(List<TagPoint> tagPointsList , String strFileName)
    {
        boolean bres = false;
        try
        {
            int i , icount = tagPointsList.size();
            OutputStream output = null;
            output = new BufferedOutputStream(new FileOutputStream(strFileName));

            for (i=0;i<icount;i++)
            {
                TagPoint tagPoint = tagPointsList.get(i);
                writeTagPointToStream( tagPoint,output);
            }
            output.flush();
            output.close();
        }
        catch(IOException ex)
        {
        	ex.printStackTrace();
        }
        return bres;

    }


    public void write_TagHeader(int ilenth,int tagCode,OutputStream bufStream)
    {
        byte[] bData = new byte[2];
        try
        {
            FileDataFormat.IntToBuf2(ilenth,bData,0,true);
            bufStream.write(bData,0,2);
            FileDataFormat.IntToBuf2(tagCode,bData,0,true);
            bufStream.write(bData,0,2);
        }
        catch(IOException ex)
        {

        }
    }

    public void writeTagPointToStream(TagPoint tagPoint,OutputStream bufStream)
    {
        try
        {
            write_TagHeader(20, tagPoint.getTagCode() , bufStream);
            byte[] bData = new byte[2];
            FileDataFormat.IntToBuf2(tagPoint.get_iPosX(), bData, 0, true);
            bufStream.write(bData,0,2);
            FileDataFormat.IntToBuf2(tagPoint.get_iPosY(), bData, 0, true);
            bufStream.write(bData,0,2);
            bData = new byte[4];
            FileDataFormat.IntToBuf4(tagPoint.get_iColor(), bData, 0, true);
            bufStream.write(bData,0,4);
            bData = new byte[1];
            FileDataFormat.IntToBuf1(tagPoint.get_iAction(), bData, 0, true);
            bufStream.write(bData,0,1);
            bData = new byte[2];
            FileDataFormat.IntToBuf2(tagPoint.get_iSize(), bData, 0, true);
            bufStream.write(bData,0,2);
            bData = new byte[1];
            FileDataFormat.IntToBuf1(tagPoint.get_iBrush(), bData, 0, true);
            bufStream.write(bData,0,1);
            bData = new byte[2];
            FileDataFormat.IntToBuf2(tagPoint.get_iTime(), bData, 0, true);
            bufStream.write(bData,0,2);
            FileDataFormat.IntToBuf2(tagPoint.get_iReserved(), bData, 0, true);
            bufStream.write(bData,0,2);
        }
        catch(IOException ex)
        {
        	ex.printStackTrace();
        }

    }




}
