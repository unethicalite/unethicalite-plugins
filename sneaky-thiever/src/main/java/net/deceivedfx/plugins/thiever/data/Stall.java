package net.deceivedfx.plugins.thiever.data;

import lombok.Getter;

@Getter
public enum Stall {
    TEA("Tea stall"),
    CAKE("Baker's stall"),
    FRUIT("Fruit Stall");

    private final String name;

    Stall(String name) {
        this.name = name;
    }
}
