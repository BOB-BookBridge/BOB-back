@ApplicationModule(
    type = ApplicationModule.Type.OPEN,
    allowedDependencies = {
        "core.member", "core.chat", "core.post",

        "admin::member-out-port", "admin::member-out-port-result",
        "admin::report-out-port", "admin::report-out-port-result",

        "shared", "global"
    }
)
package com.bob.core.report;

import org.springframework.modulith.ApplicationModule;
