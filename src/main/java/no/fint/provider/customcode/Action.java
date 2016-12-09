package no.fint.provider.customcode;

import java.util.Arrays;
import java.util.List;

public enum Action {
    HEALTH;

    public static List<String> getActions() {
        return Arrays.asList(
                Arrays.stream(Action.class.getEnumConstants()).map(Enum::name).toArray(String[]::new)
        );
    }
}
