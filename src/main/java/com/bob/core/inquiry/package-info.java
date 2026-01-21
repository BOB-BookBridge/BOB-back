@ApplicationModule(
    type = ApplicationModule.Type.OPEN,
    allowedDependencies = {
        "core.member",

        "admin::inquiry-out-port", "admin::inquiry-out-port-result",

        "shared", "global"
    }
)
package com.bob.core.inquiry;

import org.springframework.modulith.ApplicationModule;
