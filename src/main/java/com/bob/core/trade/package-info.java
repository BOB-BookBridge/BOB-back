@ApplicationModule(
    type = ApplicationModule.Type.OPEN,
    allowedDependencies = {
        "admin::out-port", "admin::out-port-result",

        "shared", "global"
    }
)
package com.bob.core.trade;

import org.springframework.modulith.ApplicationModule;
