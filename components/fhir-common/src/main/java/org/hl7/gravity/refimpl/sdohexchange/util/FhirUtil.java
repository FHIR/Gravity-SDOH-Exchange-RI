package org.hl7.gravity.refimpl.sdohexchange.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.hapi.fluentpath.FhirPathR4;
import org.hl7.fhir.r4.model.BaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility class for operations on FHIR resources.
 */
@UtilityClass
public class FhirUtil {

  /**
   * Compose a {@link Reference} object out of a resource Id.
   */
  public Reference toReference(Class<? extends IBaseResource> resourceClass, String id) {
    return new Reference(new IdType(resourceClass.getSimpleName(), id));
  }

  /**
   * Compose a {@link Reference} object out of a resource Id.
   */
  public Reference toReference(Class<? extends IBaseResource> resourceClass, String id, String display) {
    return toReference(resourceClass, id).setDisplay(display);
  }

  /**
   * Covert {@link DateType} object to a {@link LocalDate} taking into account a timezone.
   */
  public LocalDate toLocalDate(DateType dateType) {
    return Optional.ofNullable(dateType)
        .filter(d -> d.getValue() != null)
        .map(d -> d.getValue()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate())
        .orElse(null);
  }

  /**
   * Covert {@link DateTimeType} object to a {@link LocalDateTime} taking into account a timezone.
   */
  public LocalDateTime toLocalDateTime(DateTimeType dateType) {
    return Optional.ofNullable(dateType)
        .filter(d -> d.getValue() != null)
        .map(d -> d.getValue()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime())
        .orElse(null);
  }

  /**
   * Find {@link Coding} by system among a list of {@link CodeableConcept} instances.
   */
  public Coding findCoding(List<CodeableConcept> codes, String system) {
    return findCoding(codes, system, null);
  }

  /**
   * Find {@link Coding} by system and code among a list of {@link CodeableConcept} instances.
   */
  public Coding findCoding(List<CodeableConcept> codes, String system, String code) {
    return codes.stream()
        .map(c -> c.getCoding()
            .stream()
            .filter(coding -> {
              if (Strings.isNullOrEmpty(code)) {
                return system.equals(coding.getSystem());
              } else {
                return system.equals(coding.getSystem()) && code.equals(coding.getCode());
              }
            })
            .findFirst()
            .orElse(null))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  /**
   * Get all resources of specific type from a resource bundle. Resources are retrieved from Bundle.getResource().
   */
  public <T extends IBaseResource> List<T> getFromBundle(Bundle bundle, Class<T> resourceType) {
    return bundle.getEntry()
        .stream()
        .map(Bundle.BundleEntryComponent::getResource)
        .filter(resourceType::isInstance)
        .map(resourceType::cast)
        .collect(Collectors.toList());
  }

  /**
   * Get first resource of specific type from a resource bundle. Resources are retrieved from Bundle.getResource().
   */
  public <T extends IBaseResource> T getFirstFromBundle(Bundle bundle, Class<T> resourceType) {
    return bundle.getEntry()
        .stream()
        .map(Bundle.BundleEntryComponent::getResource)
        .filter(resourceType::isInstance)
        .map(resourceType::cast)
        .findFirst()
        .orElse(null);
  }

  /**
   * Get resource id for a resource of specific type from a resource bundle. Resources are retrieved from
   * Bundle.getResponse(). If multiple resources of the same type are present - a first one will be fetched.
   */
  public IdType getFromResponseBundle(Bundle respBundle, Class<? extends BaseResource> resourceType) {
    return respBundle.getEntry()
        .stream()
        .map(e -> new IdType(e.getResponse()
            .getLocation()))
        .filter(t -> resourceType.getSimpleName()
            .equals(t.getResourceType()))
        .findFirst()
        .orElse(null);
  }

  /**
   * Create a {@link org.hl7.fhir.r4.model.Bundle.BundleEntryComponent} PUT entry for an update as part of a transaction
   * Bundle.
   */
  public Bundle.BundleEntryComponent createPutEntry(Resource resource) {
    Bundle.BundleEntryComponent bundleEntryComponent = new Bundle.BundleEntryComponent().setResource(resource)
        .setFullUrl(resource.getIdElement()
            .getValue());
    bundleEntryComponent.getRequest()
        .setUrl(resource.getIdElement()
            .getValue())
        .setMethod(Bundle.HTTPVerb.PUT);
    return bundleEntryComponent;
  }

  /**
   * Create a {@link org.hl7.fhir.r4.model.Bundle.BundleEntryComponent} POST entry for a create as part of a transaction
   * Bundle. If resource contains an id - a full URL will be set to allow references to this instance within a Bundle.
   */
  public Bundle.BundleEntryComponent createPostEntry(Resource resource) {
    Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
    entry.setResource(resource)
        .getRequest()
        .setUrl(resource.getClass()
            .getSimpleName())
        .setMethod(Bundle.HTTPVerb.POST);
    if (!Strings.isNullOrEmpty(resource.getId())) {
      entry.setFullUrl(resource.getIdElement()
          .getValue());
    }
    return entry;
  }

  /**
   * Create a {@link org.hl7.fhir.r4.model.Bundle.BundleEntryComponent} GET entry for a search as part of a transaction
   * Bundle.
   */
  public Bundle.BundleEntryComponent createGetEntry(String url) {
    Bundle.BundleEntryComponent bundleEntryComponent = new Bundle.BundleEntryComponent();
    bundleEntryComponent.getRequest()
        .setMethod(Bundle.HTTPVerb.GET)
        .setUrl(url);
    return bundleEntryComponent;
  }

  /**
   * Extract all references from all fields for a specified instance. Pass a FHIRContext not to create a new one every
   * time for performance considerations.
   */
  public List<Reference> getAllReferences(FhirContext fhirR4Context, Resource resource) {
    if (!FhirVersionEnum.R4.equals(fhirR4Context.getVersion()
        .getVersion())) {
      throw new IllegalArgumentException("fhirR4Context param must be of a FHIR R4 version.");
    }
    return new FhirPathR4(fhirR4Context).evaluate(resource, "repeat(*).where(type().name = 'Reference')",
        Reference.class);
  }

  /**
   * Extract all references from all fields for a specified instance and filter them by resource types. Pass a
   * FHIRContext not to create a new one every time for performance considerations.
   */
  public Map<String, List<Reference>> getReferences(FhirContext fhirR4Context, Resource resource,
      List<Class<? extends DomainResource>> resourceTypes) {
    List<String> resourceTypeNames = resourceTypes.stream()
        .map(Class::getSimpleName)
        .collect(Collectors.toList());
    return getAllReferences(fhirR4Context, resource).stream()
        .filter(reference -> resourceTypeNames.contains(reference.getReferenceElement()
            .getResourceType()))
        .collect(Collectors.groupingBy(reference -> reference.getReferenceElement()
            .getResourceType()));
  }

  /**
   * Extract all references from all fields for a specified instance and filter them by resource types. Pass a
   * FHIRContext not to create a new one every time for performance considerations.
   */
  public List<Reference> getReferences(FhirContext fhirR4Context, Resource resource,
      Class<? extends IBaseResource> resourceType) {
    return getAllReferences(fhirR4Context, resource).stream()
        .filter(reference -> resourceType.getSimpleName()
            .equals(reference.getReferenceElement()
                .getResourceType()))
        .collect(Collectors.toList());
  }

  /**
   * This method is useful when an original response bundle should be populated with additional includes, which are not
   * supported by a search query.
   *
   * @param fhirContext FHIR Context.
   * @param original    Original response bundle. All includes will be added to it.
   * @param includes    All the includes to be added.
   * @return Merged search set. Count of results is the same as in the original bundle.
   */
  public Bundle mergeBundles(FhirContext fhirContext, Bundle original, Bundle includes) {
    if (original.getType() != Bundle.BundleType.SEARCHSET) {
      throw new IllegalArgumentException("Original bundle is not a search set.");
    }
    if (includes.getType() != Bundle.BundleType.SEARCHSET) {
      throw new IllegalArgumentException("Includes bundle is not a search set.");
    }
    original.getEntry()
        .addAll(includes.getEntry());

    IParser iParser = fhirContext.newJsonParser();
    //Re-parse bundle to resolve all references as included resources (Reference.getResource())
    return iParser.parseResource(Bundle.class, iParser.encodeResourceToString(original));
  }
}
