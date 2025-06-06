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

    public static <T> Response<T> of(T content) {
        return new Response<T>(content,null);
    }

    public static <T> Response<T> of(T content, String message) {
        return new Response<T>(content, message);
    }
    public static <T> Response<T> of(String message) {
        return new Response<T>(null,message);
    }
}
