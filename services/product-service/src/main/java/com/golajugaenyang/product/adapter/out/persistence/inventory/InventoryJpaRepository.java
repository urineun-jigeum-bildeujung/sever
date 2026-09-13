package com.golajugaenyang.product.adapter.out.persistence.inventory;

import com.golajugaenyang.product.domain.inventory.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

interface InventoryJpaRepository extends JpaRepository<Inventory, Long> {

}
