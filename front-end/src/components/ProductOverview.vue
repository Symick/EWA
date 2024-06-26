<template>
  <div>
    <table-component
      v-if="!productsAreLoading"
      :amount-to-display="amountToDisplay"
      :has-add-button="hasAddButton"
      :has-delete-button="hasDeleteButton"
      :has-edit-button="hasEditButton"
      :table-data="products"
      :has-search-bar="true"
      @edit="showEditModal"
      @delete="showDeleteModal"
      @add="showAddModal"
    />
    <!--    Templated doesn't wait for loading so show spinner for user information-->
    <spinner-component v-else></spinner-component>

    <Transition>
      <modal-component
        v-if="showModal"
        :title="modalTitle"
        :active-modal="modalBodyComponent"
        :item="modalProduct"
        :ok-btn-text="okBtnText"
        @cancel-modal-btn="this.showModal = false"
        @corner-close-modal-btn="this.showModal = false"
        @ok-modal-btn="handleOk"
      />
    </Transition>

    <transition>
      <toast-component
        v-if="showToast"
        :toast-message="toastMessage"
        :toast-title="toastTitle"
      ></toast-component>
    </transition>
  </div>
</template>
<script>
import TableComponent from "@/components/table/TableComponent.vue";
import ModalComponent from "@/components/modal/ModalComponent.vue";
import SpinnerComponent from "@/components/util/SpinnerComponent.vue";
import ToastComponent from "@/components/util/ToastComponent.vue";

/**
 * Component for the product overview. This overview give info about the different products of solar sedum.
 * The information include the id, name and description.
 *
 * Furthermore, the crud operation add, delete and update are handle by the overview, via the corresponding modals
 *
 * @author Julian Kruithof
 */
export default {
  name: "ProductOverview",
  components: {
    ToastComponent,
    SpinnerComponent,
    ModalComponent,
    TableComponent,
  },
  inject: ["productService", "sessionService"],
  data() {
    return {
      products: [
        {
          id: Number,
          productName: String,
          description: String,
        },
      ],
      showModal: false,
      modalTitle: "",
      modalBodyComponent: "",
      modalProduct: {
        id: Number,
        productName: String,
        description: String,
      },
      okBtnText: "",
      MODAL_TYPES: Object.freeze({
        DELETE: "delete-product-modal",
        UPDATE: "update-product-modal",
        ADD: "add-product-modal",
      }),
      productsAreLoading: true,

      //toast variables
      showToast: false,
      toastTitle: "",
      toastMessage: "",
      amountToDisplay: 10,
      userTypes: Object.freeze({
        ADMIN: "ADMIN",
        VIEWER: "VIEWER",
      }),
      hasAddButton: true,
      hasDeleteButton: true,
      hasEditButton: true,
    };
  },
  methods: {
    /**
     * Show the delete modal with corresponding product info
     * @param product product to be deleted
     */
    showDeleteModal(product) {
      this.modalTitle = "Delete product";
      this.modalBodyComponent = this.MODAL_TYPES.DELETE;
      this.modalProduct = product;
      this.okBtnText = "Delete";
      this.showModal = true;
    },

    /**
     * open the edit modal to update a product
     * @param product the product to be updated
     */
    showEditModal(product) {
      this.modalTitle = "Update product";
      this.modalBodyComponent = this.MODAL_TYPES.UPDATE;
      this.modalProduct = product;
      this.okBtnText = "Save";
      this.showModal = true;
    },

    /**
     * open the add modal to add a new product
     */
    showAddModal() {
      this.modalTitle = "Add product";
      this.modalBodyComponent = this.MODAL_TYPES.ADD;
      this.okBtnText = "Add";
      this.showModal = true;
    },

    /**
     * Execute the correct method depending on which modal the ok button was clicked on
     * For updating it is Save for delete it is Delete etc.
     * @param product the product to be processed by the method, for example for the delete this is the product to be deleted
     * @param modal the modal on which the ok button was pressed
     */
    handleOk(product, modal) {
      switch (modal) {
        case this.MODAL_TYPES.DELETE:
          this.deleteProduct(product);
          break;
        case this.MODAL_TYPES.UPDATE:
          this.updateProduct(product);
          break;
        case this.MODAL_TYPES.ADD:
          this.addProduct(product);
          break;
      }
    },

    /**
     * Delete a product in the back-end and front-end
     * @param product the product to be deleted
     * @return {Promise<void>}
     */
    async deleteProduct(product) {
      try {
        const deleted = await this.productService.delete(product.id);
        this.products = this.products.filter(
          (product) => product.id !== deleted.id
        );
        this.showModal = false;
        this.showTimedToast(
          "Deleted product",
          `Successfully deleted the product: ${deleted.productName}`
        );
      } catch (exception) {
        this.showModal = false;
        this.handleException(exception, "Failed to delete product");
      }
    },

    /**
     * Update a product in the back-end and front-end
     * @param product the product to be updated
     * @return {Promise<void>}
     */
    async updateProduct(product) {
      try {
        const updated = await this.productService.update(product);
        this.products = this.products.map((product) =>
          product.id === updated.id ? updated : product
        );
        this.showModal = false;
        this.showTimedToast(
          "Updated Product",
          `Successfully updated the product: ${updated.productName}`
        );
      } catch (e) {
        this.showModal = false;
        this.handleException(e, "Failed to update product");
      }
    },

    /**
     * Add a product to the back-end and front-end
     * @param product the product to be added
     * @return {Promise<void>}
     */
    async addProduct(product) {
      try {
        const added = await this.productService.add(product);
        this.products.push(added);
        this.showModal = false;
        this.showTimedToast(
          "Added product!",
          `Successfully added the product: ${product.productName}`
        );
      } catch (e) {
        this.showModal = false;
        this.handleException(e, "Failed to add product");
      }
    },

    /**
     * Formats the product data for when the data is empty.
     * @param product the product to be formatted
     * @return {{id: undefined, productName: undefined, description: undefined}}
     */
    formatEmptyTableData() {
      return {
        id: "",
        productName: "",
        description: "",
      };
    },

    /**
     * Show a toast to give the user some information about how the Crud operation went
     * @param title the title of the toast
     * @param message the to show to the user
     */
    showTimedToast(title, message) {
      this.toastTitle = title;
      this.toastMessage = message;
      this.showToast = true;

      // after 4 seconds remove the toast from view
      setTimeout(() => (this.showToast = false), 4000);
    },

    /**
     * Handles the exception and shows a toast to the user.
     * @param {{code: Number, reason: String}} exception The exception to be handled.
     * @param {String} exceptionTitle The title of the exception.
     */
    handleException(exception, exceptionTitle) {
      // If the exception is a client-error, show the reason of the exception.
      if (exception.code >= 400 && exception.code < 500) {
        this.showTimedToast(exceptionTitle, exception.reason);
      } else {
        // If the exception is a server-error, show a generic message.
        this.showTimedToast(exceptionTitle, "Something went wrong");
      }
    },
  },

  async created() {
    // Get the current active user type.
    const user = await this.sessionService.currentUser;

    if (user.type === this.userTypes.VIEWER) {
      this.hasAddButton = false;
      this.hasDeleteButton = false;
      this.hasEditButton = false;
    }

    //clear the product so that the native check is deleted.
    this.products = [];
    const data = await this.productService.findAll();

    // If there are no products, add only the table headers.
    if (data.length === 0) {
      this.products = [this.formatEmptyTableData()];

      this.productsAreLoading = false;
      return;
    }

    this.products = data;

    if (this.products.length < this.amountToDisplay) {
      this.amountToDisplay = this.products.length;
    }

    this.productsAreLoading = false;
  },
};
</script>

<style scoped></style>
