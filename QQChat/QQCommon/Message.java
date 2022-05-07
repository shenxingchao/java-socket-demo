package QQChat.QQCommon;

import java.io.Serializable;
import java.util.Arrays;

//消息类
public class Message implements Serializable {
    //发送者
    private int sender;
    //接收者
    private int receiver;
    //发送的内容
    private String content;
    //发送的时间
    private String sendTime;
    //消息类型
    private MessageType mesType;
    //图片内容
    byte[] bytes;

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public MessageType getMesType() {
        return mesType;
    }

    public void setMesType(MessageType mesType) {
        this.mesType = mesType;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", receiver=" + receiver +
                ", content='" + content + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", mesType=" + mesType +
                ", bytes=" + Arrays.toString(bytes) +
                '}';
    }
}