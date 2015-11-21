package com.sooeez.ecomm.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
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

import com.sooeez.ecomm.domain.InventoryBatch;
import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.WarehousePosition;
import com.sooeez.ecomm.service.InventoryBatchService;

@Controller
@RequestMapping("/api")
public class DownloadController {
	
	@Autowired
    private ResourceLoader resourceLoader;
	
	/*
	 * Service
	 */
	
	@Autowired
	private InventoryBatchService batchService;
	
	/*
	 * Functions
	 */
	
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
	
	/*
	 * 创建行和样式
	 */
	
	private HSSFRow createRowAndStyle(int startRow, HSSFSheet sheet, HSSFCellStyle style) {
		HSSFRow row = sheet.createRow(startRow);
		for (int i = 0; i < 5; i++) {
			HSSFCell dataCell = row.createCell(i);
			dataCell.setCellStyle(style);
		}
		return row;
	}
	
	@RequestMapping(value = "/inventory-batches/download/{id}")
    public ResponseEntity<byte[]> downloadBatch(@PathVariable("id") Long id) throws IOException {
    	
    	InventoryBatch batch = batchService.getBatch(id);
    	
    	List<Product> products = batchService.refreshBatchItems(batch);
    	
    	// 读取excel模版，常见工作簿
    	Resource reourse = resourceLoader.getResource("classpath:inventoryOutTemplate.xls");
    	HSSFWorkbook wb = new HSSFWorkbook(reourse.getInputStream());
    	
    	// 初始化字体
    	HSSFFont font = wb.createFont();
		font.setBold(true);
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 12);
    	
		// 初始化样式
		HSSFCellStyle style = wb.createCellStyle();
		style.setFont(font);
		style.setAlignment(CellStyle.ALIGN_JUSTIFY);

    	// 第一个工作表
    	HSSFSheet sheet = wb.getSheetAt(0);
    	
    	// 出库单号
    	HSSFCell cell = sheet.getRow(0).createCell(1);
    	cell.setCellStyle(style);
    	cell.setCellValue(batch.getId().longValue());
    	
    	// 仓库
    	cell = sheet.getRow(1).createCell(1);
    	cell.setCellStyle(style);
    	cell.setCellValue(batch.getWarehouse().getName());
    	
    	// 应出库商品总件数
    	cell = sheet.getRow(3).createCell(1);
    	cell.setCellStyle(style);
    	cell.setCellValue(Math.abs(batch.getTotal().longValue())); 
    	
    	int startRow = 5;
    	for(Product product: products) {
    		
    		HSSFRow row = createRowAndStyle(startRow, sheet, style);
    	
    		// 设置sku
    		row.getCell(0).setCellValue(product.getSku());
    		// 设置商品名称
    		row.getCell(1).setCellValue(product.getName());
    		
    		for (int i = 0, len = product.getPositions().size(); i < len; i++) {
    			WarehousePosition position = product.getPositions().get(i);
    			if (i == 0) {
    				// 设置库位
    				row.getCell(2).setCellValue(position.getName());
    				// 设置应出库数量
    				row.getCell(3).setCellValue(Math.abs(position.getTotal().longValue()));
    				// 出库后应剩余库存
    				row.getCell(4).setCellValue(position.getStock().longValue());
    			} else {
    				startRow++;
    				row = createRowAndStyle(startRow, sheet, style);
    				// 设置库位
    				row.getCell(2).setCellValue(position.getName());
    				// 设置应出库数量
    				row.getCell(3).setCellValue(Math.abs(position.getTotal().longValue()));
    				// 出库后应剩余库存
    				row.getCell(4).setCellValue(position.getStock().longValue());
    			}
    		}
    		startRow++;
    	}
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	wb.write(baos);
		return getResponseEntity(baos, "出库单(" + id + ")-" + (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date()) + ".xls");
    }

}
