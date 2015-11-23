package com.sooeez.ecomm.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.SupplierProduct;
import com.sooeez.ecomm.dto.PageDTO;
import com.sooeez.ecomm.service.SupplierProductService;

@RestController
@RequestMapping("/api")
public class SupplierProductController {

	/*
	 * Service
	 */

	@Autowired
	private SupplierProductService supplierProductService;

	/*
	 * SupplierProduct
	 */

	@RequestMapping(value = "/supplierproducts/{id}")
	public SupplierProduct getSupplierProduct(@PathVariable("id") Long id) {
		return supplierProductService.getSupplierProduct(id);
	}

	@RequestMapping(value = "/supplierproducts")
	public PageDTO<SupplierProduct> getPagedSuppliers(SupplierProduct supplierProduct, Pageable pageable)
	{
//		long begin = System.currentTimeMillis();
		
		Page<SupplierProduct> page = supplierProductService.getPagedSupplierProducts( supplierProduct, pageable );
//		long afterPage = System.currentTimeMillis();
//		System.out.println( "afterPage - begin: " + ( afterPage - begin ) );
		PageDTO<SupplierProduct> pageDTO = new PageDTO<>();
		BeanUtils.copyProperties(page, pageDTO, "content", "sort");
//		long afterCopyPageProperties = System.currentTimeMillis();
//		System.out.println( "afterCopyProperties - begin: " + ( afterCopyPageProperties - begin ) );
		List<SupplierProduct> fsps = new ArrayList<>();
		page.getContent().forEach( osp ->
		{
//			SupplierProduct productDTO = new SupplierProduct();
//			productDTO.setId(p.getId());
//			productDTO.setSku( p.getSku() );
//			productDTO.setName(p.getName());
//			productDTO.setProcesses(p.getProcesses());
//			productDTO.setWarehouses(p.getWarehouses());
//			
//			productDTO.setPriceL1(p.getPriceL1());
//			productDTO.setPriceL2(p.getPriceL2());
//			productDTO.setPriceL3(p.getPriceL3());
//			productDTO.setPriceL4(p.getPriceL4());
//			productDTO.setPriceL5(p.getPriceL5());
//			productDTO.setPriceL6(p.getPriceL6());
//			productDTO.setPriceL7(p.getPriceL7());
//			productDTO.setPriceL8(p.getPriceL8());
//			productDTO.setPriceL9(p.getPriceL9());
//			productDTO.setPriceL10(p.getPriceL10());
//			productDTO.setWeight(p.getWeight());

			SupplierProduct fsp = new SupplierProduct();
			BeanUtils.copyProperties( osp, fsp, "product", "supplier", "creator" );
			if( osp.getProduct() != null )
			{
				Product op = osp.getProduct();
				Product fp = new Product();
				fp.setId( op.getId() );
				fp.setSku( op.getSku() );
				fp.setName( op.getName() );
				fp.setProcesses( op.getProcesses() );
				fp.setWarehouses( op.getWarehouses() );
				
				fp.setPriceL1( op.getPriceL1() );
				fp.setPriceL2( op.getPriceL2() );
				fp.setPriceL3( op.getPriceL3() );
				fp.setPriceL4( op.getPriceL4() );
				fp.setPriceL5( op.getPriceL5() );
				fp.setPriceL6( op.getPriceL6() );
				fp.setPriceL7( op.getPriceL7() );
				fp.setPriceL8( op.getPriceL8() );
				fp.setPriceL9( op.getPriceL9() );
				fp.setPriceL10( op.getPriceL10() );
				fp.setWeight( op.getWeight() );
				
				osp.setProduct( fp );
			}
			
			
			fsps.add( fsp );
		});
//		long afterCopyProductProperties = System.currentTimeMillis();
//		System.out.println( "afterCopyProductProperties - begin: " + ( afterCopyProductProperties - begin ) );
		pageDTO.setContent( fsps );
		return pageDTO;
	}

	@RequestMapping(value = "/supplierproducts/get/all")
	public List<SupplierProduct> getSupplierProducts(SupplierProduct supplierProduct, Sort sort)
	{
		List<SupplierProduct> osps = supplierProductService.getSupplierProducts( supplierProduct, sort );
		return osps;
	}

	@RequestMapping(value = "/supplierproducts", method = RequestMethod.POST)
	public SupplierProduct saveSupplier(@RequestBody SupplierProduct supplierProduct) {
		return supplierProductService.saveSupplierProduct( supplierProduct );
	}

	@RequestMapping(value = "/supplierproducts/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteSupplierProduct(@PathVariable("id") Long id) {
		supplierProductService.deleteSupplierProduct(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
