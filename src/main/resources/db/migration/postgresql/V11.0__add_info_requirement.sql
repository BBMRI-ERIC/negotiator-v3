CREATE TABLE information_requirement
(
    id                      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    required_access_form_id BIGINT,
    for_event VARCHAR(255),
    CONSTRAINT pk_informationrequirement PRIMARY KEY (id)
);

ALTER TABLE information_requirement
    ADD CONSTRAINT FK_INFORMATIONREQUIREMENT_ON_REQUIREDACCESSFORM FOREIGN KEY (required_access_form_id) REFERENCES access_form (id);