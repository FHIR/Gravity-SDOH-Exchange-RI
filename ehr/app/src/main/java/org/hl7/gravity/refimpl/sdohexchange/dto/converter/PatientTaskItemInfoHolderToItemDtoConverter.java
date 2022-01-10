package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import com.google.common.collect.Lists;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskOutputComponent;
import org.hl7.fhir.r4.model.Type;
import org.hl7.gravity.refimpl.sdohexchange.codes.PatientTaskCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.SDCTemporaryCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.SDOHTemporaryCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.patienttask.PatientTaskType;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CodingDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ReferenceDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.patienttask.PatientTaskItemDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientTaskItemInfoBundleExtractor.PatientTaskItemInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

public class PatientTaskItemInfoHolderToItemDtoConverter<S extends PatientTaskItemInfoHolder,
    T extends PatientTaskItemDto>
    implements Converter<S, T> {

  @Override
  public T convert(S taskInfoHolder) {
    Task task = taskInfoHolder.getTask();
    Questionnaire questionnaire = taskInfoHolder.getQuestionnaire();
    PatientTaskItemDto taskDto = createDto();
    taskDto.setId(task.getIdElement()
        .getIdPart());
    taskDto.setName(task.getDescription());
    taskDto.setPriority(task.getPriority()
        .getDisplay());
    taskDto.setLastModified(FhirUtil.toLocalDateTime(task.getLastModifiedElement()));
    taskDto.setStatus(task.getStatus()
        .getDisplay());
    taskDto.setStatusReason(task.getStatusReason()
        .getText());
    setReferralTask(task, taskDto);

    List<Coding> codings = task.getCode()
        .getCoding();
    PatientTaskCode code = getCode(taskDto, codings);
    if (code != null) {
      Coding coding = code.toCoding();
      taskDto.setCode(new CodingDto(coding.getCode(), coding.getDisplay()));
    }

    setTaskType(task, taskDto, code);
    if (questionnaire != null) {
      taskDto.setAssessment(new ReferenceDto(questionnaire.getIdElement()
          .getIdPart(), questionnaire.getTitle()));
    }

    for (TaskOutputComponent outputComponent : task.getOutput()) {
      Type componentValue = outputComponent.getValue();
      Coding coding = FhirUtil.findCoding(Lists.newArrayList(outputComponent.getType()), SDCTemporaryCode.SYSTEM,
          SDCTemporaryCode.QUESTIONNAIRE_RESPONSE.getCode());
      if (coding != null) {
        Reference qrRef = (Reference) componentValue;
        if (QuestionnaireResponse.class.getSimpleName()
            .equals(qrRef.getReferenceElement()
                .getResourceType())) {
          taskDto.setAssessmentResponse(new ReferenceDto(qrRef.getReferenceElement()
              .getIdPart(), coding.getDisplay()));
        }
      }
      if (componentValue instanceof CodeableConcept) {
        CodeableConcept outcome = (CodeableConcept) componentValue;
        taskDto.setOutcome(outcome.getText());
      }
    }
    return (T) taskDto;
  }

  protected PatientTaskItemDto createDto() {
    return new PatientTaskItemDto();
  }

  private void setTaskType(Task task, PatientTaskItemDto taskDto, PatientTaskCode code) {
    if (PatientTaskCode.MAKE_CONTACT.equals(code)) {
      taskDto.setType(PatientTaskType.MAKE_CONTACT);
    } else if (PatientTaskCode.COMPLETE_QUESTIONNAIRE.equals(code)) {
      Task.ParameterComponent comp = task.getInput()
          .stream()
          .filter(i -> SDOHTemporaryCode.QUESTIONNAIRE_CATEGORY.getCode()
              .equals(i.getType()
                  .getCodingFirstRep()
                  .getCode()))
          .findAny()
          .orElse(null);
      if (comp != null && comp.getValue() instanceof CodeableConcept) {
        CodeableConcept cc = (CodeableConcept) comp.getValue();
        if (SDOHTemporaryCode.RISK_QUESTIONNAIRE.getCode()
            .equals(cc.getCodingFirstRep()
                .getCode())) {
          taskDto.setType(PatientTaskType.COMPLETE_SR_QUESTIONNAIRE);
        } else if (SDOHTemporaryCode.FEEDBACK_QUESTIONNAIRE.getCode()
            .equals(cc.getCodingFirstRep()
                .getCode())) {
          taskDto.setType(PatientTaskType.SERVICE_FEEDBACK);
        }
      }
    }
  }

  private void setReferralTask(Task task, PatientTaskItemDto taskDto) {
    if (task.getPartOf()
        .size() > 0) {
      Reference ref = task.getPartOf()
          .stream()
          .filter(r -> Task.class.getSimpleName()
              .equals(r.getReferenceElement()
                  .getResourceType()))
          .findFirst()
          .orElse(null);
      //TODO add validation for multiple references
      if (ref.getResource() == null) {
        taskDto.getErrors()
            .add(String.format("No based-on Task resource %s was returned for Task %s.", ref.getReferenceElement()
                .getIdPart(), taskDto.getId()));
      } else {
        taskDto.setReferralTask(new ReferenceDto(ref.getReferenceElement()
            .getIdPart(), ((Task) ref.getResource()).getDescription()));
      }
    }
  }

  private PatientTaskCode getCode(PatientTaskItemDto taskDto, List<Coding> codings) {
    PatientTaskCode code = null;
    if (codings.size() == 0) {
      taskDto.getErrors()
          .add(String.format("No any code is present for Task with id %s.", taskDto.getId()));
    } else {
      if (codings.size() > 1) {
        taskDto.getErrors()
            .add(String.format("Multiple codings are present for task code with id %s. The first one will" + " be "
                + "used. Other ones will be ignored!", taskDto.getId()));
      }
      Coding coding = codings.get(0);
      try {
        code = PatientTaskCode.fromCode(coding.getCode());
      } catch (FHIRException ex) {
        taskDto.getErrors()
            .add(String.format("Code %s cannot be converted to PatientTaskCode.", coding.getCode(), taskDto.getId()));
      }
    }
    return code;
  }
}
