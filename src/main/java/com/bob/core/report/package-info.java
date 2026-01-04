@ApplicationModule(
    type = ApplicationModule.Type.OPEN,
    allowedDependencies = {
        "admin::out-port", "admin::out-port-result",

        "shared", "global"
    }
)
package com.bob.core.report;

import org.springframework.modulith.ApplicationModule;
