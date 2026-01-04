@ApplicationModule(
    type = ApplicationModule.Type.OPEN,
    allowedDependencies = {
        "core.chat::event", "core.trade::event",

        "shared", "global"
    }
)
package com.bob.core.notification;

import org.springframework.modulith.ApplicationModule;
