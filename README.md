#Follower Maze : 

I have tried to break down the problem in three subproblems
 1. Problem of managing client connections
 2. Problem of managing the messages from event source.
 3. Problem of handling out of order messages from event source and processing.

Disscussing each of them below,
 1. **Managing client connections [*ClientManager*]:** To solve this problem I have tried to divide the problem into two sub tasks.
	 
     - **Accepting Client connections** : A Thread manages this task. *ClientAcceptor.java* creates a ServerSocket to listen on port 9099 to hear for any Client that wants to connect to the Server. The channel thus createdafter each client connection is pushed in a shared queue i.e, *channelQueue* 
     - **Managing Client connections** : A Thred manages this task. *ClientProcessor.java* fetches channels from the shared queue i.e, *channelQueue* . It attaches the channel to a *Selector* and to a *Client* Object. This way we can send or receive messages to a *Client* using the client object. A Map of Client ID --> Client Obj is created which is shared with MessageProcessor.

 2. **Reading events from event source [*EventManager*]:** Reading a stream of bytes from the source and dividing them into small tokens on the basis of message structure stated in the problem. This manager owns one thread i.e *EventReader.java*. 
	 
     - **Reading events** : *EventReader.java* reads bytes from the channel and tokenizes them into messages. It then pushes these messages on a shared queue i.e *messageQueue*. These messages are yet in the same order as received.

 3. **Handling out of order messages from event source and processing [*MessageProcessor*]:** To solve this problem I have tried to divide the problem into two sub tasks.
	 
     - **Message sequencer** : A Thread manages this task. *MessageSequencer.java* reads messages from  *messageQueue* and based on their sequence number puts them in a temporary sorted buffer or in a *orderedMsgQueue*. MessageSequencer adds all received messages in ascending order in the *orderedMsgQueue*.
     - **Message handler** : A Thred manages this task. *MessageHandler.java* fetches ordered messages from *orderedMsgQueue* and processes each message.




**Issues with current code:**
 1. MessageHandler sends out message with a valid byte count as return after writing message in the channel, but the Client on the test app fails to receive it and hence times out eventually. 



**Future enhancements:**
 1. Implementing a Logger to log all events.
        
