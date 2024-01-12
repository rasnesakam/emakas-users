package com.emakas.userService.dto;

public class Response <T>{
    public final T content;
    public final String message;

    public Response(T content, String message) {
        this.content = content;
        this.message = message;
    }

    public Response(T content) {
        this(content,null);
    }
}
