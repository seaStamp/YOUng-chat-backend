package com.clover.youngchat.domain.chatRoom.service;

import static com.clover.youngchat.global.exception.ResultCode.ACCESS_DENY;
import static com.clover.youngchat.global.exception.ResultCode.NOT_FOUND_CHATROOM;
import static com.clover.youngchat.global.exception.ResultCode.NOT_FOUND_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import com.clover.youngchat.domain.chatroom.dto.request.ChatRoomCreateReq;
import com.clover.youngchat.domain.chatroom.dto.request.ChatRoomEditReq;
import com.clover.youngchat.domain.chatroom.entity.ChatRoom;
import com.clover.youngchat.domain.chatroom.repository.ChatRoomRepository;
import com.clover.youngchat.domain.chatroom.repository.ChatRoomUserRepository;
import com.clover.youngchat.domain.chatroom.service.ChatRoomService;
import com.clover.youngchat.domain.user.entity.User;
import com.clover.youngchat.domain.user.repository.UserRepository;
import com.clover.youngchat.global.exception.GlobalException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import test.ChatRoomTest;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest implements ChatRoomTest {

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRoomUserRepository chatRoomUserRepository;

    private ChatRoom chatRoom;
    private User user;

    @BeforeEach
    void setup() {
        chatRoom = ChatRoom.builder()
            .title(TEST_CHAT_ROOM_TITLE)
            .build();

        ReflectionTestUtils.setField(chatRoom, "id", TEST_CHAT_ROOM_ID);

        user = User.builder()
            .username(TEST_USER_NAME)
            .email(TEST_USER_EMAIL)
            .password(TEST_USER_PASSWORD)
            .profileImage(TEST_USER_PROFILE_IMAGE)
            .build();

        ReflectionTestUtils.setField(user, "id", TEST_USER_ID);
    }

    @Nested
    @DisplayName("채팅방 생성")
    class createChatRoom {

        @Test
        @DisplayName("채팅방 생성 성공")
        void createChatRoomSuccess() {
            ChatRoomCreateReq req = ChatRoomCreateReq.builder()
                .title(TEST_CHAT_ROOM_TITLE)
                .friendId(ANOTHER_TEST_USER_ID)
                .build();

            given(userRepository.findById(anyLong())).willReturn(Optional.of(TEST_USER));

            chatRoomService.createChatRoom(req, TEST_USER);

            verify(userRepository, times(1)).findById(anyLong());
            verify(chatRoomRepository, times(1)).save(any());
            verify(chatRoomUserRepository, times(2)).save(any());
        }

        @Test
        @DisplayName("채팅방 생성 실패 : 존재하지 않는 유저")
        void createChatRoomFail_NotFoundUser() {
            ChatRoomCreateReq req = ChatRoomCreateReq.builder()
                .title(TEST_CHAT_ROOM_TITLE)
                .friendId(ANOTHER_TEST_USER_ID)
                .build();

            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            GlobalException exception = assertThrows(GlobalException.class,
                () -> chatRoomService.createChatRoom(req, TEST_USER));

            assertThat(exception.getResultCode().getMessage()).isEqualTo(
                NOT_FOUND_USER.getMessage());
        }
    }

    @Nested
    @DisplayName("채팅방 나가기")
    class leaveChatRoom {
        @Test
        @DisplayName("성공")
        void leaveChatRoomSuccess(){
            given(chatRoomRepository.existsById(anyLong())).willReturn(true);
            given(chatRoomUserRepository.findByChatRoom_IdAndUser_Id(anyLong(), anyLong())).willReturn(Optional.of(TEST_CHAT_ROOM_USER));

            chatRoomService.leaveChatRoom(TEST_CHAT_ROOM_ID, user);

            verify(chatRoomRepository,times(1)).existsById(anyLong());
            verify(chatRoomUserRepository, times(1)).findByChatRoom_IdAndUser_Id(anyLong(),anyLong());
            verify(chatRoomUserRepository,times(1)).delete(any());
        }

        @Test
        @DisplayName("실패 : 존재하지 않는 채팅방")
        void leaveChatRoomFail_NotFoundChatRoom(){
            given(chatRoomRepository.existsById(anyLong())).willReturn(false);

            GlobalException exception = assertThrows(GlobalException.class,
                ()-> chatRoomService.leaveChatRoom(TEST_CHAT_ROOM_ID, user));

            assertThat(exception.getResultCode().getMessage()).isEqualTo(NOT_FOUND_CHATROOM.getMessage());
        }

        @Test
        @DisplayName("실패 : 채팅방에 속한 유저가 아닐 경우")
        void leaveChatRoomFail_AccessDeny(){
            given(chatRoomRepository.existsById(anyLong())).willReturn(true);
            given(chatRoomUserRepository.findByChatRoom_IdAndUser_Id(anyLong(), anyLong())).willReturn(Optional.empty());

            GlobalException exception = assertThrows(GlobalException.class,
                ()-> chatRoomService.leaveChatRoom(TEST_CHAT_ROOM_ID, user));

            assertThat(exception.getResultCode().getMessage()).isEqualTo(ACCESS_DENY.getMessage());
        }
    }

    @Nested
    @DisplayName("채팅방 수정")
    class editChatRoom {

        @Test
        @DisplayName("성공")
        void editChatRoomSuccess() {
            ChatRoomEditReq req = ChatRoomEditReq.builder()
                .title("채팅방 수정")
                .build();

            given(chatRoomRepository.findById(anyLong())).willReturn(Optional.of(chatRoom));
            given(chatRoomUserRepository.existsByChatRoom_IdAndUser_Id(anyLong(),
                anyLong())).willReturn(true);

            chatRoomService.editChatRoom(TEST_CHAT_ROOM_ID, req, user);

            assertThat(chatRoom.getTitle()).isEqualTo(req.getTitle());
        }

        @Test
        @DisplayName("실패 : 존재하지 않는 채팅방")
        void editChatRoomFail_NotFoundChatRoom() {
            ChatRoomEditReq req = ChatRoomEditReq.builder()
                .title("채팅방 수정")
                .build();

            given(chatRoomRepository.findById(anyLong())).willReturn(Optional.empty());

            GlobalException exception = assertThrows(GlobalException.class, () ->
                chatRoomService.editChatRoom(TEST_CHAT_ROOM_ID, req, user));

            assertThat(exception.getResultCode().getMessage()).isEqualTo(
                NOT_FOUND_CHATROOM.getMessage());
        }

        @Test
        @DisplayName("실패 : 채팅방의 멤버가 아닐 경우")
        void editChatRoomFail_AccessDeny() {
            ChatRoomEditReq req = ChatRoomEditReq.builder()
                .title("채팅방 수정")
                .build();

            given(chatRoomRepository.findById(anyLong())).willReturn(Optional.of(chatRoom));
            given(chatRoomUserRepository.existsByChatRoom_IdAndUser_Id(anyLong(),
                anyLong())).willReturn(false);

            GlobalException exception = assertThrows(GlobalException.class, () ->
                chatRoomService.editChatRoom(TEST_CHAT_ROOM_ID, req, user));

            assertThat(exception.getResultCode().getMessage()).isEqualTo(
                ACCESS_DENY.getMessage());
        }
    }
}
