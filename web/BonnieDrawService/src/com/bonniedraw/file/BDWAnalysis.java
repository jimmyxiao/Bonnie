package com.bonniedraw.file;

import java.awt.Color;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.bonniedraw.util.LogUtils;

public class BDWAnalysis {
	
	public static final int BDWTAG_PAINT_INFO = 0xA101;
	
	public static List<Point> decrypt(File file, int startLoffset, int endLoffset) throws Exception {
		List<Point> pointList = new ArrayList<Point>();
		RandomAccessFile bdwFile = new RandomAccessFile(file, "r");
		byte[] buf = new byte[4];
		try {
			long loffset = startLoffset;
			bdwFile.seek(loffset);
			while ( (loffset<=endLoffset) && (bdwFile.read(buf, 0, 2)) > 0) {
				int length = FileDataFormat.buf2ToInt(buf, 0, true);
				if (length == 0)
					break;
				loffset = loffset + length;
				bdwFile.read(buf, 0, 2);
				int tag = FileDataFormat.buf2ToInt(buf, 0, true);

				switch (tag) {
				case BDWTAG_PAINT_INFO:
					Point point = readPointInfo(bdwFile, length, tag);
					if (point != null) {
						pointList.add(point);
					}
					break;
				}
				bdwFile.seek(loffset);
			}
		} catch (Exception e) {
			LogUtils.fileConteollerError("BDWAnalysis decrypt has error : " + e);
		} finally{
			bdwFile.close();
		}
		return pointList;
	}
	
	private static Point readPointInfo(RandomAccessFile bdwFile, int length, int tag){
		byte[] buf = new byte[4];
		Point point = new Point();
		point.setLength(length);
		point.setFc(tag);
		try{
			bdwFile.read(buf, 0, 2);
			point.setxPos(FileDataFormat.buf2ToInt(buf, 0, true));
	            
			bdwFile.read(buf, 0, 2);
			point.setyPos(FileDataFormat.buf2ToInt(buf, 0, true));

			bdwFile.read(buf, 0, 4);
			int p1 = buf[0] & 0xFF;
			int p2 = buf[1] & 0xFF;
			int p3 = buf[2] & 0xFF;
			int p4 = buf[3] & 0xFF;
			Color color = new Color(p3, p2, p1, p4);
			point.setColor(Integer.toHexString(color.getRGB()) );

			bdwFile.read(buf, 0, 1);
			point.setAction(FileDataFormat.byteToInt(buf[0]));

			bdwFile.read(buf, 0, 2);
			point.setSize(FileDataFormat.buf2ToInt(buf, 0, true));

			bdwFile.read(buf, 0, 1);
			point.setBrush(FileDataFormat.byteToInt(buf[0]));

			bdwFile.read(buf, 0, 2);
			point.setTime(FileDataFormat.buf2ToInt(buf, 0, true));

			bdwFile.read(buf, 0, 2);
			point.setReserve(FileDataFormat.buf2ToInt(buf, 0, true));
			
		}catch (Exception e){
			point = null;
		}
		return point;
	}
	
}
