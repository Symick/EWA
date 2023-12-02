package nl.solar.app.repositories;

import nl.solar.app.models.Product;
import nl.solar.app.models.Inventory;
import nl.solar.app.models.Warehouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("RESOURCES.INMEMORY")
public class InventoryRepositoryMock implements InventoryRepository {
    private List<Inventory> inventoryList;


    private final EntityRepository<Product> productRepo;
    private final EntityRepository<Warehouse> warehouseRepo;

    @Autowired
    public InventoryRepositoryMock(EntityRepository<Product> productRepo, EntityRepository<Warehouse> warehouseRepo) {
        this.productRepo = productRepo;
        this.warehouseRepo = warehouseRepo;
        this.inventoryList = new ArrayList<>();
        List<Product> products = this.productRepo.findAll();
        List<Warehouse> warehouses = this.warehouseRepo.findAll();

        for (Warehouse warehouse: warehouses) {
            for (Product product: products) {
                Inventory inventory = Inventory.createDummyResource(warehouse, product);
                inventoryList.add(inventory);
            }
        }

    }

    @Override
    public List<Inventory> findAll() {
        return inventoryList;
    }

    @Override
    public List<Inventory> findInventoryForWarehouse(long warehouseId) {
        return inventoryList.stream().filter(resource -> resource.getWarehouse().getId() == warehouseId).toList();
    }

    @Override
    public Inventory findByIds(long warehouseId, long productId) {
        return inventoryList.stream()
                .filter(resource -> resource.getWarehouse().getId() == warehouseId && resource.getProduct().getId() == productId)
                .findFirst().orElse(null);
    }

    @Override
    public void addInventoryForProduct(Product product) {
        for (Warehouse warehouse: warehouseRepo.findAll()) {
            Inventory inventory = Inventory.createDummyResource(warehouse, product);
            inventory.setQuantity(0);
            inventoryList.add(inventory);
        }
    }

    @Override
    public void addInventoryForWarehouse(Warehouse warehouse) {
        for (Product product: productRepo.findAll()) {
            Inventory inventory = Inventory.createDummyResource(warehouse, product);
            inventory.setQuantity(0);
            inventoryList.add(inventory);
        }
    }

    @Override
    public Inventory deleteByIds(long warehouseId, long productId) {
        Inventory inventory = findByIds(warehouseId, productId);

        if (inventory != null) {
            inventoryList.remove(inventory);
        }
        return inventory;
    }

    @Override
    public void deleteInventoryForProduct(Product product) {
        for (Warehouse warehouse : warehouseRepo.findAll()) {
            deleteByIds(warehouse.getId(), product.getId());
        }
    }

    @Override
    public void deleteInventoryForWarehouse(Warehouse warehouse) {
        for (Product product : productRepo.findAll()) {
            deleteByIds(warehouse.getId(), product.getId());
        }
    }

    @Override
    public Inventory save(Inventory inventory) {
        int index = inventoryList.indexOf(inventory);
        System.out.println(index);
        if (index != -1) {
            inventoryList.set(index, inventory);
        } else {
            inventoryList.add(inventory);
        }
        return inventory;
    }
}
