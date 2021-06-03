package tr.com.tolaas.spring.webflux.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import tr.com.tolaas.spring.webflux.demo.service.StdoutStreamingService;

import java.io.IOException;

@RestController
public class StdoutFluxController {

    @Autowired
    private StdoutStreamingService stdoutStreamingService;

    @GetMapping("/")
    public String getHello() {
        return "Hello";
    }

    @GetMapping("stdoutstream")
    public Flux<String> getStdoutStream() {
        try {
            return stdoutStreamingService.getStdoutStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
