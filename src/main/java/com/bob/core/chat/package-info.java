@ApplicationModule(
    type = ApplicationModule.Type.OPEN,
    allowedDependencies = {
        "core.trade::event",

        "shared", "global"
    }
)
package com.bob.core.chat;

import org.springframework.modulith.ApplicationModule;
