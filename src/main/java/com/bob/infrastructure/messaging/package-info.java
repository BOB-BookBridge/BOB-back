@ApplicationModule(
    allowedDependencies = {
        "core.notification :: infra",

        "global"
    }
)
package com.bob.infrastructure.messaging;

import org.springframework.modulith.ApplicationModule;
