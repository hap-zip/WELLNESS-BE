package com.example.wellness.wellnesschat.service;

import java.util.List;

public record GuardrailResult(boolean passed, List<String> violations) {
    public static GuardrailResult ok() {
        return new GuardrailResult(true, List.of());
    }

    public static GuardrailResult fail(List<String> violations) {
        return new GuardrailResult(false, violations);
    }
}
