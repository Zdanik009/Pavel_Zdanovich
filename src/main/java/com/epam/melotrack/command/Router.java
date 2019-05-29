package com.epam.melotrack.command;

public class Router {

    public enum Type {
        FORWARD,
        REDIRECT
    }

    private String route;
    private Type type;

    public Router(String route, Type type) {
        this.route = route;
        this.type = type;
    }

    public String getRoute() {
        return route;
    }

    public Type getType() {
        return type;
    }
}
