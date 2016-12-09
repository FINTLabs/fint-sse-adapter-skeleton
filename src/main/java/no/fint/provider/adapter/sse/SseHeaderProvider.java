package no.fint.provider.adapter.sse;

import java.util.Map;

@FunctionalInterface
public interface SseHeaderProvider {
    Map<String, String> getHeaders();
}