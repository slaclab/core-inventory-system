package edu.stanford.slac.code_inventory_system.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Is the new element to add to the inventory")
public record InventoryElementSummaryDTO(
        @Schema(description = "Is the unique id")
        String id,
        @Schema(description = "Is the name of the element")
        String name,
        @Schema(description = "Specify the domain which the item belong")
        String domainId,
        @Schema(description = "Is the {@link InventoryClassDTO#id} of one of the existing class")
        String classId,
        @Schema(description = "The list of tag that describe the element")
        List<TagDTO> tags,
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @Schema(description = "The creation time")
        LocalDateTime createdDate,
        @Schema(description = "The user that creates the element")
        String createdBy,
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @Schema(description = "The modification time")
        LocalDateTime lastModifiedDate,
        @Schema(description = "The user that modify the element")
        String lastModifiedBy
) {
}
