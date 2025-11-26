package com.bob.global.props;

import lombok.Getter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.bob.global.utils.web.CookieUtils;

@Getter
@ConfigurationProperties(prefix = "cookie")
@Component
public class CookieProps {

    private String sameSite;

    public void setSameSite(String sameSite) {
        this.sameSite = sameSite;
        CookieUtils.setSameSite(sameSite);
    }
}
