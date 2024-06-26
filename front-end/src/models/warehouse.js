export class Warehouse {
    id;
    name;
    location;

    static names =
        [
            "Solar Sedum",
            "Superzon",
            "The switch",
            "Induct",
            "EHES"
        ]

    static locations =
        [
            "H.J.E. Wenckebachweg 47D, 1096AK Amsterdam",
            "Marconistraat 4A, 1704RG Heerhugowaard",
            "Barndegat 8, 1505HN Zaandam",
            "Philippusweg 2, 3125AS Schiedam",
            "Bolwerk 5, 3905NH Veenendaal"
        ]

    constructor(id, name, location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    static createDummyWarehouses() {
        const warehouses = [];

        for (let i = 0; i < 5; i++) {
            const warehouse = new Warehouse((i+1),this.names[i], this.locations[i]);
            warehouses.push(warehouse);
        }
        return warehouses;
    }

    static copyConstructor(warehouse) {
        if (warehouse == null) return null;
        return Object.assign(new Warehouse(), warehouse)
    }
}