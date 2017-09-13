package com.sctw.bonniedraw.utility;

import com.sctw.bonniedraw.paint.TagPoint;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


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
            FileDataFormat.IntToBuf2(tagPoint.getiPosX(), bData, 0, true);
            bufStream.write(bData,0,2);
            FileDataFormat.IntToBuf2(tagPoint.getiPosY(), bData, 0, true);
            bufStream.write(bData,0,2);
            bData = new byte[4];
            FileDataFormat.IntToBuf4(tagPoint.getiColor(), bData, 0, true);
            bufStream.write(bData,0,4);
            bData = new byte[1];
            FileDataFormat.IntToBuf1(tagPoint.getiAction(), bData, 0, true); 
            bufStream.write(bData,0,1);
            bData = new byte[2];
            FileDataFormat.IntToBuf2(tagPoint.getiSize(), bData, 0, true);
            bufStream.write(bData,0,2);
            bData = new byte[1];
            FileDataFormat.IntToBuf1(tagPoint.getiPaintType(), bData, 0, true);
            bufStream.write(bData,0,1);
            bData = new byte[2];
            FileDataFormat.IntToBuf2(tagPoint.getiReserved(), bData, 0, true);
            bufStream.write(bData,0,2);
            FileDataFormat.IntToBuf2(tagPoint.getiOther(), bData, 0, true);
            bufStream.write(bData,0,2);
        }
        catch(IOException ex)
        {
        	ex.printStackTrace();
        }

    }




}
