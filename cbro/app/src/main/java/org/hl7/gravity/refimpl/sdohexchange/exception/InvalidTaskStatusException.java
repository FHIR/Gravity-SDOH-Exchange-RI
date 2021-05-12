package org.hl7.gravity.refimpl.sdohexchange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid task status.")
public class InvalidTaskStatusException extends RuntimeException {}
