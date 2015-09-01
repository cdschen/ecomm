package com.sooeez.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sooeez.ecomm.domain.Brand;
import com.sooeez.ecomm.domain.Shop;
import com.sooeez.ecomm.domain.ShopTunnel;
import com.sooeez.ecomm.repository.ShopRepository;
import com.sooeez.ecomm.repository.ShopTunnelRepository;

@Service
public class ShopService {

	@Autowired private ShopRepository shopRepository;
	
	@Autowired private ShopTunnelRepository shopTunnelRepository;
	
	/*
	 * Shop
	 */
	
	public Shop saveShop(Shop shop) {
		return this.shopRepository.save(shop);
	}
	
	public void deleteShop(Long id) {
		this.shopRepository.delete(id);
	}
	
	public Shop getShop(Long id) {
		return this.shopRepository.findOne(id);
	}
	
	public List<Shop> getShops() {
		return this.shopRepository.findAll();
	}

	public Page<Shop> getPagedShops(Pageable pageable) {
		return this.shopRepository.findAll(pageable);
	}
	
	/*
	 * ShopTunnel
	 */
	
	public ShopTunnel saveShopTunnel(ShopTunnel shopTunnel) {
		return this.shopTunnelRepository.save(shopTunnel);
	}
	
	public void deleteShopTunnel(Long id) {
		this.shopTunnelRepository.delete(id);
	}
	
	public ShopTunnel getShopTunnel(Long id) {
		return this.shopTunnelRepository.findOne(id);
	}
	
	public List<ShopTunnel> getShopTunnels() {
		return this.shopTunnelRepository.findAll();
	}

	public Page<ShopTunnel> getPagedShopTunnels(Pageable pageable) {
		return this.shopTunnelRepository.findAll(pageable);
	}
	
	
}
