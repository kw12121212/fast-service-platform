set @packageName 'com.fastservice.platform.backend.generated.model';
set @modelSrcDir './target/generated-sources/lealone-model';

create table if not exists app_user (
  id long auto_increment primary key,
  username varchar,
  display_name varchar,
  email varchar,
  enabled boolean
) package @packageName generate code @modelSrcDir;

create table if not exists app_role (
  id long auto_increment primary key,
  role_code varchar,
  role_name varchar
) package @packageName generate code @modelSrcDir;

create table if not exists app_permission (
  id long auto_increment primary key,
  permission_code varchar,
  permission_name varchar,
  scope varchar
) package @packageName generate code @modelSrcDir;

create table if not exists app_user_role (
  user_id long,
  role_id long
) package @packageName generate code @modelSrcDir;

create table if not exists app_role_permission (
  role_id long,
  permission_id long
) package @packageName generate code @modelSrcDir;

create table if not exists software_project (
  id long auto_increment primary key,
  project_key varchar,
  project_name varchar,
  project_description varchar,
  active boolean
) package @packageName generate code @modelSrcDir;

create table if not exists project_repository_binding (
  project_id long primary key,
  repository_root_path varchar
) package @packageName generate code @modelSrcDir;

create table if not exists kanban_board (
  id long auto_increment primary key,
  project_id long,
  board_name varchar
) package @packageName generate code @modelSrcDir;

create table if not exists ticket (
  id long auto_increment primary key,
  project_id long,
  kanban_id long,
  ticket_key varchar,
  title varchar,
  description varchar,
  state varchar,
  assignee_user_id long
) package @packageName generate code @modelSrcDir;
