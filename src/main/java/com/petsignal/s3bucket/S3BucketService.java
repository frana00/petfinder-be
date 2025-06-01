package com.petsignal.s3bucket;

import com.petsignal.exception.S3BucketException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.internal.sync.FileContentStreamProvider;
import software.amazon.awssdk.http.*;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.utils.IoUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3BucketService {
  private static final String UPLOAD_ERROR_MESSAGE = "Could not save file to S3";
  private static final String DOWNLOAD_ERROR_MESSAGE = "Could not retrieve file from S3";

  private final S3Presigner s3Presigner;
  private final S3AsyncClient s3AsyncClient;

  @Value("${aws.s3.bucket-name}")
  private String bucketName;

  public String createPutPresignedUrl(String key, MediaType contentType) {

    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .putObjectRequest(r -> r.bucket(bucketName).key(key).contentType(contentType.toString()).build())
        .build();

    PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
    String myURL = presignedRequest.url().toString();
    log.info("Presigned URL to upload a file to: [{}]", myURL);
    log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

    return presignedRequest.url().toExternalForm();

  }

  public String createGetPresignedUrl(String key, MediaType contentType) {

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(r -> r.bucket(bucketName).key(key).responseContentType(contentType.toString()).build())
        .build();

    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
    String myURL = presignedRequest.url().toString();
    log.info("Presigned URL to get/download a file from: [{}]", myURL);
    log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

    return presignedRequest.url().toExternalForm();

  }

  public void uploadFileWithPresignedUrl(String presignedUrlString, File fileToPut, MediaType contentType) {
    log.info("Begin [{}] upload", fileToPut.toString());

    try {

      SdkHttpRequest.Builder requestBuilder = SdkHttpRequest.builder()
          .method(SdkHttpMethod.PUT)
          .putHeader(CONTENT_TYPE, contentType.toString())
          .uri(URI.create(presignedUrlString));

      // Finish building the request.
      SdkHttpRequest request = requestBuilder.build();

      HttpExecuteRequest executeRequest = HttpExecuteRequest.builder()
          .request(request)
          .contentStreamProvider(new FileContentStreamProvider(fileToPut.toPath()))
          .build();

      try (SdkHttpClient sdkHttpClient = ApacheHttpClient.create()) {
        HttpExecuteResponse response = sdkHttpClient.prepareRequest(executeRequest).call();
        log.info("Http response code: {}", response.httpResponse().statusCode());
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new S3BucketException(UPLOAD_ERROR_MESSAGE, e);
    }
  }

  public byte[] retrieveFileFromS3(String presignedUrlString, MediaType contentType) {

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); // Capture the response body to a byte array.
    try {
      SdkHttpRequest request = SdkHttpRequest.builder()
          .method(SdkHttpMethod.GET)
          .putHeader(CONTENT_TYPE, contentType.toString())
          .uri(URI.create(presignedUrlString))
          .build();

      HttpExecuteRequest executeRequest = HttpExecuteRequest.builder()
          .request(request)
          .build();

      try (SdkHttpClient sdkHttpClient = ApacheHttpClient.create()) {
        HttpExecuteResponse response = sdkHttpClient.prepareRequest(executeRequest).call();
        response.responseBody().ifPresentOrElse(
            abortableInputStream -> {
              try {
                IoUtils.copy(abortableInputStream, byteArrayOutputStream);
              } catch (IOException e) {
                throw new S3BucketException(DOWNLOAD_ERROR_MESSAGE, e);
              }
            },
            () -> log.error("No response body."));

        HttpStatus responseStatus = HttpStatus.valueOf(response.httpResponse().statusCode());
        log.info("HTTP Response code is {}", responseStatus);
        if (responseStatus.isError()) {
          throw new S3BucketException(DOWNLOAD_ERROR_MESSAGE);
        }
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    return byteArrayOutputStream.toByteArray();
  }

  public CompletableFuture<Void> deleteObjectFromBucket(String key) {
    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    return s3AsyncClient.deleteObject(deleteObjectRequest)
        .thenAccept(response -> log.info("{} was deleted", key));
  }
} 