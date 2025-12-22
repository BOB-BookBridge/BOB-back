package com.bob.integration.adapter.post;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.area.application.port.in.AreaReader;
import com.bob.core.area.domain.EmdArea;
import com.bob.core.area.domain.SiggArea;
import com.bob.core.post.application.port.out.PostAreaPort;
import com.bob.core.post.application.port.result.PostArea;

@Component
@RequiredArgsConstructor
public class PostAreaAdapter implements PostAreaPort {

    private final AreaReader areaReader;

    @Override
    public PostArea read(int emdId) {
        EmdArea emdArea = areaReader.read(emdId);
        SiggArea siggArea = emdArea.getSiggArea();
        return PostArea.of(emdArea.getId(), emdArea.getName(), siggArea.getName());
    }
}
