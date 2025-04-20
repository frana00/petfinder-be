package com.petsignal.exception;

public class S3BucketException extends RuntimeException {

  public S3BucketException(String message, Throwable cause) {
    super(message, cause);
  }

  public S3BucketException(String message) {
    super(message);
  }
} 