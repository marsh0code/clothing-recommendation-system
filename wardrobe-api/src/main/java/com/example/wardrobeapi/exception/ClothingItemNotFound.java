package com.example.wardrobeapi.exception;

public class ClothingItemNotFound extends RuntimeException{
    public ClothingItemNotFound(String username) {
        super(username);
    }
}
