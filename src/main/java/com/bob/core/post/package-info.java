@ApplicationModule(
    type = ApplicationModule.Type.OPEN,
    allowedDependencies = {
        "admin :: out-port", "admin :: out-port-result",

        "core.trade", "core.trade :: event", "core.member :: event",

        "shared", "global"
    }
)
package com.bob.core.post;

import org.springframework.modulith.ApplicationModule;
