@ApplicationModule(
    type = ApplicationModule.Type.OPEN,
    allowedDependencies = {
        "admin::member-out-port", "admin::member-out-port-result",

        "core.trade", "core.trade::event", "core.member::event",

        "shared", "global"
    }
)
package com.bob.core.post;

import org.springframework.modulith.ApplicationModule;
