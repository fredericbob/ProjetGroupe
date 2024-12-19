CREATE TABLE public.role (id SERIAL PRIMARY KEY,
                                            nom VARCHAR(50) NOT NULL UNIQUE);


CREATE TABLE public.utilisateur
    (id SERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                                                   password VARCHAR(300) NOT NULL,
                                                                         idrole INTEGER, failed_login_attempts INTEGER DEFAULT 0,
                                                                                                                               account_locked BOOLEAN DEFAULT FALSE,
                                                                                                                                                              CONSTRAINT fk_role
     FOREIGN KEY (idrole) REFERENCES public.role(id) ON DELETE
     SET NULL);


INSERT INTO public.role (nom)
VALUES ('Admin');


INSERT INTO public.role (nom)
VALUES ('Utilisateur');

