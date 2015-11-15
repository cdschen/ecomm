package com.sooeez.ecomm.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.catalina.util.URLEncoder;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class DownloadController {
	
	@Autowired
    private ResourceLoader resourceLoader;
	
	private ResponseEntity<byte[]> getResponseEntity(ByteArrayOutputStream baos, String fileName) {
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    	try {
			headers.setContentDispositionFormData("attachment", new String(fileName.getBytes("UTF-8"), "ISO8859-1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return new ResponseEntity<byte[]>(baos.toByteArray(), headers, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/inventory-batches/download/{id}")
    public ResponseEntity<byte[]> downloadBatch(@PathVariable("id") Integer id) throws IOException {
    	Resource reourse = resourceLoader.getResource("classpath:inventoryOutTemplate.xls");
    	HSSFWorkbook wb = new HSSFWorkbook(reourse.getInputStream());
    	
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	wb.write(baos);
		return getResponseEntity(baos, "出库单(" + id + ")-" + (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date()) + ".xls");
    }

}
