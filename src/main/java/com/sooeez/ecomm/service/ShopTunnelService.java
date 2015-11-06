package com.sooeez.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sooeez.ecomm.domain.ShopTunnel;
import com.sooeez.ecomm.repository.ShopTunnelRepository;

@Service
public class ShopTunnelService {

	/*
	 * Repository
	 */
	
	@Autowired private ShopTunnelRepository tunnelRepository;
	
	/*
	 * ShopTunnel
	 */
	
	@Transactional
	public ShopTunnel saveTunnel(ShopTunnel tunnel) {
		return tunnelRepository.save(tunnel);
	}
	
	@Transactional
	public void deleteTunnel(Long id) {
		tunnelRepository.delete(id);
	}
	
	public ShopTunnel getTunnel(Long id) {
		return tunnelRepository.findOne(id);
	}
	
	public List<ShopTunnel> getTunnels() {
		return tunnelRepository.findAll();
	}

	public Page<ShopTunnel> getPagedTunnels(Pageable pageable) {
		return tunnelRepository.findAll(pageable);
	}
}
