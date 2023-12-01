package eu.bbmri.eric.csit.service.negotiator.integration.api.v3;

import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.bbmri.eric.csit.service.negotiator.NegotiatorApplication;
import eu.bbmri.eric.csit.service.negotiator.database.model.Person;
import eu.bbmri.eric.csit.service.negotiator.database.repository.PersonRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = NegotiatorApplication.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@CommonsLog
public class UserControllerTest {
  @Autowired private PersonRepository personRepository;
  private static final String ROLES_ENDPOINT = "/v3/users/roles";
  private static final String LIST_USERS_ENDPOINT = "/v3/users";
  private static final String RESOURCES_ENDPOINT = "/v3/users/resources";
  @Autowired private WebApplicationContext context;
  private MockMvc mockMvc;

  @BeforeEach
  public void before() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @Test
  @WithMockUser
  void getInfo_mockUserNoAuthorities_Ok() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(ROLES_ENDPOINT)).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = "biobank:1:collection:1")
  void getInfo_mockUserOneAuthority_responseIsOk() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get(ROLES_ENDPOINT))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0]", is("biobank:1:collection:1")));
  }

  @Test
  @WithMockUser(authorities = {"biobank:1:collection:1", "ROLE_RESEARCHER"})
  void getInfo_mockUserMultipleAuthorities_responseIsOk() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get(ROLES_ENDPOINT))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$")
                .value(Matchers.containsInAnyOrder("biobank:1:collection:1", "ROLE_RESEARCHER")));
  }

  @Test
  @WithUserDetails("TheBiobanker")
  void getRepresentedResources_oneResource_ok() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get(RESOURCES_ENDPOINT))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$")
                .value(
                    Matchers.containsInAnyOrder(
                        "biobank:1:collection:1", "biobank:1:collection:2")));
  }

  @Test
  void getUsers_notAuthorized_401() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get(LIST_USERS_ENDPOINT))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "AUTHORIZATION_MANAGER")
  void getUsers_authorized_ok() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(LIST_USERS_ENDPOINT)).andExpect(status().isOk());
  }

  @Test
  void getUserById_idNotLong_400() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get(LIST_USERS_ENDPOINT + "/null"))
        .andExpect(status().isBadRequest());
    mockMvc
        .perform(MockMvcRequestBuilders.get(LIST_USERS_ENDPOINT + "/fake4rd"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "AUTHORIZATION_MANAGER")
  void getUserById_validId_200() throws Exception {
    Person person = personRepository.findAll().iterator().next();
    mockMvc
        .perform(MockMvcRequestBuilders.get(LIST_USERS_ENDPOINT + "/" + person.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(person.getId().toString())))
        .andExpect(jsonPath("$.name", is(person.getName())))
        .andExpect(jsonPath("$.email", is(person.getEmail())));
  }
}
