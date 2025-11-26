package com.bob.integration.adapter.post;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.area.port.in.AreaReader;
import com.bob.core.application.post.port.out.PostAreaPort;
import com.bob.core.application.post.port.result.PostArea;
import com.bob.core.domain.area.EmdArea;
import com.bob.core.domain.area.SiggArea;

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
