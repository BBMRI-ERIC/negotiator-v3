package eu.bbmri_eric.negotiator.governance.resource.dto;

import eu.bbmri_eric.negotiator.governance.organization.OrganizationDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.server.core.Relation;

/** A DTO for an abstract resource in the Negotiator. */
@Getter
@Setter
@Relation(collectionRelation = "resources", itemRelation = "resource")
@NoArgsConstructor
public class ResourceResponseModel {
  Long id;
  String sourceId;
  String name;
  String description = "";
  OrganizationDTO organization;

  public ResourceResponseModel(
      Long id, String sourceId, String name, OrganizationDTO organization) {
    this.id = id;
    this.sourceId = sourceId;
    this.name = name;
    this.organization = organization;
  }

  public ResourceResponseModel(Long id, String sourceId, String name, String description) {
    this.id = id;
    this.sourceId = sourceId;
    this.name = name;
    this.description = description;
  }
}
