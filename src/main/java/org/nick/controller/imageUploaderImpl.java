package org.nick.controller;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.nick.exception.ImageUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class imageUploaderImpl implements ImageUploader{


    @Autowired
    private AmazonS3 client;

    @Value("${app.s3.bucket}")
    private String bucketName;

    @Override
    public String uploadImage(MultipartFile image) {

        if(image == null) {
            throw new ImageUploadException("No file/image passed in request !!");
        }

        String actualFileName = image.getOriginalFilename();

        String fileName = UUID.randomUUID().toString() +actualFileName.substring(actualFileName.lastIndexOf("."));

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getSize());

        try {
            PutObjectResult putObjectResult = client.putObject(new PutObjectRequest(bucketName, fileName, image.getInputStream(), metadata));
            //below line will just return the file name uploaded in aws
            //return fileName;

            //Below line return the url to access and we have set this url valid for 2 hours
            return this.preSignedUrl(fileName);


        } catch (IOException e) {
            throw new ImageUploadException("Error in uploading : "+e.getMessage());
        }
    }

    @Override
    public List<String> allFiles() {

        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request().withBucketName(bucketName);

        ListObjectsV2Result listObjectsV2Result= client.listObjectsV2(listObjectsV2Request);

        List<S3ObjectSummary> objectSummaries = listObjectsV2Result.getObjectSummaries();

        return objectSummaries.stream().map(item -> this.preSignedUrl(item.getKey())).collect(Collectors.toList());
    }

    @Override
    public String preSignedUrl(String filename) {

        Date expirationDate = new Date();
        expirationDate.setTime(expirationDate.getTime()+2*60*60*1000);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, filename)
                .withMethod(HttpMethod.GET)
                        .withExpiration(expirationDate);

        URL url = client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    @Override
    public String getImageUrlByName(String fileName) {

        S3Object object = client.getObject(bucketName,fileName);
        String key = object.getKey();
        return preSignedUrl(key);
    }

}
