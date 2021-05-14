package org.hl7.gravity.refimpl.sdohexchange.annotation;

import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.TaskStatus;
import org.hl7.gravity.refimpl.sdohexchange.validation.TaskStatusValueMatchValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TaskStatusValueMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskStatusValueMatch {

  String message() default "Task status requires additional fields to be set!";

  Task.TaskStatus updateStatus();

  String statusField();

  String[] requiredFields();

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @Target({ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @interface List {

    TaskStatusValueMatch[] value();
  }
}