--
-- PostgreSQL database dump
--

\restrict cpj4qvRRP4tFBBy0hrs4w7TY1FJqhTycqYrwMpYJ0QCZZEWWU7ijb5sMzf5plTI

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
-- Name: task_schema; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA task_schema;


ALTER SCHEMA task_schema OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: columns; Type: TABLE; Schema: task_schema; Owner: postgres
--

CREATE TABLE task_schema.columns (
    id character varying(255) NOT NULL,
    project_id character varying(255) NOT NULL,
    name character varying(255),
    "position" double precision NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE task_schema.columns OWNER TO postgres;

--
-- Name: project_member; Type: TABLE; Schema: task_schema; Owner: postgres
--

CREATE TABLE task_schema.project_member (
    id character varying(255) NOT NULL,
    project_id character varying(255) NOT NULL,
    user_id character varying(255) NOT NULL,
    role character varying(50) NOT NULL
);


ALTER TABLE task_schema.project_member OWNER TO postgres;

--
-- Name: projects; Type: TABLE; Schema: task_schema; Owner: postgres
--

CREATE TABLE task_schema.projects (
    id character varying(255) NOT NULL,
    name character varying(255),
    description text,
    created_by character varying(255),
    start_at timestamp with time zone,
    end_at timestamp with time zone,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE task_schema.projects OWNER TO postgres;

--
-- Name: tasks; Type: TABLE; Schema: task_schema; Owner: postgres
--

CREATE TABLE task_schema.tasks (
    id character varying(255) NOT NULL,
    column_id character varying(255) NOT NULL,
    title character varying(255) NOT NULL,
    description text,
    "position" double precision NOT NULL,
    start_at timestamp with time zone,
    due_at timestamp with time zone,
    created_by character varying(255),
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    completed_at timestamp with time zone,
    assignee_id character varying(255),
    label character varying(255)
);


ALTER TABLE task_schema.tasks OWNER TO postgres;

--
-- Name: workspace_project; Type: TABLE; Schema: task_schema; Owner: postgres
--

CREATE TABLE task_schema.workspace_project (
    project_id character varying(255) NOT NULL,
    workspace_id character varying(255) NOT NULL
);


ALTER TABLE task_schema.workspace_project OWNER TO postgres;

--
-- Name: workspaces; Type: TABLE; Schema: task_schema; Owner: postgres
--

CREATE TABLE task_schema.workspaces (
    id character varying(255) NOT NULL,
    user_id character varying(255) NOT NULL,
    name character varying(255),
    description text,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE task_schema.workspaces OWNER TO postgres;

--
-- Name: columns columns_pkey; Type: CONSTRAINT; Schema: task_schema; Owner: postgres
--

ALTER TABLE ONLY task_schema.columns
    ADD CONSTRAINT columns_pkey PRIMARY KEY (id);


--
-- Name: project_member project_member_pkey; Type: CONSTRAINT; Schema: task_schema; Owner: postgres
--

ALTER TABLE ONLY task_schema.project_member
    ADD CONSTRAINT project_member_pkey PRIMARY KEY (id);


--
-- Name: projects projects_pkey; Type: CONSTRAINT; Schema: task_schema; Owner: postgres
--

ALTER TABLE ONLY task_schema.projects
    ADD CONSTRAINT projects_pkey PRIMARY KEY (id);


--
-- Name: tasks tasks_pkey; Type: CONSTRAINT; Schema: task_schema; Owner: postgres
--

ALTER TABLE ONLY task_schema.tasks
    ADD CONSTRAINT tasks_pkey PRIMARY KEY (id);


--
-- Name: project_member unique_project_member_project_id_user_id; Type: CONSTRAINT; Schema: task_schema; Owner: postgres
--

ALTER TABLE ONLY task_schema.project_member
    ADD CONSTRAINT unique_project_member_project_id_user_id UNIQUE (project_id, user_id);


--
-- Name: workspace_project workspace_project_pkey; Type: CONSTRAINT; Schema: task_schema; Owner: postgres
--

ALTER TABLE ONLY task_schema.workspace_project
    ADD CONSTRAINT workspace_project_pkey PRIMARY KEY (project_id, workspace_id);


--
-- Name: workspaces workspaces_pkey; Type: CONSTRAINT; Schema: task_schema; Owner: postgres
--

ALTER TABLE ONLY task_schema.workspaces
    ADD CONSTRAINT workspaces_pkey PRIMARY KEY (id);


--
-- Name: workspaces workspaces_user_id_key; Type: CONSTRAINT; Schema: task_schema; Owner: postgres
--

ALTER TABLE ONLY task_schema.workspaces
    ADD CONSTRAINT workspaces_user_id_key UNIQUE (user_id);


--
-- Name: index_columns_position; Type: INDEX; Schema: task_schema; Owner: postgres
--

CREATE INDEX index_columns_position ON task_schema.columns USING btree (project_id, "position");


--
-- Name: index_columns_project_id; Type: INDEX; Schema: task_schema; Owner: postgres
--

CREATE INDEX index_columns_project_id ON task_schema.columns USING btree (project_id);


--
-- Name: index_project_member_project_id_user_id; Type: INDEX; Schema: task_schema; Owner: postgres
--

CREATE INDEX index_project_member_project_id_user_id ON task_schema.project_member USING btree (project_id, user_id);


--
-- Name: index_tasks_assignee_id; Type: INDEX; Schema: task_schema; Owner: postgres
--

CREATE INDEX index_tasks_assignee_id ON task_schema.tasks USING btree (assignee_id);


--
-- Name: index_tasks_column_id; Type: INDEX; Schema: task_schema; Owner: postgres
--

CREATE INDEX index_tasks_column_id ON task_schema.tasks USING btree (column_id);


--
-- Name: index_tasks_position; Type: INDEX; Schema: task_schema; Owner: postgres
--

CREATE INDEX index_tasks_position ON task_schema.tasks USING btree (column_id, "position");


--
-- Name: index_workspace_project_project_id; Type: INDEX; Schema: task_schema; Owner: postgres
--

CREATE INDEX index_workspace_project_project_id ON task_schema.workspace_project USING btree (project_id);


--
-- Name: index_workspace_project_workspace_id; Type: INDEX; Schema: task_schema; Owner: postgres
--

CREATE INDEX index_workspace_project_workspace_id ON task_schema.workspace_project USING btree (workspace_id);


--
-- Name: index_workspaces_user_id; Type: INDEX; Schema: task_schema; Owner: postgres
--

CREATE INDEX index_workspaces_user_id ON task_schema.workspaces USING btree (user_id);


--
-- Name: tasks foreign_key_columns; Type: FK CONSTRAINT; Schema: task_schema; Owner: postgres
--

ALTER TABLE ONLY task_schema.tasks
    ADD CONSTRAINT foreign_key_columns FOREIGN KEY (column_id) REFERENCES task_schema.columns(id) ON DELETE CASCADE;


--
-- Name: columns foreign_key_projects; Type: FK CONSTRAINT; Schema: task_schema; Owner: postgres
--

ALTER TABLE ONLY task_schema.columns
    ADD CONSTRAINT foreign_key_projects FOREIGN KEY (project_id) REFERENCES task_schema.projects(id) ON DELETE CASCADE;


--
-- Name: project_member foreign_key_projects; Type: FK CONSTRAINT; Schema: task_schema; Owner: postgres
--

ALTER TABLE ONLY task_schema.project_member
    ADD CONSTRAINT foreign_key_projects FOREIGN KEY (project_id) REFERENCES task_schema.projects(id) ON DELETE CASCADE;


--
-- Name: workspace_project foreign_key_projects; Type: FK CONSTRAINT; Schema: task_schema; Owner: postgres
--

ALTER TABLE ONLY task_schema.workspace_project
    ADD CONSTRAINT foreign_key_projects FOREIGN KEY (project_id) REFERENCES task_schema.projects(id) ON DELETE CASCADE;


--
-- Name: workspace_project foreign_key_workspaces; Type: FK CONSTRAINT; Schema: task_schema; Owner: postgres
--

ALTER TABLE ONLY task_schema.workspace_project
    ADD CONSTRAINT foreign_key_workspaces FOREIGN KEY (workspace_id) REFERENCES task_schema.workspaces(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict cpj4qvRRP4tFBBy0hrs4w7TY1FJqhTycqYrwMpYJ0QCZZEWWU7ijb5sMzf5plTI

