set @packageName 'com.fastservice.platform.backend.generated.model';
set @modelSrcDir './target/generated-sources/lealone-model';

-- MODULE: user-management
create table if not exists app_user (
  id long auto_increment primary key,
  username varchar,
  display_name varchar,
  email varchar,
  enabled boolean
) package @packageName generate code @modelSrcDir;

-- MODULE: role-permission-management
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

-- MODULE: project-management
create table if not exists software_project (
  id long auto_increment primary key,
  project_key varchar,
  project_name varchar,
  project_description varchar,
  active boolean
) package @packageName generate code @modelSrcDir;

-- MODULE: project-repository-management
create table if not exists project_repository_binding (
  project_id long primary key,
  repository_root_path varchar
) package @packageName generate code @modelSrcDir;

-- MODULE: project-repository-management (worktree sandbox)
create table if not exists project_worktree_sandbox (
  project_id long,
  worktree_path varchar,
  init_image_script_path_override varchar,
  init_project_script_path_override varchar,
  image_status varchar,
  image_failure_message varchar,
  container_status varchar,
  container_failure_message varchar,
  primary key (project_id, worktree_path)
) package @packageName generate code @modelSrcDir;

create table if not exists project_derived_app_assembly (
  project_id long primary key,
  status varchar,
  restricted boolean,
  restriction varchar,
  source_repository_path varchar,
  latest_outcome_status varchar,
  latest_outcome_category varchar,
  latest_outcome_message varchar,
  latest_output_directory varchar,
  latest_manifest_app_id varchar,
  latest_manifest_name varchar,
  latest_request_manifest varchar,
  latest_request_output_directory varchar,
  updated_at varchar
) package @packageName generate code @modelSrcDir;

create table if not exists project_derived_app_verification (
  project_id long primary key,
  status varchar,
  restricted boolean,
  restriction varchar,
  source_repository_path varchar,
  latest_outcome_status varchar,
  latest_outcome_category varchar,
  latest_outcome_message varchar,
  latest_target_output_directory varchar,
  latest_generated_app_verification_status varchar,
  latest_generated_app_verification_message varchar,
  latest_runtime_smoke_status varchar,
  latest_runtime_smoke_message varchar,
  updated_at varchar
) package @packageName generate code @modelSrcDir;

-- MODULE: kanban-management
create table if not exists kanban_board (
  id long auto_increment primary key,
  project_id long,
  board_name varchar
) package @packageName generate code @modelSrcDir;

-- MODULE: ticket-management
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

create table if not exists ticket_workflow_history (
  id long auto_increment primary key,
  ticket_id long,
  action varchar,
  from_state varchar,
  to_state varchar,
  actor_user_id long,
  previous_assignee_user_id long,
  next_assignee_user_id long,
  comment varchar
) package @packageName generate code @modelSrcDir;
