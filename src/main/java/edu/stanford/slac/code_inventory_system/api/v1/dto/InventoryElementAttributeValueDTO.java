package edu.stanford.slac.code_inventory_system.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Describe attribute element")
public record InventoryElementAttributeValueDTO(
        @NotEmpty(message = "Name is mandatory field")
        @Schema(description = "The name of the attribute")
        String name,
        @NotEmpty(message = "Description is mandatory field")
        @Schema(description = "The string representation of the attribute value")
        String value
) {
}
