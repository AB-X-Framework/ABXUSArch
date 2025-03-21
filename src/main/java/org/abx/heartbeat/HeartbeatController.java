package org.abx.heartbeat;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/heartbeat")
public class HeartbeatController {
    @Autowired

    @PreAuthorize("permitAll()")
    @RequestMapping("/alive")
    public boolean alive() {
        return true;
    }

    private byte[] cacheRequestBody(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return outputStream.toByteArray();
    }

    @PreAuthorize("permitAll()")
    @RequestMapping("/putit")
    public String putit(HttpServletRequest request) throws Exception{
        String data= new String(cacheRequestBody(request));
        return data;
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
