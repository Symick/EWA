export class ResourceAdaptor {
  resourceUrl;

  constructor(resourceUrl) {
    this.resourceUrl = resourceUrl;
  }

  async fetchJSON(url, options = null) {
    let response = await fetch(url, options);
    if (response.ok) {
      return await response.json();
    } else {
      const error = await response.json();
      //Log the error if there is an error provided by the http response body.
      return Promise.reject({code: error.status, reason: error.message});
    }
  }

  async findAll() {
    return await this.fetchJSON(`${this.resourceUrl}/resources`);
  }

  async findAllForWarehouse(id){
    return await this.fetchJSON(`${this.resourceUrl}/warehouses/${id}/resources`)
  }

  async findByIds(wId, pId) {
    return await this.fetchJSON(`${this.resourceUrl}/warehouses/${wId}/products/${pId}`)
  }

  async updateResource(resource) {
    return await this.fetchJSON(`${this.resourceUrl}/warehouses/${resource.warehouse.id}/products/${resource.product.id}`, {
      method: "PUT",
      headers: {"content-type": "application/json"},
      body: JSON.stringify(resource)
    })
  }
}