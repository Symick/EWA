package nl.solar.app.rest;

import nl.solar.app.DTO.DashboardDTO;
import nl.solar.app.DTO.InventoryDTO;
import nl.solar.app.models.Inventory;
import nl.solar.app.repositories.InventoryRepository;
import nl.solar.app.repositories.jpaRepositories.DashboardRepositoryJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/dashboard-items")
public class DashboardController {

    private final DashboardRepositoryJpa dashboardRepo;

    @Autowired
    public DashboardController(DashboardRepositoryJpa dashboardRepo) {
        this.dashboardRepo = dashboardRepo;
    }

    @GetMapping("/inventory")
    public List<DashboardDTO> getDashboardItems() {
        return dashboardRepo.getDashboardItems();
    }

    @GetMapping("/project")
    public List<DashboardDTO> getProjectDashboardItems() {
        return dashboardRepo.getProjectDashboardItems();
    }

    @GetMapping("/inventory-quantity")
    public List<DashboardDTO> getInventoryQuantity() {
        return dashboardRepo.getInventoryQuantity();
    }

    @Autowired
    InventoryRepository inventoryRepo;

    // quantity = the amount added by an order
// inventoryQuantity = the current inventory level
// amountOfProduct = the amount used by a project
    @Scheduled(fixedRate = 20000) // fixedRate is low for testing (fixedRate = 24, timeUnit = TimeUnit.HOURS) for daily
    protected void forecastNotification () {
        System.out.println("Scheduled method called:\n");

        List<DashboardDTO> originalQuantityList = getDashboardItems();
        List<DashboardDTO> originalAmountOfProductList = getProjectDashboardItems();

        // method to combine projects on the same date
        List<DashboardDTO> combinedQuantityList = combineQuantityList(originalQuantityList);

    }

    // method to combine the quantities of a certain product for orders on the same day
    private static List<DashboardDTO> combineQuantityList(List<DashboardDTO> originalQuantityList) {
        Map<String, DashboardDTO> combinedMap = new HashMap<>();

        for (DashboardDTO dto : originalQuantityList) {
            String key = dto.getWarehouseName() + "_" + dto.getProductName() + "_" + dto.getDeliverDate();

            if (combinedMap.containsKey(key)) {
                DashboardDTO combinedDTO = combinedMap.get(key);
                combinedDTO.setQuantity(combinedDTO.getQuantity() + dto.getQuantity());
            } else {
                combinedMap.put(key, dto);
            }
        }

        return new ArrayList<>(combinedMap.values());
    }

}
