package eu.bbmri.eric.csit.service.negotiator.api.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import eu.bbmri.eric.csit.service.negotiator.database.model.Attachment;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PostCreateDTO {

  Set<Attachment> attachments;
  @Valid
  @NotEmpty
  private String negotiationId;
  @Valid
  @NotEmpty
  private String text;
  @Valid
  @NotEmpty
  private String resourceId;


}
