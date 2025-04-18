package com.petsignal.s3bucket;

import com.petsignal.exception.S3BucketException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3BucketService {

//    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;
//
//    public String uploadPhoto(MultipartFile file) {
//
//        String fileName = file.getOriginalFilename();
//        String fileExtension = Objects.requireNonNull(fileName).substring(fileName.lastIndexOf("."));
//        String objectId = UUID.randomUUID().toString();
//
//        try {
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(objectId + fileExtension)
//                    .contentType(file.getContentType())
//                    .build();
//
//            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
//
//            return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName)).toString();
//        } catch (IOException e) {
//            log.error("Error uploading file to S3", e);
//            throw new S3BucketException("Failed to upload file to S3: " + e.getMessage(), e);
//        }
//    }

    public String createPresignedUrl(String key, String contentType) {

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(r -> r.bucket(bucketName).key(key).contentType(contentType).build())
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        String myURL = presignedRequest.url().toString();
        log.info("Presigned URL to upload a file to: [{}]", myURL);
        log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

        return presignedRequest.url().toExternalForm();

    }


    public void useHttpClientToPut(String presignedUrlString, File fileToPut, Map<String, String> metadata) {
        log.info("Begin [{}] upload", fileToPut.toString());

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        metadata.forEach((k, v) -> requestBuilder.header("x-amz-meta-" + k, v));

        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            final HttpResponse<Void> response = httpClient.send(requestBuilder
                            .uri(URI.create(presignedUrlString))
                            .PUT(HttpRequest.BodyPublishers.ofFile(Path.of(fileToPut.toURI())))
                            .build(),
                    HttpResponse.BodyHandlers.discarding());

            log.info("HTTP response code is {}", response.statusCode());

        } catch (InterruptedException | IOException e) {
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new S3BucketException("Could not save photo to S3", e);
        }
    }
} 