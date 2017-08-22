package com.bonniedraw.file.controller;

import java.io.File;
import java.io.FileNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.bonniedraw.base.model.BaseModel;
import com.bonniedraw.file.img2svg.ImgToSVG;
import com.bonniedraw.file.svg2ttf.Svg2Ttf;

@Controller
public class FileController {
	
//	=========	test area	start		=========
	/*
	 *  Test image to svg
	 */
	@RequestMapping(value = "/test",produces="application/json")
	public @ResponseBody BaseModel test(HttpServletRequest request,HttpServletResponse resp){
		resp.setHeader("Access-Control-Allow-Origin", "*");
		boolean success = false;
		
		File file1=new File("C:\\Users\\user\\Desktop\\永.jpg");
		File f = new File("C:\\Users\\user\\Desktop\\永.svg");
		try {
			success = ImgToSVG.convertByFile(file1,  new java.io.FileOutputStream(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(success);
		return baseModel;
	}
	
	/*
	 *	Test svg to ttf 
	 */
	@RequestMapping(value = "/test2",produces="application/json")
	public @ResponseBody BaseModel test2(HttpServletRequest request,HttpServletResponse resp){
		resp.setHeader("Access-Control-Allow-Origin", "*");
		File file1=new File("C:\\Users\\user\\Desktop\\永.svg");
		try {
			Svg2Ttf.convert(file1, "自創字型", "http://pppppp");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(true);
		return baseModel;
	}
//	=========	test area	end	=========
	
	private BaseModel uploadFile(CommonsMultipartFile file,String path){
		BaseModel baseModel = new BaseModel();
//		InputStream is = null;
//		FileOutputStream fos = null;
//		int length = 0;
//		byte[] b = new byte[1024];
//		String filename = file.getOriginalFilename();
//		String rootPath = System.getProperty("catalina.home");
//		File dir = new File(rootPath + ResourceConfig.FILE_SEPARATOR +"images"+ResourceConfig.FILE_SEPARATOR+ path);
//		
//		if (!dir.exists())
//			dir.mkdirs();
//        try {
//        	is = file.getInputStream();
//        	String newFileName = new StringBuffer(filename).insert(filename.indexOf("."), "_"+SercurityUtil.getUUID()).toString();
//            File uploadFile = new File(dir.getAbsolutePath() + ResourceConfig.FILE_SEPARATOR +newFileName);
//            fos = new FileOutputStream(uploadFile);
//            length = 0;
//            while ((length = is.read(b)) != -1) {
//            	fos.write(b, 0, length);
//            }
//            fos.flush();
//            fos.close();
//            baseModel.setResult(true);
//            baseModel.setData(path + ResourceConfig.FILE_SEPARATOR +newFileName);
//        }catch (Exception e) {
//        	LogUtils.fileConteollerError(filename + " uploadFile has error :" + e);
//        	baseModel.setResult(false);
//            baseModel.setMessage(filename+"上傳失敗");
//        }finally{
//        	try {
//				if (is != null)
//					is.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			try {
//				if (fos != null)
//					fos.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//        }
        return baseModel;
	}
	
	@RequestMapping(value = "/{parent}/upload",produces="application/json",method = RequestMethod.POST)
	public @ResponseBody BaseModel uploadSingle(@PathVariable String parent,@RequestParam("files") CommonsMultipartFile file,HttpServletResponse resp) {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		String path = null;
        return uploadFile(file, path);
    }
	
//	private BaseModel uploadMultipleFile(List<CommonsMultipartFile> files,String path){
//		BaseModel baseModel = new BaseModel();
//		boolean success = true;
//		List<String> filePathList = new ArrayList<String>();
//		InputStream is = null;
//		FileOutputStream fos = null;
//		int length = 0;
//		byte[] b = new byte[1024];
//		String rootPath = System.getProperty("catalina.home");
//		File dir = new File(rootPath + ResourceConfig.FILE_SEPARATOR +"images"+ResourceConfig.FILE_SEPARATOR+ path);
//		
//		if (!dir.exists())
//			dir.mkdirs();
//		for(CommonsMultipartFile file:files){
//			String filename = file.getOriginalFilename();
//			try {
//	        	is = file.getInputStream();
//	        	String newFileName = new StringBuffer(filename).insert(filename.indexOf("."), "_"+SercurityUtil.getUUID()).toString();
//	            File uploadFile = new File(dir.getAbsolutePath() + ResourceConfig.FILE_SEPARATOR +newFileName);
//	            fos = new FileOutputStream(uploadFile);
//	            length = 0;
//	            while ((length = is.read(b)) != -1) {
//	            	fos.write(b, 0, length);
//	            }
//	            fos.flush();
//	            fos.close();
//	            filePathList.add(path + ResourceConfig.FILE_SEPARATOR +newFileName);
//	        }catch (Exception e) {
//	        	success = false;
//	        	LogUtils.fileConteollerError(filename + " uploadFile has error :" + e);
//	        }finally{
//	        	try {
//					if (is != null)
//						is.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				try {
//					if (fos != null)
//						fos.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//	        }
//		}
//		baseModel.setResult(success);
//        baseModel.setData(filePathList);
//        if(!success){
//        	 baseModel.setMessage("上傳失敗");
//        }   
//        return baseModel;
//	}
	
//	@RequestMapping(value = "/loadFile/{parent}/{type}/{fileName}")
//	public @ResponseBody HttpEntity<byte[]> loadFileSingle(HttpServletResponse resp,@PathVariable String parent,@PathVariable String type,@PathVariable String fileName) {		
//		byte[] image = null;
//		HttpHeaders headers = new HttpHeaders();
//		String rootPath = System.getProperty("catalina.home");
//		String filePath = rootPath + ResourceConfig.FILE_SEPARATOR 
//				+"images" +ResourceConfig.FILE_SEPARATOR
//				+ parent + ResourceConfig.FILE_SEPARATOR
//				+ type + ResourceConfig.FILE_SEPARATOR 
//				+fileName ;
//		File dir = new File(filePath);
//		
//		try{
//			image = org.apache.commons.io.FileUtils.readFileToByteArray(dir);
//			headers.setContentType(MediaType.IMAGE_PNG); 
//			headers.setContentLength(image.length);
//		}catch(IOException e){
//			LogUtils.fileConteollerError(filePath + " loadFileSingle has error : 輸出發生異常 =>" +e);
//		}
//		return new HttpEntity<byte[]>(image, headers);
//    }
	
}
