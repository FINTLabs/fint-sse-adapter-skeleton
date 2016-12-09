package no.fint.provider.adapter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class VersionService {

    @Value("${springfox.version}")
    private String version;

    @PostConstruct
    public void init() {
        log.info("Greetings from FINT!");
        log.info("Running version: {}", version);
    }
}
