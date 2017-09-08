package com.bonniedraw.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.bonniedraw.util.LogUtils;

public class FileUtil {
	
	public static boolean uploadFile(CommonsMultipartFile file,String path){
		InputStream is = null;
		FileOutputStream fos = null;
		int length = 0;
		byte[] b = new byte[1024];
		String filename = file.getOriginalFilename();
		String rootPath = System.getProperty("catalina.home");
		File dir = new File(rootPath + "/files/" + path);
		
		if (!dir.exists())
			dir.mkdirs();
        try {
        	is = file.getInputStream();
//        	String newFileName = new StringBuffer(filename).insert(filename.indexOf("."), "_"+SercurityUtil.getUUID()).toString();
        	String newFileName =filename;
            File uploadFile = new File(dir.getAbsolutePath() + "/" +newFileName);
            fos = new FileOutputStream(uploadFile);
            length = 0;
            while ((length = is.read(b)) != -1) {
            	fos.write(b, 0, length);
            }
            fos.flush();
            fos.close();
            return true;
        }catch (Exception e) {
        	LogUtils.fileConteollerError(filename + " uploadFile has error :" + e);
        }finally{
        	try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return false;
	}
	
}
