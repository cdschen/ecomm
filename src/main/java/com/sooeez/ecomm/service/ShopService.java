package com.sooeez.ecomm.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.sooeez.ecomm.domain.Shop;
import com.sooeez.ecomm.domain.ShopTunnel;
import com.sooeez.ecomm.domain.Warehouse;
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
	
	public List<Shop> getShops(Shop shop, Sort sort) {
		return this.shopRepository.findAll(getShopSpecification(shop), sort);
	}

	public Page<Shop> getPagedShops(Pageable pageable, Shop shop) {
		return this.shopRepository.findAll(getShopSpecification(shop), pageable);
	}
	
	private Specification<Shop> getShopSpecification(Shop shop) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.get("deleted"), shop.getDeleted() != null && shop.getDeleted() == true ? true : false));
			if (shop.getShopIds() != null && shop.getShopIds().length > 0) {
				predicates.add(root.get("id").in(shop.getShopIds()));
			}
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
	
	/* 设置店铺的默认通道和下面的默认仓库  */
	public void initShopDefaultTunnel(Shop shop){
		for (ShopTunnel tunnel: shop.getTunnels()) {
			if (tunnel.getDefaultOption()) {
				shop.setDefaultTunnel(tunnel);
				for (Warehouse warehouse: tunnel.getWarehouses()) {
					if (shop.getDefaultTunnel().getDefaultWarehouseId().longValue() == warehouse.getId().longValue()) {
						shop.getDefaultTunnel().setDefaultWarehouse(warehouse);
						break;
					}
				}
				break;
			}
		}
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
