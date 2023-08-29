package eu.bbmri.eric.csit.service.negotiator.service;

import eu.bbmri.eric.csit.service.negotiator.database.model.PostType;
import eu.bbmri.eric.csit.service.negotiator.dto.post.PostCreateDTO;
import eu.bbmri.eric.csit.service.negotiator.dto.post.PostDTO;
import java.util.List;
import java.util.Optional;

public interface PostService {

  /**
   * Creates a new post for the specified Negotiation
   *
   * @param postRequest the Post DTO containing request information
   * @param personId the ID pf the person that is creating the post
   * @param negotiationId thr ID of the negotiation to which the post refers
   * @return the response PostDTO object
   */
  PostDTO create(PostCreateDTO postRequest, Long personId, String negotiationId);

  /**
   * Finds all the posts related to a negotiation
   *
   * @param negotiationId the ID of the negotiation
   * @return the list of all the posts related to the input negotiation ID
   */
  List<PostDTO> findByNegotiationId(String negotiationId, Optional<PostType> type);

  /**
   * Finds all the posts related to a negotiation and a list of specific persons (posters)
   *
   * @param negotiationId the ID of the negotiation
   * @param posters a list of all the persons that created the posts to be found
   * @return the list of all the posts related to the input negotiation ID and persons
   */
  List<PostDTO> findNewByNegotiationIdAndPosters(String negotiationId, List posters, Optional<PostType> type);

  /**
   * Updates a specific post
   *
   * @param updateRequest the Post DTO containing post information to be updates
   * @param negotiationId the ID of the negotiation
   * @param postId the ID of the post to update
   * @return the response PostDTO object
   */
  PostDTO update(PostCreateDTO updateRequest, String negotiationId, String postId);
}
