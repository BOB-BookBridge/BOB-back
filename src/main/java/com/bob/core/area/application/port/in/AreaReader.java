package com.bob.core.area.application.port.in;

import com.bob.core.area.domain.EmdArea;

public interface AreaReader {

    EmdArea read(Integer emdId);
}
