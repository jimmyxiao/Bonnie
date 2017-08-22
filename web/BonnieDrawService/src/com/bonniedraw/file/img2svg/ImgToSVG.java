package com.bonniedraw.file.img2svg;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import javax.imageio.ImageIO;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import com.bonniedraw.util.LogUtils;

public class ImgToSVG {
	
	private static void convert(BufferedImage img, OutputStream output) throws UnsupportedEncodingException, SVGGraphics2DIOException{
		Dimension m_size = new Dimension(img.getWidth() , img.getHeight());
		DOMImplementation domImplementation = GenericDOMImplementation.getDOMImplementation();
		String svgNS = "http://www.w3.org/2000/svg";
		Document svgDoc = domImplementation.createDocument(svgNS, "svg", null);
		SVGGraphics2D svg = new SVGGraphics2D(svgDoc);
		svg.setSVGCanvasSize(m_size);
		svg.drawImage(img, 0, 0, null);
		svg.dispose();
		Writer writer = new OutputStreamWriter(output, "UTF-8");
		svg.stream(writer,true);
	}
	
	public static boolean convertByFile(File file, OutputStream output) {
		boolean success = false;
		try {
			BufferedImage img = ImageIO.read(file);
			convert(img,output);
			success = true;
		} catch (IOException e) {
			LogUtils.error(ImgToSVG.class , "convertByFile has error : " +e);
		}
		return success;
	}
	
	public static boolean convertByCommonsMultipartFile(CommonsMultipartFile file, OutputStream output) {
		boolean success = false;
		try {
			BufferedImage img = ImageIO.read(file.getInputStream());
			convert(img,output);
			success = true;
		} catch (IOException e) {
			LogUtils.error(ImgToSVG.class , "convertByCommonsMultipartFile has error : " +e);
		}
		return success;
	}
	
}
