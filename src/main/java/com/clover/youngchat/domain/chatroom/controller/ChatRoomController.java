package com.clover.youngchat.domain.chatroom.controller;

import com.clover.youngchat.domain.chatroom.dto.request.ChatRoomEditReq;
import com.clover.youngchat.domain.chatroom.dto.request.GroupChatRoomCreateReq;
import com.clover.youngchat.domain.chatroom.dto.request.PersonalChatRoomCreateReq;
import com.clover.youngchat.domain.chatroom.dto.response.ChatRoomAndLastChatGetRes;
import com.clover.youngchat.domain.chatroom.dto.response.ChatRoomDetailGetRes;
import com.clover.youngchat.domain.chatroom.dto.response.ChatRoomEditRes;
import com.clover.youngchat.domain.chatroom.dto.response.ChatRoomLeaveRes;
import com.clover.youngchat.domain.chatroom.dto.response.ChatRoomPaginationDetailGetRes;
import com.clover.youngchat.domain.chatroom.dto.response.GroupChatRoomCreateRes;
import com.clover.youngchat.domain.chatroom.dto.response.PersonalChatRoomCreateRes;
import com.clover.youngchat.domain.chatroom.service.command.ChatRoomCommandService;
import com.clover.youngchat.domain.chatroom.service.query.ChatRoomQueryService;
import com.clover.youngchat.global.response.RestResponse;
import com.clover.youngchat.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomQueryService chatRoomQueryService;
    private final ChatRoomCommandService chatRoomCommandService;

    @PostMapping("/personal")
    public RestResponse<PersonalChatRoomCreateRes> createPersonalChatRoom(
        @RequestBody PersonalChatRoomCreateReq req,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return RestResponse.success(
            chatRoomCommandService.createPersonalChatRoom(req, userDetails.getUser()));
    }

    @PostMapping("/group")
    public RestResponse<GroupChatRoomCreateRes> createGroupChatRoom(
        @RequestBody GroupChatRoomCreateReq req,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return RestResponse.success(
            chatRoomCommandService.createGroupChatRoom(req, userDetails.getUser()));
    }

    @GetMapping
    public RestResponse<Slice<ChatRoomAndLastChatGetRes>> getChatRoomList(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(required = false) Long cursorChatId) {
        return RestResponse.success(
            chatRoomQueryService.getChatRoomList(userDetails.getUser(), cursorChatId));
    }

    @GetMapping("/{chatRoomId}")
    public RestResponse<ChatRoomDetailGetRes> getDetailChatRoom(@PathVariable Long chatRoomId,
        @AuthenticationPrincipal UserDetailsImpl
            userDetails) {
        log.info("user: {}님이 채팅방 {} 에 들어갔습니다.", userDetails.getUser().getId(), chatRoomId);
        return RestResponse.success(
            chatRoomQueryService.getDetailChatRoom(chatRoomId, userDetails.getUser()));
    }

    @GetMapping("/slice/{chatRoomId}")
    public RestResponse<ChatRoomPaginationDetailGetRes> getPaginationDetailChatRoom(
        @PathVariable Long chatRoomId,
        @RequestParam(required = false) Long lastChatId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return RestResponse.success(
            chatRoomQueryService.getPaginationDetailChatRoom(chatRoomId, lastChatId,
                userDetails.getUser())
        );
    }

    @PatchMapping("/{chatRoomId}")
    public RestResponse<ChatRoomEditRes> editChatRoom(@PathVariable Long chatRoomId,
        @RequestBody @Valid ChatRoomEditReq req,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return RestResponse.success(
            chatRoomCommandService.editChatRoom(chatRoomId, req, userDetails.getUser()));
    }

    @DeleteMapping("/{chatRoomId}")
    public RestResponse<ChatRoomLeaveRes> leaveChatRoom(@PathVariable Long chatRoomId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return RestResponse.success(
            chatRoomCommandService.leaveChatRoom(chatRoomId, userDetails.getUser()));
    }
}
