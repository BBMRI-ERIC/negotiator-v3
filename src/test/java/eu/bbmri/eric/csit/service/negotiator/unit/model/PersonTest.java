package eu.bbmri.eric.csit.service.negotiator.unit.model;

import static org.junit.jupiter.api.Assertions.*;

import eu.bbmri.eric.csit.service.negotiator.database.model.Person;
import org.junit.jupiter.api.Test;

public class PersonTest {
  @Test
  void testInit() {
    assertInstanceOf(Person.class, new Person());
  }

  @Test
  void getPersonId() {
    Person person = new Person();
    assertNull(person.getId());
  }
}
