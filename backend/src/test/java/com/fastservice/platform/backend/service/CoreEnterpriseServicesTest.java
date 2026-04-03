package com.fastservice.platform.backend.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.fastservice.platform.backend.access.AccessControlServiceImpl;
import com.fastservice.platform.backend.kanban.KanbanServiceImpl;
import com.fastservice.platform.backend.project.ProjectServiceImpl;
import com.fastservice.platform.backend.support.BackendTestSupport;
import com.fastservice.platform.backend.ticket.TicketServiceImpl;
import com.fastservice.platform.backend.user.UserServiceImpl;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CoreEnterpriseServicesTest {

    @BeforeAll
    void bootstrapBackend() {
        BackendTestSupport.bootstrap(BackendTestSupport.uniqueDatabaseName("service_test"), false);
    }

    @Test
    void createsUsersRolesProjectsAndTickets() {
        UserServiceImpl users = new UserServiceImpl();
        AccessControlServiceImpl access = new AccessControlServiceImpl();
        ProjectServiceImpl projects = new ProjectServiceImpl();
        KanbanServiceImpl kanbans = new KanbanServiceImpl();
        TicketServiceImpl tickets = new TicketServiceImpl();

        long userId = users.createUser("service-user", "Service User", "service@example.com");
        long roleId = access.createRole("DEV", "Developer");
        long permissionId = access.createPermission("project:view", "View Projects", "FUNCTION");
        access.assignPermissionToRole(roleId, permissionId);
        access.assignRoleToUser(userId, roleId);

        long projectId = projects.createProject("SVT", "Service Test", "Service layer test project");
        long kanbanId = kanbans.createKanban(projectId, "Sprint Board");
        long ticketId = tickets.createTicket(projectId, kanbanId, "SVT-1", "Create service test ticket", "Validate service APIs", userId);
        String movedState = tickets.moveTicket(ticketId, "IN_PROGRESS");

        assertTrue(users.listUsers().contains("service-user"));
        assertTrue(access.listRoles().contains("\"code\":\"DEV\""));
        assertTrue(access.listPermissions().contains("\"code\":\"project:view\""));
        assertTrue(access.listRolesForUser(userId).contains("\"code\":\"DEV\""));
        assertTrue(access.listPermissionsForRole(roleId).contains("\"code\":\"project:view\""));
        assertTrue(projects.listProjects().contains("Service Test"));
        assertTrue(kanbans.listKanbansByProject(projectId).contains("Sprint Board"));
        assertTrue(tickets.listTicketsByProject(projectId).contains("SVT-1"));
        assertTrue("IN_PROGRESS".equals(movedState));
    }

    @Test
    void exposesTicketWorkflowDetailAndSupportsBoundedActions() {
        UserServiceImpl users = new UserServiceImpl();
        ProjectServiceImpl projects = new ProjectServiceImpl();
        KanbanServiceImpl kanbans = new KanbanServiceImpl();
        TicketServiceImpl tickets = new TicketServiceImpl();

        long actorUserId = users.createUser("workflow-admin", "Workflow Admin", "workflow-admin@example.com");
        long reassignedUserId = users.createUser("workflow-reviewer", "Workflow Reviewer", "workflow-reviewer@example.com");
        long projectId = projects.createProject("WF", "Workflow Test", "Workflow test project");
        long kanbanId = kanbans.createKanban(projectId, "Workflow Board");
        long ticketId = tickets.createTicket(projectId, kanbanId, "WF-1", "Review workflow", "Validate workflow APIs", actorUserId);

        String submitResult = tickets.executeWorkflowAction(ticketId, "submit", actorUserId, "Submitting for review", null);
        String reassignResult = tickets.executeWorkflowAction(ticketId, "reassign", actorUserId, "Reassigning to reviewer", reassignedUserId);
        String approveResult = tickets.executeWorkflowAction(ticketId, "approve", actorUserId, "Approved for release", null);
        String workflow = tickets.getWorkflow(ticketId);

        assertTrue("IN_PROGRESS".equals(submitResult));
        assertTrue("REASSIGNED".equals(reassignResult));
        assertTrue("DONE".equals(approveResult));
        assertTrue(workflow.contains("\"state\":\"DONE\""));
        assertTrue(workflow.contains("\"availableActions\":[\"reassign\"]"));
        assertTrue(workflow.contains("\"action\":\"REASSIGN\""));
        assertTrue(workflow.contains("Workflow Reviewer"));
        assertTrue(workflow.contains("Approved for release"));
    }

    @Test
    void rejectsWorkflowActionWithoutRequiredComment() {
        UserServiceImpl users = new UserServiceImpl();
        ProjectServiceImpl projects = new ProjectServiceImpl();
        KanbanServiceImpl kanbans = new KanbanServiceImpl();
        TicketServiceImpl tickets = new TicketServiceImpl();

        long actorUserId = users.createUser("workflow-comment", "Workflow Comment", "workflow-comment@example.com");
        long projectId = projects.createProject("WFC", "Workflow Comment Test", "Workflow comment validation");
        long kanbanId = kanbans.createKanban(projectId, "Workflow Comment Board");
        long ticketId = tickets.createTicket(projectId, kanbanId, "WFC-1", "Require comment", "Comments are required", actorUserId);

        assertThrows(IllegalArgumentException.class,
                () -> tickets.executeWorkflowAction(ticketId, "submit", actorUserId, "   ", null));
    }

    @Test
    void rejectsWorkflowActionsOutsideTheirBoundedStateWindow() {
        UserServiceImpl users = new UserServiceImpl();
        ProjectServiceImpl projects = new ProjectServiceImpl();
        KanbanServiceImpl kanbans = new KanbanServiceImpl();
        TicketServiceImpl tickets = new TicketServiceImpl();

        long actorUserId = users.createUser("workflow-bounds", "Workflow Bounds", "workflow-bounds@example.com");
        long projectId = projects.createProject("WFB", "Workflow Bounds Test", "Workflow bounds validation");
        long kanbanId = kanbans.createKanban(projectId, "Workflow Bounds Board");
        long ticketId = tickets.createTicket(projectId, kanbanId, "WFB-1", "Bounded workflow", "Reject should not run from TODO", actorUserId);

        assertThrows(IllegalArgumentException.class,
                () -> tickets.executeWorkflowAction(ticketId, "reject", actorUserId, "Reject before review", null));
    }

    @Test
    void escapesJsonOutputValues() {
        UserServiceImpl users = new UserServiceImpl();

        users.createUser("json-user", "Display \"Name\"\nLine", "json\\user@example.com");

        String payload = users.listUsers();
        assertTrue(payload.contains("\"displayName\":\"Display \\\"Name\\\"\\nLine\""));
        assertTrue(payload.contains("\"email\":\"json\\\\user@example.com\""));
    }

    @Test
    void rejectsInvalidProjectAndKanbanRelationships() {
        UserServiceImpl users = new UserServiceImpl();
        ProjectServiceImpl projects = new ProjectServiceImpl();
        KanbanServiceImpl kanbans = new KanbanServiceImpl();
        TicketServiceImpl tickets = new TicketServiceImpl();

        long userId = users.createUser("relationship-user", "Relationship User", "relationship@example.com");
        long projectId = projects.createProject("REL", "Relationship Test", "Relationship validation");
        long boardId = kanbans.createKanban(projectId, "Relationship Board");
        long otherProjectId = projects.createProject("OTH", "Other Project", "Relationship mismatch");

        assertThrows(IllegalArgumentException.class, () -> kanbans.createKanban(99999L, "Broken Board"));
        assertThrows(IllegalArgumentException.class,
                () -> tickets.createTicket(projectId, 99999L, "REL-404", "Missing board", "Should fail", userId));
        assertThrows(IllegalArgumentException.class,
                () -> tickets.createTicket(otherProjectId, boardId, "OTH-1", "Wrong board", "Should fail", userId));
    }
}
