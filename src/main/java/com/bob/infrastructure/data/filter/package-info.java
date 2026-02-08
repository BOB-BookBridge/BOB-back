@ApplicationModule(
    allowedDependencies = {
        "admin :: filter-dto", "admin :: filter-out-port",

        "core.post :: infra",

        "shared"
    }
)
package com.bob.infrastructure.data.filter;

import org.springframework.modulith.ApplicationModule;
