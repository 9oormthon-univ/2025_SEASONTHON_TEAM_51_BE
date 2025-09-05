package com.mockit.support.session;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevUserSession implements UserSession {
    @Override public Long currentUserId() { return 1L; }
}
