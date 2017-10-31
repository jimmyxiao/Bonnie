package com.bonniedraw.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.SercurityUtil;

public class FileUtil {
	
	public static Map<String, Object> copyURLToFile(String url, String path){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", false);
		result.put("path", null);
		String extension = url.substring(url.lastIndexOf("."));
		String newFileName = SercurityUtil.getUUID() + extension;
		String rootPath = System.getProperty("catalina.home");
		String childrenPath = "/files/" + path;
		File dir = new File(rootPath + childrenPath);
		if (!dir.exists())
			dir.mkdirs();
		try {
			URL domain = new URL(url);
			File uploadFile = new File(dir.getAbsolutePath() + "/" +newFileName);
			org.apache.commons.io.FileUtils.copyURLToFile(domain, uploadFile);
			result.put("status", true);
	    	result.put("path", (uploadFile.getAbsolutePath().replace(rootPath, "")).replace("\\", "/"));
		} catch (Exception e) {
			LogUtils.fileConteollerError("copyURLToFile has error : " + e );
		}
		return result;
	}
	
	public static Map<String, Object> uploadMultipartFile(MultipartFile file,String path){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", false);
		result.put("path", null);
		InputStream is = null;
		FileOutputStream fos = null;
		int length = 0;
		byte[] b = new byte[1024];
		String filename = file.getOriginalFilename();
		String extension = filename.substring(filename.lastIndexOf("."));
		String rootPath = System.getProperty("catalina.home");
		String childrenPath = "/files/" + path;
		File dir = new File(rootPath + childrenPath);
		
		if (!dir.exists())
			dir.mkdirs();
        try {
        	is = file.getInputStream();
        	String newFileName = SercurityUtil.getUUID() + extension;
            File uploadFile = new File(dir.getAbsolutePath() + "/" +newFileName);
            if(uploadFile.exists()){
            	uploadFile.delete();
            }

            fos = new FileOutputStream(uploadFile);
            length = 0;
            while ((length = is.read(b)) != -1) {
            	fos.write(b, 0, length);
            }
            fos.flush();
            fos.close();
            result.put("status", true);
    		result.put("path", (uploadFile.getAbsolutePath().replace(rootPath, "")).replace("\\", "/"));
        }catch (Exception e) {
        	LogUtils.fileConteollerError(filename + " uploadMultipartFile has error :" + e);
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
        return result;
	}
	
	public static boolean uploadFile(CommonsMultipartFile file,String path){
		InputStream is = null;
		FileOutputStream fos = null;
		int length = 0;
		byte[] b = new byte[1024];
		String filename = file.getOriginalFilename();
		String extension = filename.substring(filename.indexOf(".")+1);
		String rootPath = System.getProperty("catalina.home");
		File dir = new File(rootPath + "/files/" + path);
		
		if (!dir.exists())
			dir.mkdirs();
        try {
        	is = file.getInputStream();
//        	String newFileName = new StringBuffer(filename).insert(filename.indexOf("."), "_"+SercurityUtil.getUUID()).toString();
        	String newFileName = SercurityUtil.getUUID() + extension;
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
