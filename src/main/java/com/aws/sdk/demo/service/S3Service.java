package com.aws.sdk.demo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class S3Service {
    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    private final AmazonS3 s3Client;
    @Autowired
    public S3Service(AmazonS3 s3Client) {
        this.s3Client=s3Client;
    }

    public List<String> listObjects() {
        ObjectListing objectListing=s3Client.listObjects(bucketName);
        return objectListing.getObjectSummaries()
                .stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toList());
    }
    public String uploadFile(MultipartFile file) {
        File fileObj=convertMultipartFileToFile(file);
        String fileName=file.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(bucketName,fileName,fileObj));
        fileObj.delete();
        return "File Uploaded : " + fileName;
    }

    private File convertMultipartFileToFile(MultipartFile file) {
        File convertedFile=new File(file.getOriginalFilename());
        try (FileOutputStream fos=new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        catch (IOException e) {
            log.error("Cannot convert multipart file to file",e);
        }
        return convertedFile;
    }

    public byte[] downloadFile(String fileName) {
        S3Object s3Object=s3Client.getObject(bucketName,fileName);
        S3ObjectInputStream inputStream=s3Object.getObjectContent();
        try {
            byte[] content= IOUtils.toByteArray(inputStream);
            return content;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String deleteFile(String fileName) {
        s3Client.deleteObject(bucketName,fileName);
        return fileName + "deleted...";
    }
}
