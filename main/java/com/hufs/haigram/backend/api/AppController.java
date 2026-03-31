package com.hufs.haigram.backend.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hufs.haigram.backend.service.DemoSocialService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api")
public class AppController {

    private final DemoSocialService socialService;

    public AppController(DemoSocialService socialService) {
        this.socialService = socialService;
    }

    @GetMapping("/app/bootstrap")
    public ApiModels.AppSnapshotResponse bootstrap(@RequestHeader("Authorization") String authorizationHeader) {
        return socialService.bootstrap(ApiSecuritySupport.extractBearerToken(authorizationHeader));
    }

    @PostMapping("/posts")
    public ApiModels.AppSnapshotResponse createPost(
        @RequestHeader("Authorization") String authorizationHeader,
        @Valid @RequestBody ApiModels.CreatePostRequest request
    ) {
        return socialService.createPost(ApiSecuritySupport.extractBearerToken(authorizationHeader), request);
    }

    @PostMapping("/posts/{postId}/likes")
    public ApiModels.AppSnapshotResponse toggleLike(
        @RequestHeader("Authorization") String authorizationHeader,
        @PathVariable String postId
    ) {
        return socialService.toggleLike(ApiSecuritySupport.extractBearerToken(authorizationHeader), postId);
    }

    @PostMapping("/posts/{postId}/bookmarks")
    public ApiModels.AppSnapshotResponse toggleBookmark(
        @RequestHeader("Authorization") String authorizationHeader,
        @PathVariable String postId
    ) {
        return socialService.toggleBookmark(ApiSecuritySupport.extractBearerToken(authorizationHeader), postId);
    }

    @PostMapping("/posts/{postId}/comments")
    public ApiModels.AppSnapshotResponse addComment(
        @RequestHeader("Authorization") String authorizationHeader,
        @PathVariable String postId,
        @Valid @RequestBody ApiModels.CommentRequest request
    ) {
        return socialService.addComment(ApiSecuritySupport.extractBearerToken(authorizationHeader), postId, request);
    }

    @PostMapping("/users/{userId}/follow")
    public ApiModels.AppSnapshotResponse toggleFollow(
        @RequestHeader("Authorization") String authorizationHeader,
        @PathVariable String userId
    ) {
        return socialService.toggleFollow(ApiSecuritySupport.extractBearerToken(authorizationHeader), userId);
    }

    @PostMapping("/conversations")
    public ApiModels.AppSnapshotResponse startConversation(
        @RequestHeader("Authorization") String authorizationHeader,
        @Valid @RequestBody ApiModels.StartConversationRequest request
    ) {
        return socialService.startConversation(ApiSecuritySupport.extractBearerToken(authorizationHeader), request);
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public ApiModels.AppSnapshotResponse sendMessage(
        @RequestHeader("Authorization") String authorizationHeader,
        @PathVariable String conversationId,
        @Valid @RequestBody ApiModels.MessageRequest request
    ) {
        return socialService.sendMessage(ApiSecuritySupport.extractBearerToken(authorizationHeader), conversationId, request);
    }
}
