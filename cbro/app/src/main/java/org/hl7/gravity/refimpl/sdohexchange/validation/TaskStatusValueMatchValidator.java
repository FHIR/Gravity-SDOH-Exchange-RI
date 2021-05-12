package org.hl7.gravity.refimpl.sdohexchange.validation;

import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.annotation.TaskStatusValueMatch;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.TaskStatus;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TaskStatusValueMatchValidator implements ConstraintValidator<TaskStatusValueMatch, Object> {

  private Task.TaskStatus  taskStatus;
  private String statusField;
  private String[] requiredFields;

  public void initialize(TaskStatusValueMatch constraintAnnotation) {
    this.taskStatus = constraintAnnotation.updateStatus();
    this.statusField = constraintAnnotation.statusField();
    this.requiredFields = constraintAnnotation.requiredFields();
  }

  public boolean isValid(Object value, ConstraintValidatorContext context) {
    Task.TaskStatus statusValue = (Task.TaskStatus) new BeanWrapperImpl(value).getPropertyValue(statusField);
    if (statusValue == taskStatus) {
      List<Object> requiredFieldsValues = Arrays.stream(requiredFields)
          .map(field -> new BeanWrapperImpl(value).getPropertyValue(field))
          .collect(Collectors.toList());
      for (Object fieldValue : requiredFieldsValues) {
        if (fieldValue == null) {
          return false;
        }
        if (fieldValue instanceof String && StringUtils.isEmpty(fieldValue)) {
          return false;
        }
        if (fieldValue instanceof List && ((List<?>) fieldValue).isEmpty()) {
          return false;
        }
      }
    }
    return true;
  }
}