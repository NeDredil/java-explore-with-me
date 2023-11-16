package ru.practicum;

import java.time.format.DateTimeFormatter;

public class Constant {

    private Constant() {
    }

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final String NOT_FOUND_USER = "User with id=%d not found.";
    public static final String NOT_FOUND_CATEGORY = "Category with id=%d was not found.";
    public static final String NOT_FOUND_EVENT = "Event with id=%d was not found.";
    public static final String NOT_FOUND_REQUEST = "Participation request with id=%d was not found.";
    public static final String NOT_FOUND_COMPILATION = "Compilation with id=%d was not found";

    public static final String ALREADY_EXIST_USER = "User with name: %S already exist.";
    public static final String ALREADY_EXIST_CATEGORY = "Category with the name %S already exists";

    public static final String USER_NOT_INITIATOR = "The user with the id=%d is not the initiator of the event with the id =%d";
    public static final String EVENT_DATE_ERROR = "Field: eventDate. Error: must contain a date no earlier than 2 hours " +
            "from the date the event was created. Value: %S";

    public static final String SERVICE_NAME = "ewm-main-service";

}
