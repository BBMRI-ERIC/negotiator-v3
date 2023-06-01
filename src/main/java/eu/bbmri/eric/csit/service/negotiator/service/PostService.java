package eu.bbmri.eric.csit.service.negotiator.service;

import eu.bbmri.eric.csit.service.negotiator.dto.post.PostCreateDTO;
import eu.bbmri.eric.csit.service.negotiator.dto.post.PostDTO;
import java.util.List;

public interface PostService {

  PostDTO create(PostCreateDTO postRequest, Long personId, String negotiationId);

  List<PostDTO> findByNegotiationId(String negotiationId);

  List<PostDTO> findNewByNegotiationIdAndPosters(String negotiationId, List posters);

  PostDTO update(PostCreateDTO updateRequest, String negotiationId, String postId);

}
