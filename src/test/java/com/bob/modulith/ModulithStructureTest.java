package com.bob.modulith;

import org.junit.jupiter.api.Test;

import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModulithStructureTest {

    private static final ApplicationModules modules = ApplicationModules.of(com.bob.BookBridgeApplication.class);

    @Test
    void 모듈_구조_위반_사항_검증() {
        modules.verify();
    }

    @Test
    void 모듈_구조_문서_생성() {
        modules.forEach(System.out::println);

        new Documenter(modules)
            .writeModulesAsPlantUml()
            .writeIndividualModulesAsPlantUml();
    }
}
