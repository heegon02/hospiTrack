package nurseFrame;

import java.time.LocalDateTime;

public class Message {
    private int messageId;
    private int senderId;  // 의사 ID (users 테이블 참조)
    private String senderName; // 의사 이름
    private String content;
    private LocalDateTime sendTime;
    private boolean isRead;
    
    public Message(int messageId, int senderId, String senderName, String content, LocalDateTime sendTime, boolean isRead) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.sendTime = sendTime;
        this.isRead = isRead;
    }
    
    // Getters and Setters
    public int getMessageId() { return messageId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }
    
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public LocalDateTime getSendTime() { return sendTime; }
    public void setSendTime(LocalDateTime sendTime) { this.sendTime = sendTime; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
} 