package org.axonframework.quickstart;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.quickstart.api.MarkToDoItemOverdueCommand;
import org.axonframework.quickstart.api.ToDoItemCompletedEvent;
import org.axonframework.quickstart.api.ToDoItemCreatedEvent;
import org.axonframework.unitofwork.UnitOfWork;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.axonframework.domain.GenericEventMessage.asEventMessage;

/**
 * Simple Example that shows how to Create Saga instances, schedule deadlines and inject resources using Spring.
 *
 * @author Allard Buijze
 * @since 2.0
 */
public class RunSagaWithSpring {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("saga-config.xml");

        // let's register a Command Handler that writes to System Out so we can see what happens
        applicationContext.getBean(CommandBus.class)
                          .subscribe(MarkToDoItemOverdueCommand.class.getName(),
                                     (CommandMessage<MarkToDoItemOverdueCommand> commandMessage,
                                      UnitOfWork unitOfWork) -> {
                                         System.out.println(String.format("Got command to mark [%s] overdue!",
                                                                          commandMessage.getPayload().getTodoId()));
                                         return null;
                                     });

        EventBus eventBus = applicationContext.getBean(EventBus.class);

        // We create 2 items
        eventBus.publish(asEventMessage(new ToDoItemCreatedEvent("todo1", "Got something to do")));
        eventBus.publish(asEventMessage(new ToDoItemCreatedEvent("todo2", "Got something else to do")));
        // We mark the first completed, before the deadline expires. The Saga has a hard-coded deadline of 2 seconds
        eventBus.publish(asEventMessage(new ToDoItemCompletedEvent("todo1")));
        // we wait 3 seconds. Enough time for the deadline to expire
        Thread.sleep(3000);
        // Just a System out to remind us that we should see something
        System.out.println("Should have seen an item marked overdue, now");

        applicationContext.close();
    }
}
