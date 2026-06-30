package com.boom.parentdashboard.application;

public enum DashboardLocale {
    EN_US("en-US"),
    PT_BR("pt-BR"),
    IT_IT("it-IT"),
    ES_ES("es-ES");

    private final String code;

    DashboardLocale(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static DashboardLocale resolve(String boomLocale, String acceptLanguage) {
        DashboardLocale explicitLocale = fromHeaderValue(boomLocale);

        if (explicitLocale != null) {
            return explicitLocale;
        }

        DashboardLocale languageHeaderLocale = fromHeaderValue(acceptLanguage);

        if (languageHeaderLocale != null) {
            return languageHeaderLocale;
        }

        return EN_US;
    }

    private static DashboardLocale fromHeaderValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim();

        // Accept-Language may include values such as "pt-BR,pt;q=0.9,en;q=0.8".
        if (normalized.contains(",")) {
            normalized = normalized.split(",")[0].trim();
        }

        if (normalized.contains(";")) {
            normalized = normalized.split(";")[0].trim();
        }

        return switch (normalized) {
            case "en-US", "en", "en_US" -> EN_US;
            case "pt-BR", "pt", "pt_BR" -> PT_BR;
            case "it-IT", "it", "it_IT" -> IT_IT;
            case "es-ES", "es", "es_ES" -> ES_ES;
            default -> null;
        };
    }
}
