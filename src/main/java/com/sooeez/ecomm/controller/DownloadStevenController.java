package com.sooeez.ecomm.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class DownloadStevenController {
	
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
	
	
	/**
	 * 发货单导出 Excel
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/shipment/export/{id}")
    public ResponseEntity<byte[]> exportShipment(@PathVariable("id") Integer id) throws IOException
	{
    	Workbook workbook = new HSSFWorkbook();
    	
    	String excelName = "发货单( " + id + " )-" + (new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")).format(new Date()) + ".xls";
    	
    	/* 创建 Sheet */
    	Sheet sheet = workbook.createSheet( WorkbookUtil.createSafeSheetName( excelName ) );
    	
    	CellStyle titleStyle = workbook.createCellStyle();
//    	titleStyle.setFillBackgroundColor( IndexedColors.G );
    	
    	/* 创建［发货单首行标题］ */
    	Row titleRow = sheet.createRow( 0 );
//    	titleRow.setRowStyle(arg0);
    	sheet.createRow( 0 ).createCell( 0 ).setCellValue( "收货人" );
    	sheet.createRow( 0 ).createCell( 1 ).setCellValue( "电话号" );
    	sheet.createRow( 0 ).createCell( 2 ).setCellValue( "手机号" );
    	sheet.createRow( 0 ).createCell( 3 ).setCellValue( "地址" );
    	sheet.createRow( 0 ).createCell( 4 ).setCellValue( "物品内容" );
    	sheet.createRow( 0 ).createCell( 5 ).setCellValue( "邮编" );
    	sheet.createRow( 0 ).createCell( 7 ).setCellValue( "快递单号" );
    	
    	/* 创建［发货单内容］ */
    	
    	
    	
    	
    	ByteArrayOutputStream output = new ByteArrayOutputStream();
    	workbook.write( output );
		return getResponseEntity( output, excelName );
    }
	
	@RequestMapping(value = "/purchase-order/export/{id}")
    public ResponseEntity<byte[]> exportPurchaseOrder(@PathVariable("id") Integer id) throws IOException
	{
    	Resource reourse = resourceLoader.getResource("classpath:inventoryOutTemplate.xls");
    	Workbook workbook = new HSSFWorkbook( reourse.getInputStream() );
    	
    	String excelName = "出库单(" + id + ")-" + (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date()) + ".xls";
    	
    	/* 创建 Sheet */
    	Sheet sheet = workbook.createSheet( WorkbookUtil.createSafeSheetName( excelName ) );
    	
    	/* 创建 Row */
    	Row row = sheet.createRow( 1 );
    	Cell cell = row.createCell( 1 );
    	
    	cell.setCellValue( "Hi there" );
    	
    	
    	ByteArrayOutputStream output = new ByteArrayOutputStream();
    	workbook.write( output );
		return getResponseEntity( output, excelName );
    }

}
