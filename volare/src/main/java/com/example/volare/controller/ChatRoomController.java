package com.example.volare.controller;

import com.example.volare.dto.ChatRoomDTO;
import com.example.volare.global.apiPayload.ApiResponse;
import com.example.volare.global.common.auth.model.AuthUser;
import com.example.volare.model.ChatRoomEntity;
import com.example.volare.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 채팅방 생성 요청
    @PostMapping("/{sbId}")
    public ApiResponse<ChatRoomDTO.ChatRoomResponseDto> createChatRoom(@PathVariable Long sbId, @AuthenticationPrincipal AuthUser authUser){
        ChatRoomEntity chatRoom = chatRoomService.createChatRoom(sbId, authUser.getUser());
        return ApiResponse.onSuccess(ChatRoomDTO.fromDTO(chatRoom));
    }


}

