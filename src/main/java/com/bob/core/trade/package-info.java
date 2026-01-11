@ApplicationModule(
    type = ApplicationModule.Type.OPEN,
    allowedDependencies = {
        "admin::member-out-port", "admin::member-out-port-result",

        "shared", "global"
    }
)
package com.bob.core.trade;

import org.springframework.modulith.ApplicationModule;
