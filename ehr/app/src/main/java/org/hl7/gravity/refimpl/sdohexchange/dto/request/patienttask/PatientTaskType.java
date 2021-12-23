package org.hl7.gravity.refimpl.sdohexchange.dto.request.patienttask;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.gravity.refimpl.sdohexchange.codes.PatientTaskCode;

import java.util.List;

@AllArgsConstructor
public enum PatientTaskType {
  MAKE_CONTACT(Lists.newArrayList(PatientTaskCode.MAKE_CONTACT.toCoding())),
  COMPLETE_SR_QUESTIONNAIRE(Lists.newArrayList(PatientTaskCode.COMPLETE_QUESTIONNAIRE.toCoding())),
  SERVICE_FEEDBACK(Lists.newArrayList(PatientTaskCode.COMPLETE_QUESTIONNAIRE.toCoding()));
  @Getter
  private List<Coding> codes;
}