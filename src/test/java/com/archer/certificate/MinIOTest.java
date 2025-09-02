package com.archer.certificate;

import java.io.FileInputStream;
import java.io.File;

import com.archer.tools.clients.MinioUtil;

public class MinIOTest {

	public static void main(String[] args) {
		//String bucket, String object, InputStream fileStream
		try {
			FileInputStream fin = new FileInputStream(new File("d:/da.csv"));
			MinioUtil.uploadMinio("s3.laz01.qs.capcloud.com.cn", "GMKFIWVHTQXGHXVFRSCY", "0bPS8mpC8UaEsNGBAvBR6qJHPGxC6iutdGAeyV7c", "pcc-oss", "jsonTest.json", fin);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
