package com.example.notificationservice.event;

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

    public void setEmail(String email) {
        this.email = email;
    }

    public UserOperation getUserOperation() {
        return userOperation;
    }

    public void setUserOperation(UserOperation userOperation) {
        this.userOperation = userOperation;
    }
}