package com.boom.platform.system.api;

import com.boom.platform.system.api.dto.PingResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/system")
public class SystemController {

    @GetMapping("/ping")
    public PingResponse ping() {
        return new PingResponse("Boom backend is running", Instant.now());
    }
}
