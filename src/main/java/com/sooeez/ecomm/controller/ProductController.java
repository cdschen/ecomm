package com.sooeez.ecomm.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.sooeez.ecomm.dto.PageDTO;
import com.sooeez.ecomm.service.ProductMemberService;
import com.sooeez.ecomm.service.ProductMultiCurrencyService;
import com.sooeez.ecomm.service.ProductMultiLanguageService;
import com.sooeez.ecomm.service.ProductService;
import com.sooeez.ecomm.service.ProductShopTunnelService;

@RestController
@RequestMapping("/api")
public class ProductController {
	
	@Autowired 
	private Environment env;
	
	@Autowired 
	private PasswordEncoder passwordEncoder;
	
	/*
	 * Service
	 */

	@Autowired 
	private ProductService productService;
	
	@Autowired 
	private ProductMemberService memberService;
	
	@Autowired 
	private ProductMultiLanguageService multiLanguageService;
	
	@Autowired 
	private ProductMultiCurrencyService multiCurrencyService;
	
	@Autowired 
	private ProductShopTunnelService shopTunnelService;
	
	/*
	 * Product
	 */
	
	@RequestMapping(value = "/products/check-unique")
	public Boolean existsProduct(Product product) {
		return productService.existsProduct(product);
	}
	
	@RequestMapping(value = "/products/{id}")
	public Product getProduct(@PathVariable("id") Long id) {
		return productService.getProduct(id);
	}
	
	@RequestMapping(value = "/products")
	public PageDTO<Product> getPagedProducts(Product product, Pageable pageable) {
		Page<Product> page = productService.getPagedProducts(product, pageable);
		PageDTO<Product> pageDTO = new PageDTO<>();
		BeanUtils.copyProperties(page, pageDTO, "content", "sort");
		List<Product> pList = new ArrayList<>();
		page.getContent().forEach(p -> {
			Product productDTO = new Product();
			productDTO.setId(p.getId());
			productDTO.setSku(p.getSku());
			productDTO.setName(p.getName());
			productDTO.setProcesses(p.getProcesses());
			productDTO.setWarehouses(p.getWarehouses());
			
			productDTO.setPriceL1(p.getPriceL1());
			productDTO.setPriceL2(p.getPriceL2());
			productDTO.setPriceL3(p.getPriceL3());
			productDTO.setPriceL4(p.getPriceL4());
			productDTO.setPriceL5(p.getPriceL5());
			productDTO.setPriceL6(p.getPriceL6());
			productDTO.setPriceL7(p.getPriceL7());
			productDTO.setPriceL8(p.getPriceL8());
			productDTO.setPriceL9(p.getPriceL9());
			productDTO.setPriceL10(p.getPriceL10());
			productDTO.setWeight(p.getWeight());
			
			pList.add(productDTO);
		});
		pageDTO.setContent(pList);
		return pageDTO;
	}
	
	@RequestMapping(value = "/products/get/all")
	public List<Product> getProducts(Product product, Sort sort) {
		return productService.getProducts(product, sort);
	}
	
	@RequestMapping(value = "/products", method = RequestMethod.POST)
	public Product saveProduct(@RequestBody Product product) {
		return productService.saveProduct(product);
	}
	
	@RequestMapping(value = "/products/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {
		productService.deleteProduct(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/products/image/upload/{id}/{field}", method = RequestMethod.POST)
	public ResponseEntity<String> imageUpload(@PathVariable("id") Long id, @PathVariable("field") String field,  
			@RequestParam("image") MultipartFile uploadfile,
			HttpServletRequest request) {
		
		String filename = uploadfile.getOriginalFilename();
		String time = String.valueOf(System.currentTimeMillis());
		String encode = passwordEncoder.encode(filename + time);
		encode = encode.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]", "").trim();
		String dest = encode.substring(0, 20) + "" + filename.substring(filename.lastIndexOf("."));

		try {
			System.out.println("id: " + id);
			System.out.println("field: " + field);
			String directory = env.getProperty("ecomm.resource.directory");
			System.out.println("directory:" + directory);
			
			if (id.intValue() != 0) {
				
				Product product = productService.getProduct(id);
				
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
				productService.saveProduct(product);
				
			} else {
				
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(directory + dest)));
				stream.write(uploadfile.getBytes());
				stream.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<String>(dest, HttpStatus.OK);
	} 
	
	/*
	 * ProductMultiLanguage
	 */
	
	@RequestMapping(value = "/product-multilanguages/{id}")
	public ProductMultiLanguage getMultiLanguage(@PathVariable("id") Long id) {
		return multiLanguageService.getMultiLanguage(id);
	}
	
	@RequestMapping(value = "/product-multilanguages")
	public List<ProductMultiLanguage> getMultiLanguages() {
		return multiLanguageService.getMultiLanguages();
	}
	
	@RequestMapping(value = "/product-multilanguages", method = RequestMethod.POST)
	public ProductMultiLanguage saveMultiLanguage(@RequestBody ProductMultiLanguage multiLanguage) {
		return multiLanguageService.saveMultiLanguage(multiLanguage);
	}
	
	@RequestMapping(value = "/product-multilanguages/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMultiLanguage(@PathVariable("id") Long id) {
		multiLanguageService.deleteMultiLanguage(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * ProductMultiCurrency
	 */
	
	@RequestMapping(value = "/product-multicurrencies/{id}")
	public ProductMultiCurrency getMultiCurrency(@PathVariable("id") Long id) {
		return multiCurrencyService.getMultiCurrency(id);
	}
	
	@RequestMapping(value = "/product-multicurrencies")
	public List<ProductMultiCurrency> getMultiCurrencies() {
		return multiCurrencyService.getMultiCurrencies();
	}
	
	@RequestMapping(value = "/product-multicurrencies", method = RequestMethod.POST)
	public ProductMultiCurrency saveMultiCurrency(@RequestBody ProductMultiCurrency multiCurrency) {
		return multiCurrencyService.saveMultiCurrency(multiCurrency);
	}
	
	@RequestMapping(value = "/product-multicurrencies/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMultiCurrency(@PathVariable("id") Long id) {
		multiCurrencyService.deleteMultiCurrency(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * ProductMember
	 */
	
	@RequestMapping(value = "/product-members/{id}")
	public ProductMember getMember(@PathVariable("id") Long id) {
		return memberService.getMember(id);
	}
	
	@RequestMapping(value = "/product-members")
	public List<ProductMember> getMembers() {
		return memberService.getMembers();
	}
	
	@RequestMapping(value = "/product-members", method = RequestMethod.POST)
	public ProductMember saveMember(@RequestBody ProductMember member) {
		return memberService.saveMember(member);
	}
	
	@RequestMapping(value = "/product-members/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMember(@PathVariable("id") Long id) {
		memberService.deleteMember(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*
	 * ProductShopTunnel
	 */
	
	@RequestMapping(value = "/product-shop-tunnels/{id}")
	public ProductShopTunnel getShopTunnel(@PathVariable("id") Long id) {
		return shopTunnelService.getShopTunnel(id);
	}
	
	@RequestMapping(value = "/product-shop-tunnels")
	public List<ProductShopTunnel> getShopTunnel() {
		return shopTunnelService.getShopTunnels();
	}
	
	@RequestMapping(value = "/product-shop-tunnels", method = RequestMethod.POST)
	public ProductShopTunnel saveShopTunnel(@RequestBody ProductShopTunnel shopTunnel) {
		return shopTunnelService.saveShopTunnel(shopTunnel);
	}
	
	@RequestMapping(value = "/product-shop-tunnels/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteShopTunnel(@PathVariable("id") Long id) {
		shopTunnelService.deleteShopTunnel(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
