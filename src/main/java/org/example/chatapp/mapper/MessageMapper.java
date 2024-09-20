package org.example.chatapp.mapper;

import org.example.chatapp.dto.chat.message.MessageDTO;
import org.example.chatapp.model.Message;
import org.example.chatapp.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageMapper {


    public MessageDTO messageNotificationToMessageDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getContent(),
                message.getSender_id().getFirst_name(),
                message.getSender_id().getLast_name(),
                message.getSender_id().getId(),
                message.getSentAt(),
                message.getNotification_type(),
                message.getGroup().getId()
                );
    }

    public MessageDTO messageFileToMessageDTO(Message message, User user) {
        return new MessageDTO(
                message.getId(),
                message.getContent(),
                message.isImage(),
                user.getFirst_name(),
                user.getLast_name(),
                user.getId(),
                message.getSentAt(),
                message.getDeleted(),
                List.of(),
                message.getGroup().getId(),
                null);
    }

    public MessageDTO messageToMessageDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getContent(),
                message.isImage(),
                message.getSender_id().getFirst_name(),
                message.getSender_id().getLast_name(),
                message.getSender_id().getId(),
                message.getSentAt(),
                message.getResponse_id() == null ? null : message.getResponse_id().getId(),
                message.getResponse_id() == null ? null : message.getResponse_id().getSender_id().getFirst_name(),
                message.getResponse_id() == null ? null : message.getResponse_id().getSender_id().getLast_name(),
                message.getResponse_id() == null ? null : message.getResponse_id().getSender_id().getId(),
                message.getResponse_id() == null ? null : message.getResponse_id().getSentAt(),
                message.getResponse_id() == null ? null : message.getResponse_id().getContent(),
                message.getResponse_id() == null ? null : message.getResponse_id().getDeleted(),
                message.getDeleted(),
                List.of(),
                message.getGroup().getId(),
                message.getNotification_type());
    }
}
