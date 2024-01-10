package com.clover.youngchat.domain.chatroom.service;


import com.clover.youngchat.domain.chatroom.dto.request.ChatRoomCreateReq;
import com.clover.youngchat.domain.chatroom.dto.response.ChatRoomCreateRes;
import com.clover.youngchat.domain.chatroom.entity.ChatRoom;
import com.clover.youngchat.domain.chatroom.entity.ChatUser;
import com.clover.youngchat.domain.chatroom.repository.ChatRoomRepository;
import com.clover.youngchat.domain.chatroom.repository.ChatUserRepository;
import com.clover.youngchat.domain.user.entity.User;
import com.clover.youngchat.domain.user.repository.UserRepository;
import com.clover.youngchat.global.exception.GlobalException;
import com.clover.youngchat.global.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatUserRepository chatUserRepository;
    private final UserRepository userRepository;

    public ChatRoomCreateRes createChatRoom(ChatRoomCreateReq req, User user) {
        User friend = userRepository.findById(req.getFriendId()).orElseThrow(() ->
            new GlobalException(ResultCode.NOT_FOUND_USER));

        ChatRoom chatRoom = ChatRoom.builder()
            .title(req.getTitle())
            .build();

        chatRoomRepository.save(chatRoom);

        ChatUser myChat = ChatUser.builder()
            .user(user)
            .chatRoom(chatRoom)
            .build();

        chatUserRepository.save(myChat);

        ChatUser friendChat = ChatUser.builder()
            .user(friend)
            .chatRoom(chatRoom)
            .build();

        chatUserRepository.save(friendChat);
        
        return new ChatRoomCreateRes();
    }
}
