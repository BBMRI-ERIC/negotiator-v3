package eu.bbmri.eric.csit.service.negotiator.service;

import eu.bbmri.eric.csit.service.negotiator.api.dto.post.PostCreateDTO;
import eu.bbmri.eric.csit.service.negotiator.api.dto.post.PostDTO;
import eu.bbmri.eric.csit.service.negotiator.database.model.Negotiation;
import eu.bbmri.eric.csit.service.negotiator.database.model.Person;
import eu.bbmri.eric.csit.service.negotiator.database.model.Post;
import eu.bbmri.eric.csit.service.negotiator.database.model.Resource;
import eu.bbmri.eric.csit.service.negotiator.database.repository.NegotiationRepository;
import eu.bbmri.eric.csit.service.negotiator.database.repository.PersonRepository;
import eu.bbmri.eric.csit.service.negotiator.database.repository.PostRepository;
import eu.bbmri.eric.csit.service.negotiator.database.repository.ResourceRepository;
import eu.bbmri.eric.csit.service.negotiator.exceptions.EntityNotStorableException;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.extern.apachecommons.CommonsLog;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
@CommonsLog
public class PostServiceImpl implements PostService {

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private ResourceRepository resourceRepository;

  @Autowired
  private NegotiationRepository negotiationRepository;

  @Autowired
  private PersonRepository personRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Transactional
  public PostDTO create(PostCreateDTO postRequest, Long personId) {
    Post postEntity = modelMapper.map(postRequest, Post.class);
    try {
      String resourceId = postRequest.getResourceId();
      Optional<Resource> resource = resourceRepository.findById(Long.valueOf(resourceId));

      String negotiationId = postRequest.getNegotiationId();
      Negotiation negotiation = negotiationRepository.getById(negotiationId);

      Optional<Person> person = personRepository.findDetailedById(personId);

      postEntity.setResource(resource.get());
      postEntity.setNegotiation(negotiation);
      postEntity.setPoster(person.get());
      postEntity.setPostStatus("CREATED");

      Post post = postRepository.save(postEntity);
      return modelMapper.map(post, PostDTO.class);

    } catch (DataIntegrityViolationException ex) {
      throw new EntityNotStorableException();
    }

  }

}
