package com.example.volare.model;


import com.example.volare.global.common.BaseDocument;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "message")
public class MessageEntity extends BaseDocument {

    @Id
    private String id;

    @Field("chatRoom")
    private String chatRoomId; // ChatRoomEntity의 ID

    private String message;

    @Enumerated(EnumType.STRING)
    @Field("messageType")
    private MessageType messagetype;

    public enum MessageType {
        QUESTION, GPT
    }
}