package nl.solar.app.repositories;

import nl.solar.app.models.Product;
import nl.solar.app.models.Inventory;
import nl.solar.app.models.Warehouse;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Since a Resource is a lookup between a product and a warehouse it isn't possible to use the global entityRepo
 * Therefore is has its own.
 *
 * @author Julian Kruithof
 */
public interface InventoryRepository extends ManyToManyRepository<Inventory> {

    /**
     * Find all resources for a specific warehouse
     * @param warehouseId the id of the warehouse
     * @return a list of resources where the warehouse is the same
     */
    List<Inventory> findInventoryForWarehouse(long warehouseId);

    /**
     * Add resources for a new product for all warehouses
     * @param product the newly created product
     */
    void addInventoryForProduct(Product product);

    /**
     * Add resources for all existing product for a new warehouse
     * @param warehouse the newly created warehouse
     */
    void addInventoryForWarehouse(Warehouse warehouse);


    /**
     * delete resources connected to a product which is deleted
     * @param product the product recently deleted
     * @throws UnsupportedOperationException if method is not implemented because for example cascading rules are used
     */
    default void deleteInventoryForProduct(Product product) {
        throw new UnsupportedOperationException("No implementation should be needed if cascading is on");
    };

    /**
     * delete resources connected to a warehouse which is deleted
     * @param warehouse the warehouse recently deleted
     * @throws UnsupportedOperationException if method is not implemented because for example cascading rules are used
     */
    default void deleteInventoryForWarehouse(Warehouse warehouse) {
        throw new UnsupportedOperationException("No implementation should be needed if cascading is on");
    };
}
