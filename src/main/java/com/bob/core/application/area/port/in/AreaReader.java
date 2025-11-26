package com.bob.core.application.area.port.in;

import com.bob.core.domain.area.EmdArea;

public interface AreaReader {

    EmdArea read(Integer emdId);
}
