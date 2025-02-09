package edu.stevens.cs522.chatserver.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.Instant;

public class Message implements Parcelable {

    public long id;

    public String chatroom;

    public String messageText;

    public Instant timestamp;

    public Double latitude;

    public Double longitude;

    public String sender;

    public Message() {
    }

    @Override
    public String toString() {
        return sender + ": " + messageText;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public Message(Parcel in) {
        id = in.readLong();
        chatroom = in.readString();
        messageText = in.readString();
        timestamp = TimestampConverter.deserialize(in.readString());
        latitude = in.readDouble();
        longitude = in.readDouble();
        sender = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(chatroom);
        out.writeString(messageText);
        out.writeString(TimestampConverter.serialize(timestamp));
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeString(sender);
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {

        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }

    };

}

