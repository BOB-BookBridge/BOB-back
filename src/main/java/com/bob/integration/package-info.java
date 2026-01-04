@ApplicationModule(
    allowedDependencies = {
        "core.book", "core.category", "core.interest",

        "core.area", "core.member", "core.post", "core.trade", "core.chat", "core.bookcase",

        "core.file", "core.notification",

        "shared", "global"
    }
)
package com.bob.integration;

import org.springframework.modulith.ApplicationModule;
