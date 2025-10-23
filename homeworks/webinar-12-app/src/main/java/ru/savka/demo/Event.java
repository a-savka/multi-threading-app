package ru.savka.demo;

public class Event {
    private final int id;
    private final String message;

    public Event(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Событие{" +
                "id=" + id +
                ", сообщение='" + message + "'" +
                "}";
    }
}
