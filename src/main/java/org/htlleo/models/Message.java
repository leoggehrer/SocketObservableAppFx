package org.htlleo.models;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private UUID id;
    private String command;
    private String from;
    private String body;

    public UUID getId() {
        return  id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", command='" + command + '\'' +
                ", from='" + from + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
