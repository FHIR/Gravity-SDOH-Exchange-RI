package org.hl7.gravity.refimpl.sdohexchange.validation;

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

  private TaskStatus updateStatus;
  private String statusField;
  private String[] requiredFields;
  private String[] nullFields;

  public void initialize(TaskStatusValueMatch constraintAnnotation) {
    this.updateStatus = constraintAnnotation.updateStatus();
    this.statusField = constraintAnnotation.statusField();
    this.requiredFields = constraintAnnotation.requiredFields();
    this.nullFields = constraintAnnotation.nullFields();
  }

  public boolean isValid(Object value, ConstraintValidatorContext context) {
    TaskStatus status = (TaskStatus) new BeanWrapperImpl(value).getPropertyValue(statusField);
    if (status != null && status == updateStatus) {
      for (Object fieldValue : getFields(value, requiredFields)) {
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
      for (Object fieldValue : getFields(value, nullFields)) {
        if (fieldValue != null) {
          return false;
        }
      }
    }
    return true;
  }

  private List<Object> getFields(Object value, String[] fieldNames) {
    return Arrays.stream(fieldNames)
        .map(field -> new BeanWrapperImpl(value).getPropertyValue(field))
        .collect(Collectors.toList());
  }
}
