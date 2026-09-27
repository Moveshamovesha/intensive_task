package com.example.event;

public class UserEvent {
    private String email;
    private UserOperation userOperation;

    public UserEvent() {
    }

    public UserEvent(String email, UserOperation userOperation) {
        this.email = email;
        this.userOperation = userOperation;
    }

    public String getEmail() {
        return email;
    }

    public UserOperation getUserOperation() {
        return userOperation;
    }
}
