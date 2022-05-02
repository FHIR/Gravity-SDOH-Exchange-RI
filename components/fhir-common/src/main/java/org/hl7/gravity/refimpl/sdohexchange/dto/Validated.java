package org.hl7.gravity.refimpl.sdohexchange.dto;

import org.hl7.fhir.exceptions.FHIRException;

import java.util.List;

/**
 * Interface for DTOs which hold additional validation information required for troubleshooting purposes. Corresponding
 * converters should take this into account and set these errors when needed. <br/> For example, when a Task is
 * returned, but underlying FHIR resource does not belong to profile "http://hl7
 * .org/fhir/us/sdoh-clinicalcare/StructureDefinition/SDOHCC-Task-Base-1"
 * - an error entry should be added to help user generate proper Tasks in future or update the current one.
 */
public interface Validated {

  /**
   * List of user friendly error descriptions.
   */
  List<String> getErrors();

  /**
   * Execute runnable (usually an operation on the same object) and, if an exception is thrown, add an exception message
   * to the errors list.
   *
   * @param dto      current dto.
   * @param runnable code to execute.
   */
  static void withError(Validated dto, Runnable runnable) {
    try {
      runnable.run();
    } catch (FHIRException exc) {
      dto.getErrors()
          .add(exc.getMessage());
    }
  }
}
