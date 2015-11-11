package com.sooeez.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sooeez.ecomm.domain.ProductShopTunnel;
import com.sooeez.ecomm.repository.ProductShopTunnelRepository;

@Service
public class ProductShopTunnelService {

	/*
	 * Repository
	 */

	@Autowired
	private ProductShopTunnelRepository shopTunnelRepository;

	/*
	 * ProductShopTunnel
	 */

	@Transactional
	public ProductShopTunnel saveShopTunnel(ProductShopTunnel shopTunnel) {
		return shopTunnelRepository.save(shopTunnel);
	}

	@Transactional
	public void deleteShopTunnel(Long id) {
		shopTunnelRepository.delete(id);
	}

	public ProductShopTunnel getShopTunnel(Long id) {
		return shopTunnelRepository.findOne(id);
	}

	public List<ProductShopTunnel> getShopTunnels() {
		return shopTunnelRepository.findAll();
	}

	public Page<ProductShopTunnel> getPagedShopTunnels(Pageable pageable) {
		return shopTunnelRepository.findAll(pageable);
	}
}
