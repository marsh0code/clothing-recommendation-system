package com.example.wardrobeapi.service;

import org.springframework.core.io.Resource;

public interface StorageService {
    void uploadImage(byte[] file, String objectKey, String bucket);

    Resource findByKey(String objectKey, String bucket);

    void deleteByKey(String objectKey, String bucket);
}
