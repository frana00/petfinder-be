package com.petsignal.s3bucket;

import com.petsignal.exception.S3BucketException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.http.*;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3BucketService {
  private static final String UPLOAD_ERROR_MESSAGE = "Could not save file to S3";
  private static final String DOWNLOAD_ERROR_MESSAGE = "Could not retrieve file from S3";

  private final S3Presigner s3Presigner;

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
    log.info("Presigned URL to upload a file to: [{}]", myURL);
    log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

    return presignedRequest.url().toExternalForm();

  }


  public void uploadFileWithPresignedUrl(String presignedUrlString, File fileToPut, MediaType contentType) {
    log.info("Begin [{}] upload", fileToPut.toString());

    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

    try (HttpClient httpClient = HttpClient.newHttpClient()) {
      final HttpResponse<Void> response = httpClient.send(requestBuilder
              .uri(URI.create(presignedUrlString))
              .header(CONTENT_TYPE, contentType.toString())
              .PUT(HttpRequest.BodyPublishers.ofFile(Path.of(fileToPut.toURI())))
              .build(),
          HttpResponse.BodyHandlers.discarding());

      HttpStatus responseStatus = HttpStatus.valueOf(response.statusCode());
      log.info("HTTP response code is {}", responseStatus);

      if (responseStatus.isError()) {
        throw new S3BucketException(UPLOAD_ERROR_MESSAGE);
      }

    } catch (InterruptedException | IOException e) {
      log.error(e.getMessage(), e);
      Thread.currentThread().interrupt();
      throw new S3BucketException(UPLOAD_ERROR_MESSAGE, e);
    }
  }

  public byte[] useSdkHttpClientToGet(String presignedUrlString, MediaType contentType) {

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); // Capture the response body to a byte array.
    try {
      SdkHttpRequest request = SdkHttpRequest.builder()
          .method(SdkHttpMethod.GET)
//          .putHeader("Content-Type", contentType.toString())
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
} 