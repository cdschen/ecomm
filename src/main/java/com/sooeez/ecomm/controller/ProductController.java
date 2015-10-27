package com.sooeez.ecomm.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.ProductMember;
import com.sooeez.ecomm.domain.ProductMultiCurrency;
import com.sooeez.ecomm.domain.ProductMultiLanguage;
import com.sooeez.ecomm.domain.ProductShopTunnel;
import com.sooeez.ecomm.service.ProductService;

@RestController
@RequestMapping("/api")
public class ProductController {

	@Autowired private Environment env;
	@Autowired private ProductService productService;
	
	/*
	 * Product
	 */
	
	@RequestMapping(value = "/products/{id}")
	public Product getProduct(@PathVariable("id") Long id) {
		return this.productService.getProduct(id);
	}
	
	@RequestMapping(value = "/products")
	public Page<Product> getPagedProducts(Product product, Pageable pageable) {
		return this.productService.getPagedProducts(product, pageable);
	}
	
	@RequestMapping(value = "/products/get/all")
	public List<Product> getProducts(Product product, Sort sort) {
		return this.productService.getProducts(product, sort);
	}
	
	@RequestMapping(value = "/products", method = RequestMethod.POST)
	public Product saveProduct(@RequestBody Product product, @RequestParam String action, HttpServletRequest request) {
		if ("create".equals(action)) {
			String directory = env.getProperty("ecomm.resource.directory");
			System.out.println("directory:" + directory);
			//this.productService.saveProduct(product);
			if (product.getThumbnailUrl() != null) {
				File file = new File(directory + product.getThumbnailUrl());
				product.setThumbnailUrl(product.getId() + "-" + product.getThumbnailUrl());
				boolean b = file.renameTo(new File(directory + product.getThumbnailUrl()));
				System.out.println("缩略图更新: " + b);
			}
			if (product.getImage1Url() != null) {
				File file = new File(directory + product.getImage1Url());
				product.setImage1Url(product.getId() + "-" + product.getImage1Url());
				boolean b = file.renameTo(new File(directory + product.getImage1Url()));
				System.out.println("图片1更新: " + b);
			}
			if (product.getImage2Url() != null) {
				File file = new File(directory + product.getImage2Url());
				product.setImage2Url(product.getId() + "-" + product.getImage2Url());
				boolean b = file.renameTo(new File(directory + product.getImage2Url()));
				System.out.println("图片2更新: " + b);
			}
			if (product.getImage3Url() != null) {
				File file = new File(directory + product.getImage3Url());
				product.setImage3Url(product.getId() + "-" + product.getImage3Url());
				boolean b = file.renameTo(new File(directory + product.getImage3Url()));
				System.out.println("图片3更新: " + b);
			}
			if (product.getImage4Url() != null) {
				File file = new File(directory + product.getImage4Url());
				product.setImage4Url(product.getId() + "-" + product.getImage4Url());
				boolean b = file.renameTo(new File(directory + product.getImage4Url()));
				System.out.println("图片4更新: " + b);
			}
			if (product.getImage5Url() != null) {
				File file = new File(directory + product.getImage5Url());
				product.setImage5Url(product.getId() + "-" + product.getImage5Url());
				boolean b = file.renameTo(new File(directory + product.getImage5Url()));
				System.out.println("图片5更新: " + b);
			}
			if (product.getImage6Url() != null) {
				File file = new File(directory + product.getImage6Url());
				product.setImage6Url(product.getId() + "-" + product.getImage6Url());
				boolean b = file.renameTo(new File(directory + product.getImage6Url()));
				System.out.println("图片6更新: " + b);
			}
			if (product.getImage7Url() != null) {
				File file = new File(directory + product.getImage7Url());
				product.setImage7Url(product.getId() + "-" + product.getImage7Url());
				boolean b = file.renameTo(new File(directory + product.getImage7Url()));
				System.out.println("图片7更新: " + b);
			}
			if (product.getImage8Url() != null) {
				File file = new File(directory + product.getImage8Url());
				product.setImage8Url(product.getId() + "-" + product.getImage8Url());
				boolean b = file.renameTo(new File(directory + product.getImage8Url()));
				System.out.println("图片8更新: " + b);
			}
			if (product.getImage9Url() != null) {
				File file = new File(directory + product.getImage9Url());
				product.setImage9Url(product.getId() + "-" + product.getImage9Url());
				boolean b = file.renameTo(new File(directory + product.getImage9Url()));
				System.out.println("图片9更新: " + b);
			}
			return this.productService.saveProduct(product);		
		} else if ("update".equals(action)) {
			return this.productService.saveProduct(product);
		}
		return null;
	}
	
	@RequestMapping(value = "/products/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {
		this.productService.deleteProduct(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/products/image/upload/{id}/{field}", method = RequestMethod.POST)
	public ResponseEntity<?> imageUpload(@PathVariable("id") Long id, @PathVariable("field") String field,  
			@RequestParam("image") MultipartFile uploadfile,
			HttpServletRequest request) {

		try {
			System.out.println("id: " + id);
			System.out.println("field: " + field);
			String directory = env.getProperty("ecomm.resource.directory");
			System.out.println("directory:" + directory);
			
			String filename = uploadfile.getOriginalFilename();
			if (id.intValue() != 0) {
				
				Product product = this.productService.getProduct(id);
				String dest = id + "-" + filename;
				if ("thumbnailUrl".equals(field)) {
					product.setThumbnailUrl(dest);
					System.out.println("缩略图已上传并更新表");
				}
				if ("image1Url".equals(field)) {
					product.setImage1Url(dest);
					System.out.println("图片1已上传并更新表");
				}
				if ("image2Url".equals(field)) {
					product.setImage2Url(dest);
					System.out.println("图片2已上传并更新表");
				}
				if ("image3Url".equals(field)) {
					product.setImage3Url(dest);
					System.out.println("图片3已上传并更新表");
				}
				if ("image4Url".equals(field)) {
					product.setImage4Url(dest);
					System.out.println("图片4已上传并更新表");
				}
				if ("image5Url".equals(field)) {
					product.setImage5Url(dest);
					System.out.println("图片5已上传并更新表");
				}
				if ("image6Url".equals(field)) {
					product.setImage6Url(dest);
					System.out.println("图片6已上传并更新表");
				}
				if ("image7Url".equals(field)) {
					product.setImage7Url(dest);
					System.out.println("图片7已上传并更新表");
				}
				if ("image8Url".equals(field)) {
					product.setImage8Url(dest);
					System.out.println("图片8已上传并更新表");
				}
				if ("image9Url".equals(field)) {
					product.setImage9Url(dest);
					System.out.println("图片9已上传并更新表");
				}
				
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(directory + dest)));
				stream.write(uploadfile.getBytes());
				stream.close();
				this.productService.saveProduct(product);
				
			} else {
				
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(directory + filename)));
				stream.write(uploadfile.getBytes());
				stream.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	} 
	
	/*
	 * ProductMultiLanguage
	 */
	
	@RequestMapping(value = "/productmultilanguages/{id}")
	public ProductMultiLanguage getProductMultiLanguage(@PathVariable("id") Long id) {
		return this.productService.getProductMultiLanguage(id);
	}
	
	@RequestMapping(value = "/productmultilanguages")
	public List<ProductMultiLanguage> getProductMultiLanguages() {
		return this.productService.getProductMultiLanguages();
	}
	
	@RequestMapping(value = "/productmultilanguages", method = RequestMethod.POST)
	public ProductMultiLanguage saveProductMultiLanguage(@RequestBody ProductMultiLanguage productMultiLanguage) {
		return this.productService.saveProductMultiLanguage(productMultiLanguage);
	}
	
	@RequestMapping(value = "/productmultilanguages/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProductMultiLanguage(@PathVariable("id") Long id) {
		this.productService.deleteProductMultiLanguage(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * ProductMultiCurrency
	 */
	
	@RequestMapping(value = "/productmulticurrencies/{id}")
	public ProductMultiCurrency getProductMultiCurrency(@PathVariable("id") Long id) {
		return this.productService.getProductMultiCurrency(id);
	}
	
	@RequestMapping(value = "/productmulticurrencies")
	public List<ProductMultiCurrency> getProductMultiCurrencies() {
		return this.productService.getProductMultiCurrencies();
	}
	
	@RequestMapping(value = "/productmulticurrencies", method = RequestMethod.POST)
	public ProductMultiCurrency saveProductMultiCurrency(@RequestBody ProductMultiCurrency productMultiCurrency) {
		return this.productService.saveProductMultiCurrency(productMultiCurrency);
	}
	
	@RequestMapping(value = "/productmulticurrencies/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProductMultiCurrency(@PathVariable("id") Long id) {
		this.productService.deleteProductMultiCurrency(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * ProductMember
	 */
	
	@RequestMapping(value = "/productmembers/{id}")
	public ProductMember getProductMember(@PathVariable("id") Long id) {
		return this.productService.getProductMember(id);
	}
	
	@RequestMapping(value = "/productmembers")
	public List<ProductMember> getProductMembers() {
		return this.productService.getProductMembers();
	}
	
	@RequestMapping(value = "/productmembers", method = RequestMethod.POST)
	public ProductMember saveProductMember(@RequestBody ProductMember productMember) {
		return this.productService.saveProductMember(productMember);
	}
	
	@RequestMapping(value = "/productmembers/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProductMember(@PathVariable("id") Long id) {
		this.productService.deleteProductMember(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * ProductShopTunnel
	 */
	
	@RequestMapping(value = "/productshoptunnels/{id}")
	public ProductShopTunnel getProductShopTunnel(@PathVariable("id") Long id) {
		return this.productService.getProductShopTunnel(id);
	}
	
	@RequestMapping(value = "/productshoptunnels")
	public List<ProductShopTunnel> getProductShopTunnel() {
		return this.productService.getProductShopTunnels();
	}
	
	@RequestMapping(value = "/productshoptunnels", method = RequestMethod.POST)
	public ProductShopTunnel saveProductProductShopTunnel(@RequestBody ProductShopTunnel productShopTunnel) {
		return this.productService.saveProductShopTunnel(productShopTunnel);
	}
	
	@RequestMapping(value = "/productshoptunnels/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProductShopTunnel(@PathVariable("id") Long id) {
		this.productService.deleteProductShopTunnel(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
