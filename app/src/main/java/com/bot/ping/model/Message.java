package com.bot.ping.model;

public class Message {
    private String uuid;
    private String message;
    private String time;
    private String date;
    private String from;
    private String to;
    private String status;
    private String msgType;
    public final static String MSG_TYPE_SENT = "MSG_TYPE_SENT";
    public final static String MSG_TYPE_RECEIVED = "MSG_TYPE_RECEIVED";
    public final static String MSG_STATUS_SENT = "MSG_STATUS_SENT";
    public final static String MSG_STATUS_DELIVERED = "MSG_STATUS_DELIVERED";
    public final static String MSG_STATUS_READED = "MSG_STATUS_READED";
    public Message(String message, String msgType){
        this.setMessage(message);
        this.setMsgType(msgType);
    }
    public Message(String uuid, String from, String to,String message, String time, String date){
        this.setUuid(uuid);
        this.setMessage(message);
        this.setTime(time);
        this.setDate(date);
        this.setFrom(from);
        this.setTo(to);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
    public String getMsgType() {
        return msgType;
    }
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
