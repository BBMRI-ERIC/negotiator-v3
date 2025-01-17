package eu.bbmri_eric.negotiator.negotiation.request;

import eu.bbmri_eric.negotiator.negotiation.dto.RequestCreateDTO;
import eu.bbmri_eric.negotiator.negotiation.dto.RequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v3")
@Tag(name = "Requests", description = "Handover of Resources to the Negotiator to apply for access")
public class RequestController {

  @Autowired private RequestService requestService;

  @GetMapping("/requests")
  List<RequestDTO> list() {
    return requestService.findAll();
  }

  @GetMapping("/requests/{id}")
  public RequestDTO retrieve(@PathVariable String id) {
    return requestService.findById(id);
  }

  @PostMapping(
      value = "/requests",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Create a request to apply for access",
      description =
          "This is the handover endpoint for Discovery Services that wish to use the Negotiator for access applications. No need to provide any form of authentication for this request as the user will be asked to authenticate upon redirect.")
  RequestDTO add(@Valid @RequestBody RequestCreateDTO queryRequest) {
    return requestService.create(queryRequest);
  }

  @PutMapping(
      value = "/requests/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  RequestDTO update(
      @Valid @PathVariable String id, @Valid @RequestBody RequestCreateDTO queryRequest) {
    return requestService.update(id, queryRequest);
  }
}
