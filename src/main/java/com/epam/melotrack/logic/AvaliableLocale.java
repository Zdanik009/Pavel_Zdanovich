package com.epam.melotrack.logic;

public enum AvaliableLocale {

    EN_US, BE_BY, RU_BY;

    public static boolean contains(String language) {
        AvaliableLocale avaliableLanguage = AvaliableLocale.valueOf(language);
        return avaliableLanguage != null;
    }

    @Override
    public String toString() {
        return this.name().substring(0, 2).toLowerCase() + this.name().substring(2);
    }

}
