package org.scouts105bentaya.core.security;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @PostMapping("api/login")
    public void fakeLogin(@RequestBody LoginDto loginDto) {
        throw new IllegalStateException("This method shouldn't be called. It's implemented by Spring Security filters.");
    }
}
