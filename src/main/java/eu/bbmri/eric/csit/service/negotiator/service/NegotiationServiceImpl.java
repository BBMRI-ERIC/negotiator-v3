package eu.bbmri.eric.csit.service.negotiator.service;

import eu.bbmri.eric.csit.service.negotiator.configuration.auth.NegotiatorUserDetailsService;
import eu.bbmri.eric.csit.service.negotiator.configuration.state_machine.negotiation.NegotiationState;
import eu.bbmri.eric.csit.service.negotiator.database.model.*;
import eu.bbmri.eric.csit.service.negotiator.database.repository.AttachmentRepository;
import eu.bbmri.eric.csit.service.negotiator.database.repository.NegotiationRepository;
import eu.bbmri.eric.csit.service.negotiator.database.repository.PersonRepository;
import eu.bbmri.eric.csit.service.negotiator.database.repository.RequestRepository;
import eu.bbmri.eric.csit.service.negotiator.database.repository.RoleRepository;
import eu.bbmri.eric.csit.service.negotiator.dto.attachments.AttachmentMetadataDTO;
import eu.bbmri.eric.csit.service.negotiator.dto.negotiation.NegotiationCreateDTO;
import eu.bbmri.eric.csit.service.negotiator.dto.negotiation.NegotiationDTO;
import eu.bbmri.eric.csit.service.negotiator.exceptions.EntityNotFoundException;
import eu.bbmri.eric.csit.service.negotiator.exceptions.EntityNotStorableException;
import eu.bbmri.eric.csit.service.negotiator.exceptions.WrongRequestException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.NotImplementedException;
import org.hibernate.exception.DataException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "DefaultNegotiationService")
@CommonsLog
@Transactional
public class NegotiationServiceImpl implements NegotiationService {

  @Autowired NegotiationRepository negotiationRepository;
  @Autowired RoleRepository roleRepository;
  @Autowired PersonRepository personRepository;
  @Autowired RequestRepository requestRepository;
  @Autowired AttachmentRepository attachmentRepository;
  @Autowired ModelMapper modelMapper;
  @Autowired NotificationService notificationService;

  public static boolean isNegotiationCreator(Negotiation negotiation) {
    return negotiation.isCreator(
        NegotiatorUserDetailsService.getCurrentlyAuthenticatedUserInternalId());
  }

  public static boolean isAuthorizedForNegotiation(Negotiation negotiation) {
    return isNegotiationCreator(negotiation)
        || NegotiatorUserDetailsService.isRepresentativeAny(
            negotiation.getResources().stream()
                .map(Resource::getSourceId)
                .collect(Collectors.toList()));
  }

  private List<Request> findRequests(Set<String> requestsId) {
    List<Request> entities = requestRepository.findAllById(requestsId);
    if (entities.size() < requestsId.size()) {
      throw new WrongRequestException("One or more of the specified requests do not exist");
    }
    return entities;
  }

  private List<Attachment> findAttachments(Set<AttachmentMetadataDTO> attachmentDTOs) {
    List<Attachment> entities =
        attachmentRepository.findAllById(
            attachmentDTOs.stream().map(AttachmentMetadataDTO::getId).collect(Collectors.toList()));
    if (entities.size() < attachmentDTOs.size()) {
      throw new WrongRequestException("One or more of the specified attachments do not exist");
    }
    return entities;
  }

  @Override
  public boolean exists(String negotiationId) {
    try {
      findEntityById(negotiationId, false);
      return true;
    } catch (EntityNotFoundException ex) {
      return false;
    }
  }

  private void addPersonToNegotiation(
      Person person, Negotiation negotiationEntity, String roleName) {
    Role role = roleRepository.findByName(roleName).orElseThrow(EntityNotStorableException::new);
    // Creates the association between the Person and the Negotiation
    PersonNegotiationRole personRole = new PersonNegotiationRole(person, negotiationEntity, role);
    // Updates person and negotiation with the person role
    person.getRoles().add(personRole);
    negotiationEntity.getPersons().add(personRole);
  }

  /**
   * Creates a Negotiation into the repository.
   *
   * @param negotiationBody the NegotiationCreateDTO DTO sent from to the endpoint
   * @param creatorId the ID of the Person that creates the Negotiation (i.e., the authenticated
   *     Person that called the API)
   * @return the created Negotiation entity
   */
  public NegotiationDTO create(NegotiationCreateDTO negotiationBody, Long creatorId) {
    Negotiation negotiationEntity = modelMapper.map(negotiationBody, Negotiation.class);
    // Gets the Entities for the requests
    log.debug("Getting request entities");
    List<Request> requests = findRequests(negotiationBody.getRequests());

    // Check if any negotiationBody is already associated to a negotiation
    if (requests.stream().anyMatch(request -> request.getNegotiation() != null)) {
      log.error("One or more negotiationBody object is already assigned to another negotiation");
      throw new WrongRequestException(
          "One or more negotiationBody object is already assigned to another negotiation");
    }

    Person creator =
        personRepository.findById(creatorId).orElseThrow(EntityNotStorableException::new);
    addPersonToNegotiation(creator, negotiationEntity, "ROLE_RESEARCHER");

    // Updates the bidirectional relationship between negotiation and requests
    negotiationEntity.setRequests(new HashSet<>(requests));
    requests.forEach(
        request -> {
          request.setNegotiation(negotiationEntity);
        });

    Negotiation savedNegotiation;
    try {
      // Finally, save the negotiation. NB: it also cascades operations for other Requests,
      // PersonNegotiationRole
      savedNegotiation = negotiationRepository.save(negotiationEntity);

    } catch (DataException | DataIntegrityViolationException ex) {
      log.error("Error while saving the Negotiation into db. Some db constraint violated");
      log.error(ex);
      throw new EntityNotStorableException();
    }
    if (negotiationBody.getAttachments() != null) {
      List<Attachment> attachments = findAttachments(negotiationBody.getAttachments());
      negotiationEntity.setAttachments(new HashSet<>(attachments));
      attachments.forEach(
          attachment -> {
            attachment.setNegotiation(negotiationEntity);
          });
    }
    // TODO: Add call to send email.
    return modelMapper.map(savedNegotiation, NegotiationDTO.class);
  }

  private NegotiationDTO update(Negotiation negotiationEntity, NegotiationCreateDTO request) {
    List<Request> requests = findRequests(request.getRequests());

    if (requests.stream()
        .anyMatch(
            query ->
                query.getNegotiation() != null && query.getNegotiation() != negotiationEntity)) {
      throw new WrongRequestException(
          "One or more request object is already assigned to another negotiation");
    }

    requests.forEach(
        query -> {
          query.setNegotiation(negotiationEntity);
        });

    negotiationEntity.setRequests(new HashSet<>(requests));

    try {
      Negotiation negotiation = negotiationRepository.save(negotiationEntity);
      return modelMapper.map(negotiation, NegotiationDTO.class);
    } catch (DataException | DataIntegrityViolationException ex) {
      throw new EntityNotStorableException();
    }
  }

  /**
   * Updates the negotiation with the specified ID.
   *
   * @param negotiationId the negotiationId of the negotiation tu update
   * @param negotiationBody the NegotiationCreateDTO DTO with the new Negotiation data
   * @return The updated Negotiation entity
   */
  public NegotiationDTO update(String negotiationId, NegotiationCreateDTO negotiationBody) {
    Negotiation negotiationEntity = findEntityById(negotiationId, true);
    return update(negotiationEntity, negotiationBody);
  }

  public NegotiationDTO addRequestToNegotiation(String negotiationId, String requestId) {
    Negotiation negotiationEntity = findEntityById(negotiationId, false);
    Request requestEntity =
        requestRepository
            .findById(requestId)
            .orElseThrow(() -> new EntityNotFoundException(requestId));
    negotiationEntity.getRequests().add(requestEntity);
    requestEntity.setNegotiation(negotiationEntity);
    try {
      negotiationEntity = negotiationRepository.save(negotiationEntity);
      return modelMapper.map(negotiationEntity, NegotiationDTO.class);
    } catch (DataIntegrityViolationException ex) {
      throw new EntityNotStorableException();
    }
  }

  /**
   * Returns all negotiation in the repository
   *
   * @return the List of Negotiation entities
   */
  public List<NegotiationDTO> findAll() {
    List<Negotiation> negotiations = negotiationRepository.findAll();
    return negotiations.stream()
        .map(negotiation -> modelMapper.map(negotiation, NegotiationDTO.class))
        .collect(Collectors.toList());
  }

  private Negotiation findEntityById(String negotiationId, boolean includeDetails) {
    if (includeDetails) {
      return negotiationRepository
          .findDetailedById(negotiationId)
          .orElseThrow(() -> new EntityNotFoundException(negotiationId));
    } else {
      return negotiationRepository
          .findById(negotiationId)
          .orElseThrow(() -> new EntityNotFoundException(negotiationId));
    }
  }

  /**
   * Returns the Negotiation with the specified negotiationId if exists, otherwise it throws an
   * exception
   *
   * @param negotiationId the negotiationId of the Negotiation to retrieve
   * @param includeDetails whether the negotiation returned include details
   * @return the Negotiation with specified negotiationId
   */
  public NegotiationDTO findById(String negotiationId, boolean includeDetails)
      throws EntityNotFoundException {
    Negotiation negotiation = findEntityById(negotiationId, includeDetails);
    return modelMapper.map(negotiation, NegotiationDTO.class);
  }

  /**
   * Returns a List of Negotiation entities filtered by biobank id
   *
   * @param biobankId the id in the data source of the biobank of the negotiation
   * @return the List of Negotiation entities found
   */
  public List<NegotiationDTO> findByBiobankId(String biobankId) {
    throw new NotImplementedException("Not yet implemented");
  }

  /**
   * Returns a List of Negotiation entities filtered by biobank id
   *
   * @param resourceId the id in the data source of the biobank of the negotiation
   * @return the List of Negotiation entities found
   */
  public List<NegotiationDTO> findByResourceId(String resourceId) {
    List<Negotiation> negotiations = negotiationRepository.findByCollectionId(resourceId);
    return negotiations.stream()
        .map(negotiation -> modelMapper.map(negotiation, NegotiationDTO.class))
        .collect(Collectors.toList());
  }

  public List<NegotiationDTO> findByUserIdAndRole(String userId, String userRole) {
    List<Negotiation> negotiations = negotiationRepository.findByUserIdAndRole(userId, userRole);
    return negotiations.stream()
        .map(negotiation -> modelMapper.map(negotiation, NegotiationDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  public List<NegotiationDTO> findByCreatorId(Long personId) {
    List<Negotiation> negotiations = negotiationRepository.findByCreatedBy_Id(personId);
    return negotiations.stream()
        .map(negotiation -> modelMapper.map(negotiation, NegotiationDTO.class))
        .collect(Collectors.toList());
  }

  @Transactional
  public void enablePosts(String negotiationId) {
    Negotiation negotiation =
        negotiationRepository
            .findById(negotiationId)
            .orElseThrow(() -> new EntityNotFoundException(negotiationId));
    negotiation.setPostsEnabled(true);
    negotiationRepository.save(negotiation);
  }

  @Override
  public List<NegotiationDTO> findAllWithCurrentState(NegotiationState negotiationState) {
    return negotiationRepository.findByCurrentState(negotiationState).stream()
        .map(negotiation -> modelMapper.map(negotiation, NegotiationDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  public List<NegotiationDTO> findByResourceIds(List<String> resourceIds) {
    List<Negotiation> negotiations = negotiationRepository.findByCollectionIds(resourceIds);

    return negotiations.stream()
        .map(negotiation -> modelMapper.map(negotiation, NegotiationDTO.class))
        .collect(Collectors.toList());
  }
}
