package com.ceos21.spring_boot.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3UploadService{
    List<String> saveFile(List<MultipartFile> multipartFiles);
    String saveSingleFile(MultipartFile multipartFile);
}
