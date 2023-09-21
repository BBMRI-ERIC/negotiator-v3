package eu.bbmri.eric.csit.service.negotiator.dto.attachments;

import eu.bbmri.eric.csit.service.negotiator.dto.person.PersonDTO;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentDTO {
  @NotNull String id;

  String name;

  String contentType;

  byte[] payload;
}
