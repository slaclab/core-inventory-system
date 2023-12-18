package edu.stanford.slac.code_inventory_system.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "The summarized information of a class, usually used into the lists")
public record InventoryClassSummaryDTO(
        @Schema(description = "The unique identifier of the inventory class")
        String id,
        @Schema(description = "The name of the inventory class")
        String name
) {
}
