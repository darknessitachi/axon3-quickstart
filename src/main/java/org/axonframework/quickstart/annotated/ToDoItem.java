package org.axonframework.quickstart.annotated;

import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.axonframework.quickstart.api.CreateToDoItemCommand;
import org.axonframework.quickstart.api.MarkCompletedCommand;
import org.axonframework.quickstart.api.ToDoItemCompletedEvent;
import org.axonframework.quickstart.api.ToDoItemCreatedEvent;

/**
 * @author Jettro Coenradie
 */
public class ToDoItem extends AbstractAnnotatedAggregateRoot {

    @AggregateIdentifier
    private String id;

    // No-arg constructor, required by Axon
    public ToDoItem() {
    }

    @CommandHandler
    public ToDoItem(CreateToDoItemCommand command) {
        apply(new ToDoItemCreatedEvent(command.getTodoId(), command.getDescription()));
    }

    @CommandHandler
    public void markCompleted(MarkCompletedCommand command) {
        apply(new ToDoItemCompletedEvent(command.getTodoId()));
    }

    @EventSourcingHandler
    public void on(ToDoItemCreatedEvent event) {
        this.id = event.getTodoId();
    }
}
