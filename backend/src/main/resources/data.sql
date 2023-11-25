-- Insert default users if they do not exist
INSERT INTO application_user (id, email, password, admin)
SELECT 1L, 'user@email.com', '$2a$10$xIjVArOCsuJd.3Eu1q0/h.7SJZqD.2aHhRP9DA1lHjqOqGsHxQ.dK', FALSE
  WHERE NOT EXISTS (SELECT 1 FROM application_user WHERE id = 1L);

INSERT INTO application_user (id, email, password, admin)
SELECT 2L, 'admin@email.com', '$2a$10$xIjVArOCsuJd.3Eu1q0/h.7SJZqD.2aHhRP9DA1lHjqOqGsHxQ.dK', TRUE
  WHERE NOT EXISTS (SELECT 1 FROM application_user WHERE id = 2L);

