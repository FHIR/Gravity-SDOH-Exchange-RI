package org.hl7.gravity.refimpl.sdohexchange.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.TaskStatus;
import org.hl7.gravity.refimpl.sdohexchange.validation.TaskStatusValueMatchValidator;

@Constraint(validatedBy = TaskStatusValueMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskStatusValueMatch {

  String message() default "Task status requires additional fields to be set!";

  TaskStatus updateStatus();

  String statusField() default "status";

  String[] requiredFields() default {};

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @Target({ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @interface List {

    TaskStatusValueMatch[] value();
  }
}