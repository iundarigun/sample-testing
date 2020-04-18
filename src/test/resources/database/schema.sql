CREATE TABLE public.employee (
    id bigint NOT NULL,
    born_at date,
    collage_completed_year integer,
    created_at timestamp without time zone,
    document character varying(255),
    name character varying(255),
    updated_date timestamp without time zone,
    sector_id bigint
);

CREATE SEQUENCE public.employee_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.employee_id_seq OWNED BY public.employee.id;

CREATE TABLE public.sector (
    id bigint NOT NULL,
    code character varying(255),
    name character varying(255)
);

CREATE SEQUENCE public.sector_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.sector_id_seq OWNED BY public.sector.id;

ALTER TABLE ONLY public.employee ALTER COLUMN id SET DEFAULT nextval('public.employee_id_seq'::regclass);

ALTER TABLE ONLY public.sector ALTER COLUMN id SET DEFAULT nextval('public.sector_id_seq'::regclass);

ALTER TABLE ONLY public.employee
    ADD CONSTRAINT employee_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.sector
    ADD CONSTRAINT sector_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.employee
    ADD CONSTRAINT fkqxbo4fprgn1j40k2bgan17p4j FOREIGN KEY (sector_id) REFERENCES public.sector(id);

