package com.bob.global.utils.uuid;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

public class UuidV7Generator extends SequenceStyleGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        return UuidUtils.randomV7();
    }
}
