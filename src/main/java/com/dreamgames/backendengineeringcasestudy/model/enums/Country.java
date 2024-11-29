package com.dreamgames.backendengineeringcasestudy.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Random;

@Getter
public enum Country {

    TURKEY("Turkey"),
    US("United States"),
    UK("United Kingdom"),
    FRANCE("France"),
    GERMANY("Germany");

    private final String value;

    Country(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Country fromValue(String text) {
        for (Country c : Country.values()) {
            if (String.valueOf(c.value).equals(text)) {
                return c;
            }
        }
        return null;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }

    /**
     * Returns a random Country enum value.
     */
    public static Country getRandomCountry() {
        Country[] countries = Country.values();
        Random random = new Random();
        int randomIndex = random.nextInt(countries.length);
        return countries[randomIndex];
    }
}
