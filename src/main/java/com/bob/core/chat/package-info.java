@ApplicationModule(
    type = ApplicationModule.Type.OPEN,
    allowedDependencies = {
        "core.trade::event",

        "admin::report-out-port", "admin::report-out-port-result",

        "shared", "global"
    }
)
package com.bob.core.chat;

import org.springframework.modulith.ApplicationModule;
