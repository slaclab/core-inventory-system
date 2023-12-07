package edu.stanford.slac.code_inventory_system.model;

import lombok.*;
import org.springframework.data.annotation.*;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * An InventoryClass defines the different classes of items and cables in your system, specifying their attributes and connectivity rules.
 * {
 *   "_id": "serverClass",
 *   "name": "Server",
 *   "type": "Item",
 *   "attributes": {
 *     {name:"CPU", "mandatory": true, "type": "string", "unit": ""},
 *     {name:"RAM", "mandatory": true, "type": "string", "unit": "GB"},
 *     {name:""PowerSupply", ""mandatory": false, "type": "string", "unit": "Watts"},
 *     {name:""Storage", "mandatory": true, "type": "string", "unit": "TB"}
 *   },
 *   "connectableClasses": ["powerSupplyClass", "networkDeviceClass"]
 * }
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InventoryClass {
    @Id
    String id;
    /**
     * define the name of the inventory element
     */
    String name;
    /**
     * The description of the class
     */
    String description;
    /**
     * Define to which type belong the element
     */
    InventoryClassType type;
    /**
     * Define the list for that can be used to specialize the element
     */
    @Builder.Default
    List<InventoryClassAttribute> attributes = Collections.emptyList();
    @CreatedDate
    private LocalDateTime createdDate;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    @LastModifiedBy
    private String lastModifiedBy;
    @Version
    private Long version;
}