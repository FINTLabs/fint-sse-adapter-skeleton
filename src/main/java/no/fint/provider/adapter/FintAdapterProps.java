package no.fint.provider.adapter;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class FintAdapterProps {

    @Value("${fint.adapter.organizations}")
    private String[] organizations;

    @Value("${fint.adapter.base-url}")
    private String baseUrl;

    public String getSseEndpoint() {
        return String.format("%s/sse/%%s", baseUrl);
    }

    public String getResponseEndpoint() {
        return String.format("%s/response", baseUrl);
    }

    public String getStatusEndpoint() {
        return String.format("%s/status", baseUrl);
    }

}
