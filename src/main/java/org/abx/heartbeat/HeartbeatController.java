package org.abx.heartbeat;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/heartbeat")
public class HeartbeatController {
    @Autowired

    @PreAuthorize("permitAll()")
    @RequestMapping("/alive")
    public boolean alive() {
        return true;
    }


    @PreAuthorize("permitAll()")
    @RequestMapping("/user")
    public String user(HttpServletRequest request) {
        return request.getUserPrincipal().getName();
    }

    @Secured("admin")
    @RequestMapping("/admin")
    public boolean admin() {
        return true;
    }
}
