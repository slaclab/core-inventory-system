package edu.stanford.slac.code_inventory_system.service;

import edu.stanford.slac.ad.eed.baselib.exception.ControllerLogicException;
import edu.stanford.slac.code_inventory_system.api.v1.dto.*;
import edu.stanford.slac.code_inventory_system.exception.InventoryDomainAlreadyExists;
import edu.stanford.slac.code_inventory_system.exception.InventoryElementNotFound;
import edu.stanford.slac.code_inventory_system.exception.TagNotFound;
import edu.stanford.slac.code_inventory_system.model.InventoryClass;
import edu.stanford.slac.code_inventory_system.model.InventoryDomain;
import edu.stanford.slac.code_inventory_system.model.InventoryElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureMockMvc
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class InventoryElementServiceTest {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    TestUtilityService testUtilityService;
    @Autowired
    InventoryClassService inventoryClassService;
    @Autowired
    InventoryElementService inventoryElementService;

    @BeforeEach
    public void cleanCollection() {
        mongoTemplate.remove(new Query(), InventoryClass.class);
        mongoTemplate.remove(new Query(), InventoryDomain.class);
        mongoTemplate.remove(new Query(), InventoryElement.class);
    }

    @Test
    public void saveDomainOK() {
        String newDomainId = assertDoesNotThrow(
                () -> inventoryElementService.createNew(
                        NewInventoryDomainDTO
                                .builder()
                                .name("New Domain")
                                .description("This is the description for the new domain")
                                .build()
                )
        );
        assertThat(newDomainId)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    public void saveDomainFailWithSameName() {
        String newDomainId = assertDoesNotThrow(
                () -> inventoryElementService.createNew(
                        NewInventoryDomainDTO
                                .builder()
                                .name("New Domain")
                                .description("This is the description for the new domain")
                                .build()
                )
        );
        assertThat(newDomainId)
                .isNotNull()
                .isNotEmpty();
        InventoryDomainAlreadyExists exceptionForDomainWithSameName = assertThrows(
                InventoryDomainAlreadyExists.class,
                () -> inventoryElementService.createNew(
                        NewInventoryDomainDTO
                                .builder()
                                .name("New Domain")
                                .description("This is the description for the new domain")
                                .build()
                )
        );
        assertThat(exceptionForDomainWithSameName)
                .isNotNull();
        assertThat(exceptionForDomainWithSameName.getErrorCode())
                .isEqualTo(-1);
    }

    @Test
    public void getDomainOK() {
        String newDomainId = assertDoesNotThrow(
                () -> inventoryElementService.createNew(
                        NewInventoryDomainDTO
                                .builder()
                                .name("New Domain")
                                .description("This is the description for the new domain")
                                .build()
                )
        );
        assertThat(newDomainId)
                .isNotNull()
                .isNotEmpty();

        var fullDomain = assertDoesNotThrow(
                () -> inventoryElementService.getFullDomain(newDomainId)
        );

        assertThat(fullDomain).isNotNull()
                .extracting(
                        InventoryDomainDTO::name
                ).isEqualTo(
                        "new-domain"
                );
    }

    @Test
    public void updateDomainWithTag() {
        String newDomainId = assertDoesNotThrow(
                () -> inventoryElementService.createNew(
                        NewInventoryDomainDTO
                                .builder()
                                .name("New Domain")
                                .description("This is the description for the new domain")
                                .build()
                )
        );
        assertThat(newDomainId)
                .isNotNull()
                .isNotEmpty();

        // update domain with tags
        assertDoesNotThrow(
                () -> inventoryElementService.update(
                        newDomainId,
                        UpdateDomainDTO
                                .builder()
                                .name("new-domain")
                                .description("Update the description")
                                .tags(
                                        List.of(
                                                TagDTO
                                                        .builder()
                                                        .name("New tag")
                                                        .build()
                                        )
                                )
                                .build()
                )
        );

        var updatedDomain = assertDoesNotThrow(
                () -> inventoryElementService.getFullDomain(newDomainId)
        );
        // check for tag
        assertThat(updatedDomain.description()).contains("Update the description");
        assertThat(updatedDomain.tags())
                .hasSize(1)
                .extracting(TagDTO::name)
                .contains("new-tag");

        // now update the tag
        InventoryDomainDTO finalUpdatedDomain = updatedDomain;
        assertDoesNotThrow(
                () -> inventoryElementService.update(
                        newDomainId,
                        UpdateDomainDTO
                                .builder()
                                .name("new-domain")
                                .description("Update the description")
                                .tags(
                                        List.of(
                                                finalUpdatedDomain.tags().get(0)
                                                        .toBuilder()
                                                        .name("Updated tag name")
                                                        .build()
                                        )
                                )
                                .build()
                )
        );

        updatedDomain = assertDoesNotThrow(
                () -> inventoryElementService.getFullDomain(newDomainId)
        );
        // check for tag
        assertThat(updatedDomain.description()).contains("Update the description");
        assertThat(updatedDomain.tags())
                .hasSize(1)
                .extracting(TagDTO::name)
                .contains("updated-tag-name");

        // now delete
        assertDoesNotThrow(
                () -> inventoryElementService.update(
                        newDomainId,
                        UpdateDomainDTO
                                .builder()
                                .name("new-domain")
                                .description("Update the description")
                                .tags(
                                        List.of(
                                        )
                                )
                                .build()
                )
        );

        updatedDomain = assertDoesNotThrow(
                () -> inventoryElementService.getFullDomain(newDomainId)
        );
        // check for tag
        assertThat(updatedDomain.description()).contains("Update the description");
        assertThat(updatedDomain.tags())
                .hasSize(0);
    }

    @Test
    public void createElementFailsWithNotMandatoryData() {
        ControllerLogicException checkExceptionNoName = assertThrows(
                ControllerLogicException.class,
                () -> inventoryElementService.createNew(
                        null,
                        NewInventoryElementDTO
                                .builder()
                                .build()
                )
        );
        assertThat(checkExceptionNoName.getErrorCode()).isEqualTo(-1);
        checkExceptionNoName = assertThrows(
                ControllerLogicException.class,
                () -> inventoryElementService.createNew(
                        null,
                        NewInventoryElementDTO
                                .builder()
                                .name("name")
                                .build()
                )
        );
        assertThat(checkExceptionNoName.getErrorCode()).isEqualTo(-1);
        checkExceptionNoName = assertThrows(
                ControllerLogicException.class,
                () -> inventoryElementService.createNew(
                        null,
                        NewInventoryElementDTO
                                .builder()
                                .name("name")
                                .classId("cid")
                                .build()
                )
        );
        assertThat(checkExceptionNoName.getErrorCode()).isEqualTo(-1);
        checkExceptionNoName = assertThrows(
                ControllerLogicException.class,
                () -> inventoryElementService.createNew(
                        "did",
                        NewInventoryElementDTO
                                .builder()
                                .name("name")
                                .build()
                )
        );
        assertThat(checkExceptionNoName.getErrorCode()).isEqualTo(-1);
    }

    @Test
    public void createNewElementOK() {
        String newClassID = assertDoesNotThrow(
                () -> inventoryClassService.createNew(
                        NewInventoryClassDTO
                                .builder()
                                .name("class a")
                                .type(InventoryClassTypeDTO.Building)
                                .attributes(
                                        List.of(
                                                InventoryClassAttributeDTO
                                                        .builder()
                                                        .name("Building Number")
                                                        .mandatory(true)
                                                        .type(InventoryClassAttributeTypeDTO.Number)
                                                        .build()
                                        )
                                )
                                .build()
                )
        );

        String newDomainId = assertDoesNotThrow(
                () -> inventoryElementService.createNew(
                        NewInventoryDomainDTO
                                .builder()
                                .name("New Domain")
                                .description("This is the description for the new domain")
                                .build()
                )
        );

        String newElementId = assertDoesNotThrow(
                () -> inventoryElementService.createNew(
                        newDomainId,
                        NewInventoryElementDTO
                                .builder()
                                .name("Building Control")
                                .description("Main control system building")
                                .classId(newClassID)
                                .attributes(
                                        List.of(
                                                InventoryElementAttributeValue
                                                        .builder()
                                                        .name("building-number")
                                                        .value("34")
                                                        .build()
                                        )
                                )
                                .build()
                )
        );

        assertThat(newElementId).isNotNull().isNotEmpty();
    }

    @Test
    public void createNewElementWithParentOK() {
        String newBuildingClassID = assertDoesNotThrow(
                () -> inventoryClassService.createNew(
                        NewInventoryClassDTO
                                .builder()
                                .name("building class a")
                                .type(InventoryClassTypeDTO.Building)
                                .attributes(
                                        List.of(
                                                InventoryClassAttributeDTO
                                                        .builder()
                                                        .name("Building Number")
                                                        .mandatory(true)
                                                        .type(InventoryClassAttributeTypeDTO.Number)
                                                        .build()
                                        )
                                )
                                .build()
                )
        );
        String newRoomClassID = assertDoesNotThrow(
                () -> inventoryClassService.createNew(
                        NewInventoryClassDTO
                                .builder()
                                .name("room class a")
                                .type(InventoryClassTypeDTO.Building)
                                .attributes(
                                        List.of(
                                                InventoryClassAttributeDTO
                                                        .builder()
                                                        .name("Room Number")
                                                        .mandatory(true)
                                                        .type(InventoryClassAttributeTypeDTO.Number)
                                                        .build()
                                        )
                                )
                                .build()
                )
        );

        String newDomainId = assertDoesNotThrow(
                () -> inventoryElementService.createNew(
                        NewInventoryDomainDTO
                                .builder()
                                .name("New Domain")
                                .description("This is the description for the new domain")
                                .build()
                )
        );

        String newRootElementId = assertDoesNotThrow(
                () -> inventoryElementService.createNew(
                        newDomainId,
                        NewInventoryElementDTO
                                .builder()
                                .name("Building Control 1")
                                .description("Main control system building")
                                .classId(newBuildingClassID)
                                .attributes(
                                        List.of(
                                                InventoryElementAttributeValue
                                                        .builder()
                                                        .name("building-number")
                                                        .value("34")
                                                        .build()
                                        )
                                )
                                .build()
                )
        );

        assertThat(newRootElementId).isNotNull().isNotEmpty();

        String newParentElementId = assertDoesNotThrow(
                () -> inventoryElementService.createNew(
                        newDomainId,
                        NewInventoryElementDTO
                                .builder()
                                .name("Room Control 1")
                                .description("Main control system building")
                                .classId(newRoomClassID)
                                .parentId(newRootElementId)
                                .attributes(
                                        List.of(
                                                InventoryElementAttributeValue
                                                        .builder()
                                                        .name("room-number")
                                                        .value("101")
                                                        .build()
                                        )
                                )
                                .build()
                )
        );

        assertThat(newParentElementId).isNotNull().isNotEmpty();
    }

    @Test
    public void createNewElementWithParentFailWithBadParentOK() {
        String newBuildingClassID = assertDoesNotThrow(
                () -> inventoryClassService.createNew(
                        NewInventoryClassDTO
                                .builder()
                                .name("building class a")
                                .type(InventoryClassTypeDTO.Building)
                                .attributes(
                                        List.of(
                                                InventoryClassAttributeDTO
                                                        .builder()
                                                        .name("Building Number")
                                                        .mandatory(true)
                                                        .type(InventoryClassAttributeTypeDTO.Number)
                                                        .build()
                                        )
                                )
                                .build()
                )
        );

        String newDomainId = assertDoesNotThrow(
                () -> inventoryElementService.createNew(
                        NewInventoryDomainDTO
                                .builder()
                                .name("New Domain")
                                .description("This is the description for the new domain")
                                .build()
                )
        );

        InventoryElementNotFound parentNotFoundException = assertThrows(
                InventoryElementNotFound.class,
                () -> inventoryElementService.createNew(
                        newDomainId,
                        NewInventoryElementDTO
                                .builder()
                                .name("Room Control 1")
                                .description("Main control system building")
                                .classId(newBuildingClassID)
                                .parentId("bad parent id")
                                .build()
                )
        );
        assertThat(parentNotFoundException.getErrorCode()).isEqualTo(-5);
    }

    @Test
    public void errorWithNoFoundTagInDomain() {
        String newBuildingClassID = assertDoesNotThrow(
                () -> inventoryClassService.createNew(
                        NewInventoryClassDTO
                                .builder()
                                .name("building class a")
                                .type(InventoryClassTypeDTO.Building)
                                .attributes(
                                        List.of(
                                                InventoryClassAttributeDTO
                                                        .builder()
                                                        .name("Building Number")
                                                        .mandatory(true)
                                                        .type(InventoryClassAttributeTypeDTO.Number)
                                                        .build()
                                        )
                                )
                                .build()
                )
        );


        String newDomainId = assertDoesNotThrow(
                () -> inventoryElementService.createNew(
                        NewInventoryDomainDTO
                                .builder()
                                .name("New Domain")
                                .description("This is the description for the new domain")
                                .build()
                )
        );

        TagNotFound tagNotFoundError = assertThrows(
                TagNotFound.class,
                () -> inventoryElementService.createNew(
                        newDomainId,
                        NewInventoryElementDTO
                                .builder()
                                .name("Building Control 1")
                                .description("Main control system building")
                                .classId(newBuildingClassID)
                                .tags(
                                        List.of(
                                                "bad tag id"
                                        )
                                )
                                .attributes(
                                        List.of(
                                                InventoryElementAttributeValue
                                                        .builder()
                                                        .name("building-number")
                                                        .value("34")
                                                        .build()
                                        )
                                )
                                .build()
                )
        );

        assertThat(tagNotFoundError.getErrorCode()).isEqualTo(-4);
    }

    @Test
    public void updateElementOk() {
        String newBuildingClassID = assertDoesNotThrow(
                () -> inventoryClassService.createNew(
                        NewInventoryClassDTO
                                .builder()
                                .name("building class a")
                                .type(InventoryClassTypeDTO.Building)
                                .attributes(
                                        List.of(
                                                InventoryClassAttributeDTO
                                                        .builder()
                                                        .name("Building Number")
                                                        .mandatory(true)
                                                        .type(InventoryClassAttributeTypeDTO.Number)
                                                        .build()
                                        )
                                )
                                .build()
                )
        );


        String newDomainId = assertDoesNotThrow(
                () -> inventoryElementService.createNew(
                        NewInventoryDomainDTO
                                .builder()
                                .name("New Domain")
                                .description("This is the description for the new domain")
                                .build()
                )
        );

        assertDoesNotThrow(
                () -> inventoryElementService.update(
                        newDomainId,
                        UpdateDomainDTO
                                .builder()
                                .description("Updated description")
                                .tags(
                                        List.of(
                                                TagDTO
                                                        .builder()
                                                        .name("tag a")
                                                        .build()
                                        )
                                )
                                .build()
                )
        );

        var fullDomain = assertDoesNotThrow(
                () -> inventoryElementService.getFullDomain(newDomainId)
        );
        // check tag tags are presents
        assertThat(fullDomain.tags()).hasSize(1);

        var newElementId = assertDoesNotThrow(
                () -> inventoryElementService.createNew(
                        newDomainId,
                        NewInventoryElementDTO
                                .builder()
                                .name("Building Control 1")
                                .description("Main control system building")
                                .classId(newBuildingClassID)
                                .attributes(
                                        List.of(
                                                InventoryElementAttributeValue
                                                        .builder()
                                                        .name("building-number")
                                                        .value("34")
                                                        .build()
                                        )
                                )
                                .build()
                )
        );

        assertThat(newElementId)
                .isNotNull()
                .isNotEmpty();

        // update the inventory element
        assertDoesNotThrow(
                () -> inventoryElementService.update(
                        newDomainId,
                        newElementId,
                        UpdateInventoryElementDTO
                                .builder()
                                .description("updated description")
                                .attributes(
                                        List.of(
                                                InventoryElementAttributeValue
                                                        .builder()
                                                        .name("building-number")
                                                        .value("43")
                                                        .build()
                                        )
                                )
                                .tags(
                                        List.of(
                                                fullDomain.tags().get(0).id()
                                        )
                                )
                                .build()
                )
        );
        var fullElementRead = assertDoesNotThrow(
                () ->inventoryElementService.getFullElement(
                        newDomainId,
                        newElementId
                )
        );
        assertThat(fullElementRead).isNotNull();
        assertThat(fullElementRead.id()).isEqualTo(newElementId);
        assertThat(fullElementRead.classId()).isEqualTo(newBuildingClassID);
        assertThat(fullElementRead.domainId()).isEqualTo(newDomainId);
        assertThat(fullElementRead.description()).isEqualTo("updated description");
        assertThat(fullElementRead.tags())
                .hasSize(1)
                .extracting(TagDTO::name).contains("tag-a");
    }
}
