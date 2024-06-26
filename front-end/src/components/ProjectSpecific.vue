<template>
  <div>
    <div v-if="!showErrorMessage" class="grid row gap-5">
      <div class="card border-0 d-flex col-xl-4 p-4">
        <div v-if="projectLoaded" class="px-1">
          <h3 class="fw-bold fs-2 project-title">
            {{ project.projectName }}
          </h3>
          <span class="badge mb-4" :class="STATUS_OPTIONS[project.status]">{{
            projectStatus
          }}</span>
          <p class="fw-bold mb-2">
            Client: <span class="fw-normal">{{ project.client }}</span>
          </p>
          <p class="fw-bold mb-2">
            Due Date:
            <span class="fw-normal">{{ dueDate }}</span>
          </p>
          <p class="fw-bold mb-2">
            Assigned Team: <span class="fw-normal">{{ project.teamName }}</span>
          </p>
          <p class="fw-bold">
            Description:
            <span class="fw-normal">{{ description }}</span>
          </p>
        </div>
        <SpinnerComponent v-else />
      </div>

      <div class="col-xl p-0">
        <TableComponent
          v-if="productsLoaded"
          :table-data="products"
          :amount-to-display="9"
          table-title="Products"
          sub-title="Current products in this project"
        />
        <SpinnerComponent v-else />
      </div>
    </div>

    <!-- If the project is not found, show the error message component. -->
    <ErrorMessage
      v-else
      error-title="Project not found"
      error-message="The project you are looking for does not exist."
      hyperlink="/projects"
    />
  </div>
</template>

<script>
import ErrorMessage from "./ErrorMessage.vue";
import TableComponent from "./table/TableComponent.vue";
import SpinnerComponent from "./util/SpinnerComponent.vue";

/**
/**
 * This file represents the ProjectSpecific component. The component is responsible for displaying project-specific information.
 * 
 * @author Tim Knops
 */
export default {
  name: "SpecificView",
  inject: ["projectService"],
  components: {
    TableComponent,
    SpinnerComponent,
    ErrorMessage,
  },
  data() {
    return {
      project: {},
      products: [],
      productsLoaded: false,
      projectLoaded: false,
      STATUS_OPTIONS: Object.freeze({
        COMPLETED: "success-badge",
        IN_PROGRESS: "in-progress-badge",
        UPCOMING: "upcoming-badge",
      }),
      showErrorMessage: false,
    };
  },
  async created() {
    // Get the specific project data by using the project id from the route.
    this.project = await this.projectService.get(this.$route.params.id);

    // If the project has an error, show the error message component.
    if (this.project.error !== undefined) {
      this.showErrorMessage = true;
      return;
    }

    this.formatProducts();

    this.productsLoaded = true;
    this.projectLoaded = true;
  },
  computed: {
    /** Computed value for the project status. */
    projectStatus() {
      return this.project.status.toUpperCase().replace("_", " ");
    },

    /** Computed value for the project due date. */
    dueDate() {
      return this.project.dueDate.split("T")[0];
    },

    /** Computed value for the project description. If the project has no description, a fallback message is displayed. */
    description() {
      return this.project.description === null
        ? "This project has no description."
        : this.project.description;
    },
  },

  methods: {
    /** Formats the resources so that they can be displayed in the table. */
    formatProducts() {
      if (this.project.resources.length === 0) {
        this.products = [this.formatEmptyTableData()];
        return;
      }

      // For each resource, add the product name and quantity to the products array.
      this.project.resources.forEach((resource) => {
        this.products.push({
          id: resource.product.id,
          product: resource.product.productName,
          quantity: resource.quantity,
        });
      });
    },

    /**
     * Formats the table data when there are no products.
     * @returns {Object} The formatted table data.
     */
    formatEmptyTableData() {
      return {
        id: "",
        product: "",
        quantity: "",
      };
    },
  },
};
</script>

<style scoped>
.card {
  box-shadow: var(--custom-box-shadow);
}

.project-title {
  color: var(--bs-gray-900);
}

.success-badge {
  color: #15803d;
  background-color: #bbf7d0;
}

.in-progress-badge {
  color: #a16207;
  background-color: #fef08a;
}

.upcoming-badge {
  color: #1d4ed8;
  background-color: #bfdbfe;
}

.fw-normal {
  color: var(--bs-gray-700);
}
</style>
