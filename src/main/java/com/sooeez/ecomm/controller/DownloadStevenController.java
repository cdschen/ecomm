package com.sooeez.ecomm.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sooeez.ecomm.domain.PurchaseOrder;
import com.sooeez.ecomm.domain.Shipment;
import com.sooeez.ecomm.service.PurchaseOrderService;
import com.sooeez.ecomm.service.ShipmentService;

@Controller
@RequestMapping("/api")
public class DownloadStevenController {
	
	@Autowired 
	private ShipmentService shipmentService;
	
	@Autowired 
	private PurchaseOrderService purchaseOrderService;
	
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
	 * 发货单导出 Excel （单个/批量）
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/shipment/export")
    public ResponseEntity<byte[]> exportShipment
    (
    	@RequestParam( value = "id", required = false ) Long id,
    	@RequestParam( value = "ids", required = false ) Long[] ids
    )
    throws IOException
	{
    	boolean isExportSingleShipment = true;
    	boolean isExportable = false;

    	String excelName = null;
    	
    	if( id != null )
    	{
    		excelName = "发货单( " + id + " ) - " + (new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")).format(new Date()) + ".xls";
    		isExportable = true;
    	}
    	else if( ids != null && ids.length > 0 )
    	{
    		excelName = "发货单( 批量 ) - " + (new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")).format(new Date()) + ".xls";
    		isExportSingleShipment = false;
    		isExportable = true;
    	}
    	
    	Workbook workbook = new HSSFWorkbook();
    	
    	/* 创建 Sheet */
    	Sheet sheet = workbook.createSheet( WorkbookUtil.createSafeSheetName( "Sheet1" ) );
    	
    	/* 初始化［发货单标题］字体 */
    	Font titleFont = workbook.createFont();
    	titleFont.setBold( true );
    	titleFont.setFontName("宋体");
    	titleFont.setFontHeightInPoints( (short) 13 );
    	
    	/* 初始化［发货单内容］字体 */
    	Font contentFont = workbook.createFont();
    	contentFont.setFontName("宋体");
    	contentFont.setFontHeightInPoints( (short) 10 );

    	/* 初始化［发货单标题］样式 */
    	CellStyle titleStyle = workbook.createCellStyle();
    	titleStyle.setFont( titleFont );
    	titleStyle.setFillForegroundColor( HSSFColor.GREY_25_PERCENT.index );
    	titleStyle.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );
		
    	/* 初始化［发货单内容］样式 */
    	CellStyle contentStyle = workbook.createCellStyle();
    	contentStyle.setFont( contentFont );

    	/* 创建［发货单标题］ */
    	String[] titles = { "收货人", "电话号", "手机号", "地址", "物品内容", "邮编", "", "快递单号", "", "发货单号", "状态", "备注" };

    	Row titleRow = sheet.createRow( 0 );
    	for( int i = 0; i < titles.length; i++ )
    	{
        	Cell cell = titleRow.createCell( i );
        	cell.setCellStyle( titleStyle );
        	cell.setCellValue( titles[ i ] );
    	}

    	/* 导出单个发货单 */
    	if( isExportSingleShipment && isExportable )
    	{
        	Shipment shipment = shipmentService.getShipment( id );
        	
        	/* 创建［发货单内容］
        	 */
        	this.shipmentService.addShipmentToCell( shipment, sheet, contentStyle, 1 );
    	}
    	/* 导出多个发货单 */
    	else if( ! isExportSingleShipment && isExportable )
    	{
    		List<Shipment> shipments = this.shipmentService.getShipmentsByIds( ids );
    		
    		if( shipments != null && shipments.size() > 0 )
    		{
    			for( int i = 0; i < shipments.size(); i++ )
    			{
    	        	/* 创建［发货单内容］
    	        	 */
    	        	this.shipmentService.addShipmentToCell( shipments.get( i ), sheet, contentStyle, i + 1 );
    			}
    		}
    	}
    	
    	ByteArrayOutputStream output = new ByteArrayOutputStream();
    	workbook.write( output );
		return getResponseEntity( output, excelName );
    }

	
	/**
	 * 采购单导出 Excel （单个/批量）
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/purchaseOrder/export")
    public ResponseEntity<byte[]> exportPurchaseOrder
    (
    	@RequestParam( value = "id", required = false ) Long id,
    	@RequestParam( value = "ids", required = false ) Long[] ids
    )
    throws IOException
	{
    	boolean isExportSinglePurchaseOrder = true;
    	boolean isExportable = false;

    	String excelName = null;
    	
    	if( id != null )
    	{
    		excelName = "采购单( " + id + " ) - " + (new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")).format(new Date()) + ".xlsx";
    		isExportable = true;
    	}
//    	else if( ids != null && ids.length > 0 )
//    	{
//    		excelName = "采购单( 批量 ) - " + (new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")).format(new Date()) + ".xlsx";
//    		isExportSinglePurchaseOrder = false;
//    		isExportable = true;
//    	}
    	
    	Resource reourse = resourceLoader.getResource("classpath:purchaseOrderTemplate.xlsx");
    	Workbook workbook = new XSSFWorkbook( reourse.getInputStream() );
    	
    	/* 创建 Sheet */
    	Sheet sheet = workbook.getSheetAt( 0 );
    	
    	/* 导出单个采购单 */
    	if( isExportSinglePurchaseOrder && isExportable )
    	{
        	/* 获取［采购单内容］ */
        	PurchaseOrder purchaseOrder = this.purchaseOrderService.getPurchaseOrder( id );
        	this.purchaseOrderService.addPurchaseOrderToCell( purchaseOrder, workbook, sheet );
    	}
//    	/* 导出多个采购单 */
//    	else if( ! isExportSinglePurchaseOrder && isExportable )
//    	{
//    		List<Shipment> shipments = this.shipmentService.getShipmentsByIds( ids );
//    		
//    		if( shipments != null && shipments.size() > 0 )
//    		{
//    			for( int i = 0; i < shipments.size(); i++ )
//    			{
//    			}
//    		}
//    	}
    	
    	
    	ByteArrayOutputStream output = new ByteArrayOutputStream();
    	workbook.write( output );
		return getResponseEntity( output, excelName );
    }

}