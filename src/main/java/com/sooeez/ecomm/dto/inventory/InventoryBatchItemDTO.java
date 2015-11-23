package com.sooeez.ecomm.dto.inventory;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.sooeez.ecomm.domain.InventoryBatch;
import com.sooeez.ecomm.domain.Product;
import com.sooeez.ecomm.domain.User;
import com.sooeez.ecomm.domain.Warehouse;
import com.sooeez.ecomm.domain.WarehousePosition;

public class InventoryBatchItemDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long productId;
	private Long warehouseId;
	private Long warehousePositionId;
	private Long userId;
	private Long executeOperatorId;
	private Long inventoryBatchId;
	private Long outBatchId;
	private Long changedQuantity;
	private Long actualQuantity;
	private String inventorySnapshot;
	private Date expireDate;
	private Date createTime;
	private Date lastTime;
	private Integer batchType;
	private Integer batchOperate;
	private String memo;

	private Product product = new Product();
	//private Warehouse warehouse = new Warehouse();
	private WarehousePosition position = new WarehousePosition();
	//private User user = new User();
	//private User executeOperator = new User();
	private InventoryBatch outBatch = new InventoryBatch();

}
