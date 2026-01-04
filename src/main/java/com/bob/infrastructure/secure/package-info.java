@ApplicationModule(
    allowedDependencies = {
        "security :: infra",

        "core.member :: encoder"
    }
)
package com.bob.infrastructure.secure;

import org.springframework.modulith.ApplicationModule;
