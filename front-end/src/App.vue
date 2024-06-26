<template>
  <div v-if="isLoggedIn === false">
    <router-view></router-view>
  </div>
  <div v-else class="view">
    <sidebar/>
    <header-component class="header"></header-component>
    <router-view id="component"></router-view>
  </div>
</template>

<script>
import Sidebar from "@/components/Sidebar.vue";
import HeaderComponent from "@/components/HeaderComponent.vue";
import appConfig from "@/appConfig";
import {ProductAdaptor} from "@/service/productAdaptor";
import {WarehouseAdaptor} from "@/service/warehouseAdaptor";
import {UserAdaptor} from "@/service/userAdaptor";
import {InventoryAdaptor} from "@/service/inventoryAdaptor";
import {TeamAdaptor} from "@/service/teamAdaptor";
import {EmailAdaptor} from "@/service/emailAdaptor";
import {ProjectAdaptor} from "@/service/projectAdaptor";
import {OrderAdaptor} from "@/service/orderAdaptor";
import {SessionSbService} from "@/service/SessionSbService";
import {shallowReactive} from "vue";
import {FetchInterceptor} from "@/service/FetchInterceptor";
import {DashboardAdaptor} from "@/service/dashboardAdaptor";


export default {
  name: "App",
  components: {
    HeaderComponent,
    Sidebar,
  },
  provide() {
    this.theSessionService = shallowReactive(new SessionSbService(appConfig.BACKEND_URL +
        '/authentication', appConfig.JWT_STORAGE_ITEM));
    this.theFetchInterceptor = new FetchInterceptor(this.theSessionService, this.$router);
    return {
      //session service
      sessionService: this.theSessionService,

      productService: new ProductAdaptor(`${appConfig.BACKEND_URL}/products`),
      warehouseService: new WarehouseAdaptor(
          `${appConfig.BACKEND_URL}/warehouses`
      ),
      userService: new UserAdaptor(`${appConfig.BACKEND_URL}/users`),
      inventoryService: new InventoryAdaptor(appConfig.BACKEND_URL),
      teamsService: new TeamAdaptor(`${appConfig.BACKEND_URL}/teams`),
      emailService: new EmailAdaptor(`${appConfig.BACKEND_URL}`),
      projectService: new ProjectAdaptor(`${appConfig.BACKEND_URL}/projects`),
      orderService: new OrderAdaptor(`${appConfig.BACKEND_URL}/orders`),
      dashboardService: new DashboardAdaptor(`${appConfig.BACKEND_URL}/dashboard-items`)
    }
  },
  beforeUnmount() {
    this.theFetchInterceptor.unregister();
  },
  //checks to see if the user is logged in
  computed: {
    isLoggedIn() {
      return this.theSessionService.isAuthenticated();
    },
  },
};
</script>

<style>
.view {
  grid-template-areas:
    "sidebar header"
    "sidebar component";
  grid-template-columns: var(--sidebar-width) 1fr;
  display: grid;
}

#component {
  grid-area: component;
  margin: 0 48px 48px 48px;
}

.header {
  grid-area: header;
}

/* Colors used by Solar Sedum*/
:root {
  --color-primary: #c7d02c;
  --color-secondary: #572700;
  --color-tertiary: #111827;
  --color-text: #333;
  --color-bg: #fff;
  --color-subtitle: #bfbfbf;
  --color-text-bg: #f8f8f8;

  --btn-secondary-shadow-color: 64, 29, 0; /*rgb because of the box-shadow*/

  --sidebar-width: 17rem;

  --custom-box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.05),
  0 4px 6px -4px rgb(0 0 0 / 0.05);
}

body {
  font-family: "Montserrat", sans-serif !important;
  color: var(--color-text) !important;
  background-color: var(--bs-gray-100) !important;
}
</style>
