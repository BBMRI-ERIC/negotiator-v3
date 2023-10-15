package eu.bbmri_eric.negotiator.mappers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.bbmri_eric.negotiator.configuration.state_machine.negotiation.NegotiationState;
import eu.bbmri_eric.negotiator.configuration.state_machine.resource.NegotiationResourceState;
import eu.bbmri_eric.negotiator.database.model.Negotiation;
import eu.bbmri_eric.negotiator.database.model.PersonNegotiationRole;
import eu.bbmri_eric.negotiator.dto.negotiation.NegotiationDTO;
import eu.bbmri_eric.negotiator.dto.person.PersonRoleDTO;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.apachecommons.CommonsLog;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@CommonsLog
public class NegotiationModelMapper {

  @Autowired ModelMapper modelMapper;

  public NegotiationModelMapper(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @PostConstruct
  public void addMappings() {
    TypeMap<Negotiation, NegotiationDTO> typeMap =
        modelMapper.createTypeMap(Negotiation.class, NegotiationDTO.class);

    Converter<Set<PersonNegotiationRole>, Set<PersonRoleDTO>> personsRoleConverter =
        role -> personsRoleConverter(role.getSource());

    Converter<NegotiationState, String> negotiationStatusConverter =
        status -> negotiationStatusConverter(status.getSource());

    Converter<Map<String, NegotiationResourceState>, JsonNode> resourcesStatusConverter =
        resources -> resourcesStatusConverter(resources.getSource());

    Converter<String, JsonNode> payloadConverter =
        p -> {
          try {
            return payloadConverter(p.getSource());
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e); // TODO: raise the correct exception
          }
        };

    typeMap.addMappings(
        mapper ->
            mapper
                .using(personsRoleConverter)
                .map(Negotiation::getPersons, NegotiationDTO::setPersons));
    typeMap.addMappings(
        mapper ->
            mapper
                .using(payloadConverter)
                .map(Negotiation::getPayload, NegotiationDTO::setPayload));
    typeMap.addMappings(
        mapper ->
            mapper
                .using(negotiationStatusConverter)
                .map(Negotiation::getCurrentState, NegotiationDTO::setStatus));
    typeMap.addMappings(
        mapper ->
            mapper
                .using(resourcesStatusConverter)
                .map(Negotiation::getCurrentStatePerResource, NegotiationDTO::setResourceStatus));
  }

  private Set<PersonRoleDTO> personsRoleConverter(Set<PersonNegotiationRole> personsRoles) {
    if (Objects.isNull(personsRoles)) {
      return null;
    }
    Stream<PersonRoleDTO> roles =
        personsRoles.stream()
            .map(
                personRole ->
                    new PersonRoleDTO(
                        String.valueOf(personRole.getPerson().getId()),
                        personRole.getPerson().getAuthName(),
                        personRole.getRole().getName()));
    return roles.collect(Collectors.toSet());
  }

  private JsonNode payloadConverter(String jsonPayload) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    if (jsonPayload == null) {
      jsonPayload = "{}";
    }
    return mapper.readTree(jsonPayload);
  }

  private String negotiationStatusConverter(NegotiationState currentState) {
    if (Objects.isNull(currentState)) {
      return "";
    }
    return currentState.name();
  }

  private JsonNode resourcesStatusConverter(
      Map<String, NegotiationResourceState> currentStatePerResource) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    return mapper.valueToTree(currentStatePerResource);
  }
}
