package eu.bbmri.eric.csit.service.negotiator.service;

import eu.bbmri.eric.csit.service.negotiator.api.dto.perun.PerunUserDTO;
import eu.bbmri.eric.csit.service.negotiator.database.model.Person;
import java.util.List;

public interface PersonService {

  Person getById(Long id);

  List<Person> findAll();

  List<PerunUserDTO> createOrUpdate(List<PerunUserDTO> request);

}
