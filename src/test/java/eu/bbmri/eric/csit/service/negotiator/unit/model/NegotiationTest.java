package eu.bbmri.eric.csit.service.negotiator.unit.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import eu.bbmri.eric.csit.service.negotiator.database.model.Negotiation;
import eu.bbmri.eric.csit.service.negotiator.database.model.NegotiationState;
import eu.bbmri.eric.csit.service.negotiator.database.model.Request;
import eu.bbmri.eric.csit.service.negotiator.database.model.Resource;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;

public class NegotiationTest {

  @Test
  void createNegotiation_ConstructorAndBuilder_Ok() {
    Negotiation negotiation = new Negotiation();
    assertInstanceOf(Negotiation.class, negotiation);
    Negotiation negotiationFromBuilder = Negotiation.builder().build();
    assertInstanceOf(Negotiation.class, negotiationFromBuilder);
  }

  @Test
  void getNegotiationRequests_Ok() {
    Negotiation negotiation = new Negotiation();
    Request request = new Request();
    negotiation.setRequests(new HashSet<>(List.of(request)));
    assertEquals(1, negotiation.getRequests().size());
    assertEquals(request, negotiation.getRequests().iterator().next());
  }

  @Test
  void getNegotiationResources_Ok() {
    Negotiation negotiation = new Negotiation();
    Request request = new Request();
    Resource resource = new Resource();
    resource.setSourceId("fancyId");
    request.setResources(new HashSet<>(List.of(resource)));
    negotiation.setRequests(new HashSet<>(List.of(request)));
    assertEquals(List.of(resource), negotiation.getAllResources().getResources());
  }

  @Test
  void equals_sameIds_equal() {
    Negotiation negotiation = new Negotiation();
    negotiation.setId("sameId");
    Negotiation negotiation2 = new Negotiation();
    negotiation2.setId("sameId");
    assertEquals(negotiation, negotiation2);
  }

  @Test
  void equals_noIds_equal() {
    assertEquals(new Negotiation(), new Negotiation());
  }

  @Test
  void setStatus_ok() {
    Negotiation negotiation = Negotiation.builder().currentState(NegotiationState.SUBMITTED).build();
    assertEquals(NegotiationState.SUBMITTED, negotiation.getCurrentState());
    negotiation.setCurrentState(NegotiationState.APPROVED);
    assertEquals(NegotiationState.APPROVED, negotiation.getCurrentState());
  }
}
