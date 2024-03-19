package eu.bbmri_eric.negotiator.mappers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import eu.bbmri_eric.negotiator.api.controller.v3.NegotiationController;
import eu.bbmri_eric.negotiator.api.controller.v3.NegotiationSortField;
import eu.bbmri_eric.negotiator.dto.negotiation.NegotiationDTO;
import eu.bbmri_eric.negotiator.dto.negotiation.NegotiationFilters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

public class NegotiationModelAssembler
    implements RepresentationModelAssembler<NegotiationDTO, EntityModel<NegotiationDTO>> {
  public NegotiationModelAssembler() {}

  @Override
  public @NonNull EntityModel<NegotiationDTO> toModel(@NonNull NegotiationDTO entity) {
    List<Link> links = new ArrayList<>();
    links.add(linkTo(NegotiationController.class).slash("negotiations").withRel("negotiations"));
    links.add(
        WebMvcLinkBuilder.linkTo(methodOn(NegotiationController.class).retrieve(entity.getId()))
            .withSelfRel());
    return EntityModel.of(entity, links);
  }

  public PagedModel<EntityModel<NegotiationDTO>> toPagedModel(
      @NonNull Page<NegotiationDTO> page,
      NegotiationFilters filters,
      NegotiationSortField sortBy,
      Sort.Direction sortOrder,
      Long userId) {

    List<Link> links = new ArrayList<>();
    if (page.hasContent()) {
      links = getLinks(page, filters, sortBy, sortOrder, userId);
    }
    return PagedModel.of(
        page.getContent().stream().map(this::toModel).collect(Collectors.toList()),
        new PagedModel.PageMetadata(
            page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages()),
        links);
  }

  public PagedModel<EntityModel<NegotiationDTO>> toPagedModel(
      @NonNull Page<NegotiationDTO> page,
      NegotiationFilters filters,
      NegotiationSortField sortBy,
      Sort.Direction sortOrder) {
    List<Link> links = new ArrayList<>();
    if (page.hasContent()) {
      links = getLinks(page, filters, sortBy, sortOrder);
    }
    return PagedModel.of(
        page.getContent().stream().map(this::toModel).collect(Collectors.toList()),
        new PagedModel.PageMetadata(
            page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages()),
        links);
  }

  private List<Link> getLinks(
      Page<NegotiationDTO> page,
      NegotiationFilters filters,
      NegotiationSortField sortBy,
      Sort.Direction sortOrder) {
    List<Link> links = new ArrayList<>();
    if (page.hasPrevious()) {
      links.add(
          linkTo(
                  methodOn(NegotiationController.class)
                      .list(
                          filters.getState(),
                          filters.getCreatedAfter(),
                          filters.getCreatedBefore(),
                          sortBy,
                          sortOrder,
                          page.getNumber() - 1,
                          page.getSize()))
              .withRel(IanaLinkRelations.PREVIOUS)
              .expand());
    }
    if (page.hasNext()) {
      links.add(
          linkTo(
                  methodOn(NegotiationController.class)
                      .list(
                          filters.getState(),
                          filters.getCreatedAfter(),
                          filters.getCreatedBefore(),
                          sortBy,
                          sortOrder,
                          page.getNumber() + 1,
                          page.getSize()))
              .withRel(IanaLinkRelations.NEXT)
              .expand());
    }
    links.add(
        linkTo(
                methodOn(NegotiationController.class)
                    .list(
                        filters.getState(),
                        filters.getCreatedAfter(),
                        filters.getCreatedBefore(),
                        sortBy,
                        sortOrder,
                        0,
                        page.getSize()))
            .withRel(IanaLinkRelations.FIRST)
            .expand());
    links.add(
        linkTo(
                methodOn(NegotiationController.class)
                    .list(
                        filters.getState(),
                        filters.getCreatedAfter(),
                        filters.getCreatedBefore(),
                        sortBy,
                        sortOrder,
                        page.getNumber(),
                        page.getSize()))
            .withRel(IanaLinkRelations.CURRENT)
            .expand());
    links.add(
        linkTo(
                methodOn(NegotiationController.class)
                    .list(
                        filters.getState(),
                        filters.getCreatedAfter(),
                        filters.getCreatedBefore(),
                        sortBy,
                        sortOrder,
                        page.getTotalPages() - 1,
                        page.getSize()))
            .withRel(IanaLinkRelations.LAST)
            .expand());
    return links;
  }

  private List<Link> getLinks(
      Page<NegotiationDTO> page,
      NegotiationFilters filters,
      NegotiationSortField sortBy,
      Sort.Direction sortOrder,
      Long userId) {
    List<Link> links = new ArrayList<>();
    if (page.hasPrevious()) {
      links.add(
          linkTo(
                  methodOn(NegotiationController.class)
                      .listRelated(
                          userId,
                          filters.getRole(),
                          filters.getState(),
                          filters.getCreatedAfter(),
                          filters.getCreatedBefore(),
                          sortBy,
                          sortOrder,
                          page.getNumber() - 1,
                          page.getSize()))
              .withRel(IanaLinkRelations.PREVIOUS)
              .expand());
    }
    if (page.hasNext()) {
      links.add(
          linkTo(
                  methodOn(NegotiationController.class)
                      .listRelated(
                          userId,
                          filters.getRole(),
                          filters.getState(),
                          filters.getCreatedAfter(),
                          filters.getCreatedBefore(),
                          sortBy,
                          sortOrder,
                          page.getNumber() + 1,
                          page.getSize()))
              .withRel(IanaLinkRelations.NEXT)
              .expand());
    }
    links.add(
        linkTo(
                methodOn(NegotiationController.class)
                    .listRelated(
                        userId,
                        filters.getRole(),
                        filters.getState(),
                        filters.getCreatedAfter(),
                        filters.getCreatedBefore(),
                        sortBy,
                        sortOrder,
                        0,
                        page.getSize()))
            .withRel(IanaLinkRelations.FIRST)
            .expand());
    links.add(
        linkTo(
                methodOn(NegotiationController.class)
                    .listRelated(
                        userId,
                        filters.getRole(),
                        filters.getState(),
                        filters.getCreatedAfter(),
                        filters.getCreatedBefore(),
                        sortBy,
                        sortOrder,
                        page.getNumber(),
                        page.getSize()))
            .withRel(IanaLinkRelations.CURRENT)
            .expand());
    links.add(
        linkTo(
                methodOn(NegotiationController.class)
                    .listRelated(
                        userId,
                        filters.getRole(),
                        filters.getState(),
                        filters.getCreatedAfter(),
                        filters.getCreatedBefore(),
                        sortBy,
                        sortOrder,
                        page.getTotalPages() - 1,
                        page.getSize()))
            .withRel(IanaLinkRelations.LAST)
            .expand());
    return links;
  }
}
