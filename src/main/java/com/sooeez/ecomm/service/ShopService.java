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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sooeez.ecomm.domain.Shop;
import com.sooeez.ecomm.domain.ShopTunnel;
import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.repository.ShopRepository;

@Service
public class ShopService {

	/*
	 * Repository
	 */

	@Autowired
	private ShopRepository shopRepository;

	/*
	 * Shop
	 */

	@Transactional
	public Shop saveShop(Shop shop) {
		
		if (shop.getTunnels() != null && shop.getTunnels().size() > 0) {
			// set default tunnel for shop
			for (ShopTunnel tunnel : shop.getTunnels()) {
				if (tunnel.getDefaultOption()) {
					shop.setDefaultTunnel(tunnel);
					break;
				}
			}
		}
		
		return shopRepository.save(shop);
	}

	@Transactional
	public void deleteShop(Long id) {
		shopRepository.delete(id);
	}
	
	public Boolean existsShop(Shop shop) {
		return shopRepository.count(getShopSpecification(shop)) > 0 ? true : false;
	}

	public Shop getShop(Long id) {
		return shopRepository.findOne(id);
	}

	public List<Shop> getShops(Shop shop, Sort sort) {
		return shopRepository.findAll(getShopSpecification(shop), sort);
	}

	public Page<Shop> getPagedShops(Shop shop, Pageable pageable) {
		return shopRepository.findAll(getShopSpecification(shop), pageable);
	}

	private Specification<Shop> getShopSpecification(Shop shop) {

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (shop.getId() != null) {
				if (shop.getCheckUnique() != null && shop.getCheckUnique().booleanValue() == true) {
					predicates.add(cb.notEqual(root.get("id"), shop.getId()));
				} else {
					predicates.add(cb.equal(root.get("id"), shop.getId()));
				}
			}
			if (StringUtils.hasText(shop.getName())) {
				predicates.add(cb.equal(root.get("name"), shop.getName()));
			}
			if (shop.getEnabled() != null) {
				predicates.add(cb.equal(root.get("enabled"), shop.getEnabled()));
			}
			if (shop.getShopIds() != null && shop.getShopIds().length > 0) {
				predicates.add(root.get("id").in((Object)shop.getShopIds()));
			}
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
		
	}

	/* 设置店铺的默认通道和下面的默认仓库 */
	public void initShopDefaultTunnel(Shop shop) {
		for (ShopTunnel tunnel : shop.getTunnels()) {
			if (tunnel.getDefaultOption()) {
				shop.setDefaultTunnel(tunnel);
				for (Warehouse warehouse : tunnel.getWarehouses()) {
					if (shop.getDefaultTunnel().getDefaultWarehouseId().longValue() == warehouse.getId().longValue()) {
						shop.getDefaultTunnel().setDefaultWarehouse(warehouse);
						break;
					}
				}
				break;
			}
		}
	}

}
