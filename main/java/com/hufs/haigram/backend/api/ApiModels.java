package com.hufs.haigram.backend.api;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class ApiModels {

    private ApiModels() {
    }

    public record ErrorResponse(String message) {
    }

    public record LoginRequest(
        @Email(message = "올바른 이메일 형식이 아닙니다.") @NotBlank(message = "이메일을 입력해 주세요.") String email,
        @NotBlank(message = "비밀번호를 입력해 주세요.") String password
    ) {
    }

    public record SignupRequest(
        @Email(message = "올바른 이메일 형식이 아닙니다.") @NotBlank(message = "이메일을 입력해 주세요.") String email,
        @NotBlank(message = "사용자 이름을 입력해 주세요.") @Size(min = 3, max = 30, message = "사용자 이름은 3자 이상 30자 이하입니다.") String username,
        @NotBlank(message = "이름을 입력해 주세요.") @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하입니다.") String fullName,
        @NotBlank(message = "비밀번호를 입력해 주세요.") @Size(min = 8, max = 72, message = "비밀번호는 8자 이상이어야 합니다.") String password,
        @Size(max = 160, message = "소개는 160자 이하로 입력해 주세요.") String bio
    ) {
    }

    public record CreatePostRequest(
        @Size(max = 2200, message = "캡션은 2200자 이하로 입력해 주세요.") String caption,
        @Size(max = 120, message = "위치는 120자 이하로 입력해 주세요.") String location,
        List<UploadMediaRequest> media
    ) {
    }

    public record UploadMediaRequest(
        @NotBlank(message = "미디어 타입이 필요합니다.") String type,
        @NotBlank(message = "파일 이름이 필요합니다.") String name,
        @NotBlank(message = "파일 데이터가 필요합니다.") String dataUrl
    ) {
    }

    public record CommentRequest(
        @NotBlank(message = "댓글 내용을 입력해 주세요.") @Size(max = 280, message = "댓글은 280자 이하로 입력해 주세요.") String text
    ) {
    }

    public record StartConversationRequest(
        @NotBlank(message = "대화 상대를 선택해 주세요.") String targetUserId
    ) {
    }

    public record MessageRequest(
        @NotBlank(message = "메시지 내용을 입력해 주세요.") @Size(max = 500, message = "메시지는 500자 이하로 입력해 주세요.") String text
    ) {
    }

    public record AuthResponse(String token, AppSnapshotResponse snapshot) {
    }

    public record AppSnapshotResponse(
        ViewerResponse viewer,
        List<UserCardResponse> stories,
        List<UserCardResponse> suggestions,
        List<PostResponse> feed,
        List<PostResponse> explore,
        List<PostResponse> reels,
        List<ActivityResponse> activities,
        List<ConversationResponse> conversations
    ) {
    }

    public record ViewerResponse(
        String id,
        String email,
        String username,
        String fullName,
        String bio,
        String initials,
        String accent,
        boolean followedByViewer,
        List<String> followingIds,
        List<String> likedPostIds,
        List<String> bookmarkedPostIds,
        long followerCount,
        long followingCount,
        long postCount
    ) {
    }

    public record UserCardResponse(
        String id,
        String username,
        String fullName,
        String bio,
        String initials,
        String accent,
        boolean followedByViewer
    ) {
    }

    public record MediaResponse(
        String id,
        String type,
        String src,
        String posterUrl,
        String alt
    ) {
    }

    public record CommentResponse(
        String id,
        UserCardResponse author,
        String text,
        Instant createdAt
    ) {
    }

    public record PostResponse(
        String id,
        UserCardResponse author,
        String caption,
        String location,
        Instant createdAt,
        List<MediaResponse> media,
        boolean likedByViewer,
        boolean bookmarkedByViewer,
        int likeCount,
        int commentCount,
        List<CommentResponse> comments
    ) {
    }

    public record ActivityResponse(
        String id,
        String type,
        String text,
        Instant createdAt
    ) {
    }

    public record MessageResponse(
        String id,
        String senderId,
        String text,
        Instant createdAt
    ) {
    }

    public record ConversationResponse(
        String id,
        UserCardResponse counterpart,
        List<MessageResponse> messages,
        Instant updatedAt
    ) {
    }
}
