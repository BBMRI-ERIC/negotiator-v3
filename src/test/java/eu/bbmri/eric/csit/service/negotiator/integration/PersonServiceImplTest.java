package eu.bbmri.eric.csit.service.negotiator.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.bbmri.eric.csit.service.negotiator.database.model.Person;
import eu.bbmri.eric.csit.service.negotiator.database.repository.PersonRepository;
import eu.bbmri.eric.csit.service.negotiator.dto.person.UserModel;
import eu.bbmri.eric.csit.service.negotiator.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PersonServiceImplTest {

  @Autowired PersonService personService;
  @Autowired PersonRepository personRepository;

  @Test
  void loadContext() {}

  @Test
  void findAll_page_ok() {
    int count = personService.findAll().size();
    Iterable<UserModel> result = personService.findAll(0, 1);
    assertInstanceOf(Page.class, result);
    assertEquals(count, ((Page<UserModel>) result).getTotalElements());
  }

  @Test
  void findAll_invalidSort_throwsIllegalArg() {
    assertThrows(IllegalArgumentException.class, () -> personService.findAll(0, 1, "invalid"));
  }

  @Test
  void findAll_pageAndSorted_ok() {
    Person person =
        personRepository.save(
            Person.builder().subjectId("test-id").name("AAAAA").email("test@test.com").build());
    assertEquals(
        person.getId().toString(),
        ((Page<UserModel>) personService.findAll(0, 1, "name")).getContent().get(0).getId());
  }

  @Test
  void findAll_tooHighPageNumber_throwsIllegalArg() {
    assertThrows(IllegalArgumentException.class, () -> personService.findAll(999, 1));
  }
}