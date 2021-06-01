package org.hl7.gravity.refimpl.sdohexchange.validation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hl7.gravity.refimpl.sdohexchange.annotation.TaskStatusValueMatch;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.TaskStatus;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

public class TaskStatusValueMatchValidator implements ConstraintValidator<TaskStatusValueMatch, Object> {

  private TaskStatus updateStatus;
  private String statusField;
  private String[] requiredFields;

  public void initialize(TaskStatusValueMatch constraintAnnotation) {
    this.updateStatus = constraintAnnotation.updateStatus();
    this.statusField = constraintAnnotation.statusField();
    this.requiredFields = constraintAnnotation.requiredFields();
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