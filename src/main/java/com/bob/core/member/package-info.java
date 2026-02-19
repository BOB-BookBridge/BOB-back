@ApplicationModule(
    type = ApplicationModule.Type.OPEN,
    allowedDependencies = {
        "admin::member-out-port", "admin::member-out-port-result",
        "admin::report-out-port", "admin::report-out-port-result",
        "admin::post-out-port", "admin::notice-out-port",

        "security::out-port", "security::out-port-result",

        "shared", "global"
    }
)
package com.bob.core.member;

import org.springframework.modulith.ApplicationModule;
