@ApplicationModule(
    type = ApplicationModule.Type.OPEN,
    allowedDependencies = {
        "core.trade", "core.member", "core.trade::event", "core.member::event", "core.report::event",

        "admin::member-out-port", "admin::member-out-port-result",
        "admin::report-out-port", "admin::report-out-port-result",
        "admin::post-out-port", "admin::post-out-port-result",

        "shared", "global"

    }
)
package com.bob.core.post;

import org.springframework.modulith.ApplicationModule;
