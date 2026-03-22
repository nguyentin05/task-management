--
-- PostgreSQL database dump
--

\restrict kkqybPv7r0dXfdm756aVEI1R4xamueEHl6M5SE6WlmEU2oGwYujxQzz75mMWSLF

-- Dumped from database version 16.13 (Ubuntu 16.13-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 16.13 (Ubuntu 16.13-0ubuntu0.24.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: auth_schema; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA auth_schema;


ALTER SCHEMA auth_schema OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: invalidated_tokens; Type: TABLE; Schema: auth_schema; Owner: postgres
--

CREATE TABLE auth_schema.invalidated_tokens (
    id character varying(255) NOT NULL,
    expiry_time timestamp with time zone
);


ALTER TABLE auth_schema.invalidated_tokens OWNER TO postgres;

--
-- Name: outbox_events; Type: TABLE; Schema: auth_schema; Owner: postgres
--

CREATE TABLE auth_schema.outbox_events (
    id character varying(255) NOT NULL,
    routing_key character varying(255) NOT NULL,
    payload text NOT NULL,
    status character varying(50) NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    retry_count integer DEFAULT 0 NOT NULL
);


ALTER TABLE auth_schema.outbox_events OWNER TO postgres;

--
-- Name: roles; Type: TABLE; Schema: auth_schema; Owner: postgres
--

CREATE TABLE auth_schema.roles (
    name character varying(255) NOT NULL,
    description text
);


ALTER TABLE auth_schema.roles OWNER TO postgres;

--
-- Name: user_role; Type: TABLE; Schema: auth_schema; Owner: postgres
--

CREATE TABLE auth_schema.user_role (
    user_id character varying(255) NOT NULL,
    role_name character varying(255) NOT NULL
);


ALTER TABLE auth_schema.user_role OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: auth_schema; Owner: postgres
--

CREATE TABLE auth_schema.users (
    id character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    password character varying(255) NOT NULL
);


ALTER TABLE auth_schema.users OWNER TO postgres;

--
-- Name: invalidated_tokens invalidated_tokens_pkey; Type: CONSTRAINT; Schema: auth_schema; Owner: postgres
--

ALTER TABLE ONLY auth_schema.invalidated_tokens
    ADD CONSTRAINT invalidated_tokens_pkey PRIMARY KEY (id);


--
-- Name: outbox_events outbox_events_pkey; Type: CONSTRAINT; Schema: auth_schema; Owner: postgres
--

ALTER TABLE ONLY auth_schema.outbox_events
    ADD CONSTRAINT outbox_events_pkey PRIMARY KEY (id);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: auth_schema; Owner: postgres
--

ALTER TABLE ONLY auth_schema.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (name);


--
-- Name: user_role user_role_pkey; Type: CONSTRAINT; Schema: auth_schema; Owner: postgres
--

ALTER TABLE ONLY auth_schema.user_role
    ADD CONSTRAINT user_role_pkey PRIMARY KEY (user_id, role_name);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: auth_schema; Owner: postgres
--

ALTER TABLE ONLY auth_schema.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: auth_schema; Owner: postgres
--

ALTER TABLE ONLY auth_schema.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: index_invalidated_tokens_expiry_time; Type: INDEX; Schema: auth_schema; Owner: postgres
--

CREATE INDEX index_invalidated_tokens_expiry_time ON auth_schema.invalidated_tokens USING btree (expiry_time);


--
-- Name: index_outbox_events_polling; Type: INDEX; Schema: auth_schema; Owner: postgres
--

CREATE INDEX index_outbox_events_polling ON auth_schema.outbox_events USING btree (status, retry_count, created_at);


--
-- Name: index_users_email; Type: INDEX; Schema: auth_schema; Owner: postgres
--

CREATE INDEX index_users_email ON auth_schema.users USING btree (email);


--
-- Name: user_role foreign_key_role; Type: FK CONSTRAINT; Schema: auth_schema; Owner: postgres
--

ALTER TABLE ONLY auth_schema.user_role
    ADD CONSTRAINT foreign_key_role FOREIGN KEY (role_name) REFERENCES auth_schema.roles(name) ON DELETE CASCADE;


--
-- Name: user_role foreign_key_user; Type: FK CONSTRAINT; Schema: auth_schema; Owner: postgres
--

ALTER TABLE ONLY auth_schema.user_role
    ADD CONSTRAINT foreign_key_user FOREIGN KEY (user_id) REFERENCES auth_schema.users(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict kkqybPv7r0dXfdm756aVEI1R4xamueEHl6M5SE6WlmEU2oGwYujxQzz75mMWSLF

