package com.kamruddin.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.test.ApplicationModuleTest;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest
class ModulithStructureTests {

    ApplicationModules modules = ApplicationModules.of(ModulithApplication.class);

    @Test
    void verifyModulithStructure() {

        // Verify that we have the expected modules
        assertThat(modules.stream().map(module -> module.getIdentifier().toString()))
                .containsExactlyInAnyOrder("inventory", "order");

        modules.verify();

        // Note: The verify() method seems to have issues in this version,
        // but the module structure is correct with proper event-based communication
    }

    @Test
    void writeDocumentation() {
        new Documenter(modules).writeDocumentation();
    }

    @Test
    void createModuleDocumentation() {
        new Documenter(modules)
            .writeModulesAsPlantUml()
            .writeIndividualModulesAsPlantUml();
    }
}