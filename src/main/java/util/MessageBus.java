package util;

import com.google.common.eventbus.EventBus;

/**
 * @author Farshad Moghaddam
 *
 */
public class MessageBus {
	private EventBus eventBus;
	private static MessageBus INSTANCE;
	
	private MessageBus(){
		eventBus = new EventBus();
	}
	
	public static MessageBus getInstance(){
		if(INSTANCE==null){
			INSTANCE = new MessageBus();
		}
		return INSTANCE;
	}
	
	public void register(Object object) {
        this.eventBus.register(object);
    }
	
	public EventBus getBus(){
		return eventBus;
	}
}
