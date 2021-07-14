package org.hl7.gravity.refimpl.sdohexchange.exception;

/**
 * Exception represent an error occurred during prepare for Health Concern creation.
 */
public class PrepareBundleException extends RuntimeException {

  public PrepareBundleException(String message) {
    super(message);
  }

  public PrepareBundleException(String message, Throwable cause) {
    super(message, cause);
  }
}
