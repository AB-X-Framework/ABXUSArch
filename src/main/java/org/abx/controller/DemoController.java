package org.abx.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @Autowired

    @PreAuthorize("permitAll()")
    @RequestMapping("/demo")
    public String demo() {
        return "demo";
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
