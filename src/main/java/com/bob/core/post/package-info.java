@ApplicationModule(
    type = ApplicationModule.Type.OPEN,
    allowedDependencies = {
        "core.trade", "core.trade::event", "core.member::event",

        "admin::member-out-port", "admin::member-out-port-result",
        "admin::report-out-port", "admin::report-out-port-result",

        "shared", "global"

    }
)
package com.bob.core.post;

import org.springframework.modulith.ApplicationModule;
