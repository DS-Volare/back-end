package com.example.volare.service;

import com.example.volare.global.apiPayload.code.status.ErrorStatus;
import com.example.volare.global.apiPayload.exception.handler.GeneralHandler;
import com.example.volare.model.ChatRoomEntity;
import com.example.volare.model.StoryScript;
import com.example.volare.model.User;
import com.example.volare.repository.ChatRoomRepository;
import com.example.volare.repository.StoryScriptRepository;
import com.example.volare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final StoryScriptRepository storyScriptRepository;

    private final UserRepository userRepository;

    // 채팅방 생성
    @Transactional
    public String createChatRoom(Long sbId, User user){
        // 스토리보드 생성 확인 유효성  검사 진행
        StoryScript storyScript = storyScriptRepository.findById(sbId).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));

        // 유저 존재 확인 유효성
        User ChatUser = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new GeneralHandler(ErrorStatus._BAD_REQUEST));

        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .storyScript(storyScript)
                .user(ChatUser)
                .build();

        ChatRoomEntity save = chatRoomRepository.save(chatRoom);
        return save.getId();
    }
}
