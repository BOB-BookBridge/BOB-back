@ApplicationModule(
    type = ApplicationModule.Type.OPEN,
    allowedDependencies = {
        "admin::out-port", "admin::out-port-result",

        "security::out-port", "security::out-port-result",

        "shared", "global"
    }
)
package com.bob.core.member;

import org.springframework.modulith.ApplicationModule;
