<template>
  <div
      class="row warehouse-display rounded-top mx-0 p-3 pb-0"
      v-if="!this.sessionService.isAdmin()"
  >
    <div class="col">
      <strong class="warehouse-select active">{{
          activeUser.team.warehouse.name
        }}</strong>
    </div>
  </div>

  <!-- row containing all names of warehouses and total which the admin can pick    -->
  <div
      class="row warehouse-display rounded-top mx-0 p-3 pb-0"
      v-else-if="this.sessionService.isAdmin()"
  >
    <div class="col-auto">
      <button
          v-if="!hasNoTotalOption"
          type="button"
          class="warehouse-select btn btn-link p-0"
          :class="{ active: activeWarehouse === 'Total' }"
          @click="$emit('setActiveWarehouse','Total')"
      >
        <strong>{{ totalText }}</strong>
      </button>
    </div>
    <div class="col-auto" v-for="(warehouse) in warehouses" :key="warehouse.id">
      <button
          type="button"
          class="warehouse-select btn btn-link p-0"
          :class="{ active: warehouse.name === activeWarehouse.name}"
          @click="$emit('setActiveWarehouse', warehouse)"
      >
        <strong>{{ warehouse.name }}</strong>
      </button>
    </div>
  </div>
</template>

<script>
/**
 * Display for all warehouse which are connected to Solar Sedum,
 * Makes it possible to handle which warehouse is active at the initialisation of the parent component.
 *
 * @author Julian Kruithof
 */
export default {
  name: "WarehouseHeaderDisplay",
  inject: ["warehouseService", "sessionService"],
  emits: ["setActiveWarehouse"],
  props: {
    activeWarehouse: {},

    //option to have a total overview, or only overview for each separate warehouse
    hasNoTotalOption: Boolean,
    //text of the total option
    totalText: String
  },
  data() {
    return {
      warehouses: [],
      activeUser: this.sessionService.currentUser
    }
  },
  methods: {
    /**
     * Get a warehouse object with name and id, from the name in the url
     * @param name the name of a warehouse
     * @return {Warehouse|string} a warehouse or total if the name is total
     */
    findWarehouseByName(name) {
      if (name === "Total") return "Total";
      return this.warehouses.find((warehouse) => name === warehouse.name);
    }
  },

  /**
   * Find all warehouses and emit to the parentComponent, which is the active warehouse
   *
   */
  async created() {

    if (!this.sessionService.isAdmin()) {
      this.$emit('setActiveWarehouse', this.activeUser.team.warehouse);
      return;
    }
    try {
      this.warehouses = await this.warehouseService.findAll();
    } catch (error) {
      this.warehouses = [];
      if (!this.hasNoTotalOption) {
        this.$emit("setActiveWarehouse", "Total");
        return;
      }
      this.$emit("setActiveWarehouse", null)

      return;
    }

    if (this.$route.params.warehouse) {
      const warehouse = this.findWarehouseByName(this.$route.params.warehouse);
      //if a warehouse is found in the route, set is as the active warehouse
      if (warehouse !== undefined) {
        this.$emit('setActiveWarehouse', warehouse);
        return
      }
    }
    //if there is no total default the active warehouse to the first in the list
    if (this.hasNoTotalOption) {
      this.$emit('setActiveWarehouse', this.warehouses[0])
    } else {
      this.$emit('setActiveWarehouse', "Total");
    }
  }
}
</script>


<style scoped>
.warehouse-display {
  background-color: var(--color-bg);
}

.warehouse-select {
  position: relative;
  transition: 200ms ease-out;
  color: var(--color-text);
  text-decoration: none;
  z-index: 2;
}

.warehouse-select:hover,
.warehouse-select:focus {
  color: var(--color-secondary);
  outline: none;
}

/*
Overwriting bootstrap active class
 */
.warehouse-select.active,
.warehouse-select:first-child:active {
  color: var(--color-primary);
}

.warehouse-select::before {
  content: "";
  position: absolute;
  width: 0;
  height: 3px;
  bottom: -0.25em;
  border-radius: 10px 10px 0 0;
  transition: 200ms ease-out;
}

.warehouse-select.active::before,
.warehouse-select:hover::before,
.warehouse-select:focus::before {
  width: 100%;
  background-color: var(--color-primary);
}

.warehouse-select:not(.active):hover::before,
.warehouse-select:not(.active):focus::before {
  background-color: var(--color-secondary);
}
</style>