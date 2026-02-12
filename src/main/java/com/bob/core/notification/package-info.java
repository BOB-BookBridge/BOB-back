@ApplicationModule(
    type = ApplicationModule.Type.OPEN,
    allowedDependencies = {
        "core.chat::event", "core.trade::event", "core.inquiry::event", "core.report::event",

        "shared", "global"
    }
)
package com.bob.core.notification;

import org.springframework.modulith.ApplicationModule;
