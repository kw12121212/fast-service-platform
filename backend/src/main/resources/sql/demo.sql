insert into app_user(id, username, display_name, email, enabled)
values(100, 'admin', 'Administrator', 'admin@fastservice.local', true);

insert into app_role(id, role_code, role_name)
values(200, 'ADMIN', 'Administrator');

insert into app_permission(id, permission_code, permission_name, scope)
values(300, 'dashboard:view', 'View Dashboard', 'MENU');

insert into app_permission(id, permission_code, permission_name, scope)
values(301, 'project:manage', 'Manage Projects', 'FUNCTION');

insert into app_permission(id, permission_code, permission_name, scope)
values(302, 'ticket:manage', 'Manage Tickets', 'FUNCTION');

insert into app_permission(id, permission_code, permission_name, scope)
values(303, 'kanban:view', 'View Kanban', 'MENU');

insert into app_user_role(user_id, role_id)
values(100, 200);

insert into app_role_permission(role_id, permission_id)
values(200, 300);

insert into app_role_permission(role_id, permission_id)
values(200, 301);

insert into app_role_permission(role_id, permission_id)
values(200, 302);

insert into app_role_permission(role_id, permission_id)
values(200, 303);

-- Demo users for team membership
insert into app_user(id, username, display_name, email, enabled)
values(101, 'alice', 'Alice Chen', 'alice@fastservice.local', true);

insert into app_user(id, username, display_name, email, enabled)
values(102, 'bob', 'Bob Kumar', 'bob@fastservice.local', true);

insert into app_user(id, username, display_name, email, enabled)
values(103, 'carol', 'Carol Park', 'carol@fastservice.local', true);

-- Team roles
insert into team_role(id, role_code, role_name)
values(1, 'SCRUM_MASTER', 'Scrum Master');

insert into team_role(id, role_code, role_name)
values(2, 'PRODUCT_OWNER', 'Product Owner');

insert into team_role(id, role_code, role_name)
values(3, 'DEVELOPER', 'Developer');

insert into team_role(id, role_code, role_name)
values(4, 'OBSERVER', 'Observer');

-- Sample teams
insert into team(id, name, description, status)
values(400, 'Platform Core', 'Core platform engineering team', 'Active');

insert into team(id, name, description, status)
values(401, 'Frontend Delivery', 'Frontend feature delivery team', 'Active');

insert into team(id, name, description, status)
values(402, 'DevOps', 'Infrastructure and deployment team', 'Archived');

-- Team memberships
insert into team_member(id, team_id, user_id)
values(500, 400, 100);

insert into team_member(id, team_id, user_id)
values(501, 400, 101);

insert into team_member(id, team_id, user_id)
values(502, 401, 102);

insert into team_member(id, team_id, user_id)
values(503, 401, 103);

insert into team_member(id, team_id, user_id)
values(504, 400, 102);

-- Team role assignments
insert into team_member_role(team_member_id, team_role_id)
values(500, 1);

insert into team_member_role(team_member_id, team_role_id)
values(501, 3);

insert into team_member_role(team_member_id, team_role_id)
values(502, 2);

insert into team_member_role(team_member_id, team_role_id)
values(503, 3);

insert into team_member_role(team_member_id, team_role_id)
values(504, 3);
