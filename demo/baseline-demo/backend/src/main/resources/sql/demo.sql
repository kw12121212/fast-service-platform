insert into app_user(id, username, display_name, email, enabled)
values(100, 'admin', 'Administrator', 'admin@fastservice.local', true);

insert into app_role(id, role_code, role_name)
values(200, 'ADMIN', 'Administrator');

insert into app_permission(id, permission_code, permission_name, scope)
values(300, 'dashboard:view', 'View Dashboard', 'MENU');

insert into app_user_role(user_id, role_id)
values(100, 200);

insert into app_role_permission(role_id, permission_id)
values(200, 300);

insert into app_permission(id, permission_code, permission_name, scope)
values(301, 'project:manage', 'Manage Projects', 'FUNCTION');

insert into app_role_permission(role_id, permission_id)
values(200, 301);

insert into app_permission(id, permission_code, permission_name, scope)
values(304, 'project:repository:manage', 'Manage Project Repositories', 'FUNCTION');

insert into app_role_permission(role_id, permission_id)
values(200, 304);

insert into app_permission(id, permission_code, permission_name, scope)
values(302, 'ticket:manage', 'Manage Tickets', 'FUNCTION');

insert into app_role_permission(role_id, permission_id)
values(200, 302);

insert into app_permission(id, permission_code, permission_name, scope)
values(303, 'kanban:view', 'View Kanban', 'MENU');

insert into app_role_permission(role_id, permission_id)
values(200, 303);

