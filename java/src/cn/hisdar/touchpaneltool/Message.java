package cn.hisdar.touchpaneltool;

public class Message {

	public static final int MESSAGE_FINISH = 0;
	
	public Message() {
	}
	
	public void sendMessage(MessageHandler handler,String[] message) {
		handler.receiveMessage(message);
	}
	
	public void sendMessage(MessageHandler handler, int message) {
		handler.receiveMessage(message);
	}
}
