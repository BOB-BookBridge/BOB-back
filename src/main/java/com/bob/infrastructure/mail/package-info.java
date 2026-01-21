@ApplicationModule(
    allowedDependencies = {"core.member :: infra", "core.inquiry :: infra"}
)
package com.bob.infrastructure.mail;

import org.springframework.modulith.ApplicationModule;
