package com.fastservice.platform.backend.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.fastservice.platform.backend.kanban.KanbanServiceImpl;
import com.fastservice.platform.backend.project.ProjectServiceImpl;
import com.fastservice.platform.backend.support.BackendTestSupport;
import com.fastservice.platform.backend.ticket.TicketServiceImpl;
import com.fastservice.platform.backend.ticket.TicketWorkflowServiceImpl;
import com.fastservice.platform.backend.user.UserServiceImpl;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicketWorkflowServiceTest {

    @BeforeAll
    void bootstrapBackend() {
        BackendTestSupport.bootstrap(BackendTestSupport.uniqueDatabaseName("workflow_test"), false);
    }

    @Test
    void getWorkflowReturnsWorkflowInstanceShape() {
        UserServiceImpl users = new UserServiceImpl();
        ProjectServiceImpl projects = new ProjectServiceImpl();
        KanbanServiceImpl kanbans = new KanbanServiceImpl();
        TicketServiceImpl tickets = new TicketServiceImpl();
        TicketWorkflowServiceImpl workflow = new TicketWorkflowServiceImpl();

        long userId = users.createUser("wf-shape-user", "Shape User", "wf-shape@example.com");
        long projectId = projects.createProject("WFS", "Workflow Shape", "Shape test");
        long kanbanId = kanbans.createKanban(projectId, "Shape Board");
        long ticketId = tickets.createTicket(projectId, kanbanId, "WFS-1", "Shape ticket", "Validate shape", userId);

        String result = workflow.getWorkflow(ticketId);

        assertTrue(result.contains("\"ticketId\":" + ticketId));
        assertTrue(result.contains("\"ticketKey\":\"WFS-1\""));
        assertTrue(result.contains("\"title\":\"Shape ticket\""));
        assertTrue(result.contains("\"state\":\"TODO\""));
        assertTrue(result.contains("\"assignee\":{"));
        assertTrue(result.contains("\"userId\":" + userId));
        assertTrue(result.contains("\"availableActions\":["));
        assertTrue(result.contains("\"history\":["));
    }

    @Test
    void validStateTransitionsSucceed() {
        UserServiceImpl users = new UserServiceImpl();
        ProjectServiceImpl projects = new ProjectServiceImpl();
        KanbanServiceImpl kanbans = new KanbanServiceImpl();
        TicketServiceImpl tickets = new TicketServiceImpl();
        TicketWorkflowServiceImpl workflow = new TicketWorkflowServiceImpl();

        long userId = users.createUser("wf-transition-user", "Transition User", "wf-transition@example.com");
        long projectId = projects.createProject("WFT", "Workflow Transition", "Transition test");
        long kanbanId = kanbans.createKanban(projectId, "Transition Board");
        long ticketId = tickets.createTicket(projectId, kanbanId, "WFT-1", "Transition ticket", "Validate transitions", userId);

        String submitResult = workflow.executeWorkflowAction(ticketId, "submit", userId, "Submitting for review", null);
        String approveResult = workflow.executeWorkflowAction(ticketId, "approve", userId, "Approved", null);

        assertTrue("IN_PROGRESS".equals(submitResult));
        assertTrue("DONE".equals(approveResult));

        String wf = workflow.getWorkflow(ticketId);
        assertTrue(wf.contains("\"state\":\"DONE\""));
    }

    @Test
    void invalidStateTransitionIsRejected() {
        UserServiceImpl users = new UserServiceImpl();
        ProjectServiceImpl projects = new ProjectServiceImpl();
        KanbanServiceImpl kanbans = new KanbanServiceImpl();
        TicketServiceImpl tickets = new TicketServiceImpl();
        TicketWorkflowServiceImpl workflow = new TicketWorkflowServiceImpl();

        long userId = users.createUser("wf-invalid-user", "Invalid User", "wf-invalid@example.com");
        long projectId = projects.createProject("WFI", "Workflow Invalid", "Invalid transition test");
        long kanbanId = kanbans.createKanban(projectId, "Invalid Board");
        long ticketId = tickets.createTicket(projectId, kanbanId, "WFI-1", "Invalid ticket", "Reject from TODO", userId);

        assertThrows(IllegalArgumentException.class,
                () -> workflow.executeWorkflowAction(ticketId, "reject", userId, "Reject before review", null));
    }

    @Test
    void blankCommentIsRejected() {
        UserServiceImpl users = new UserServiceImpl();
        ProjectServiceImpl projects = new ProjectServiceImpl();
        KanbanServiceImpl kanbans = new KanbanServiceImpl();
        TicketServiceImpl tickets = new TicketServiceImpl();
        TicketWorkflowServiceImpl workflow = new TicketWorkflowServiceImpl();

        long userId = users.createUser("wf-comment-user", "Comment User", "wf-comment@example.com");
        long projectId = projects.createProject("WFC2", "Workflow Comment 2", "Comment validation");
        long kanbanId = kanbans.createKanban(projectId, "Comment Board");
        long ticketId = tickets.createTicket(projectId, kanbanId, "WFC2-1", "Comment ticket", "Comments required", userId);

        assertThrows(IllegalArgumentException.class,
                () -> workflow.executeWorkflowAction(ticketId, "submit", userId, "   ", null));
        assertThrows(IllegalArgumentException.class,
                () -> workflow.executeWorkflowAction(ticketId, "submit", userId, null, null));
    }

    @Test
    void reassignRequiresAssigneeUserId() {
        UserServiceImpl users = new UserServiceImpl();
        ProjectServiceImpl projects = new ProjectServiceImpl();
        KanbanServiceImpl kanbans = new KanbanServiceImpl();
        TicketServiceImpl tickets = new TicketServiceImpl();
        TicketWorkflowServiceImpl workflow = new TicketWorkflowServiceImpl();

        long userId = users.createUser("wf-reassign-user", "Reassign User", "wf-reassign@example.com");
        long projectId = projects.createProject("WFR", "Workflow Reassign", "Reassign validation");
        long kanbanId = kanbans.createKanban(projectId, "Reassign Board");
        long ticketId = tickets.createTicket(projectId, kanbanId, "WFR-1", "Reassign ticket", "Assignee required", userId);

        assertThrows(IllegalArgumentException.class,
                () -> workflow.executeWorkflowAction(ticketId, "reassign", userId, "Reassigning", null));
    }
}
