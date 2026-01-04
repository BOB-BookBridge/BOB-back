@ApplicationModule(
    allowedDependencies = {
        "security :: infra",

        "core.member :: infra"
    }
)
package com.bob.infrastructure.cache;

import org.springframework.modulith.ApplicationModule;
