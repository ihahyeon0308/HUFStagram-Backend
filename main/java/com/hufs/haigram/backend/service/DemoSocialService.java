package com.hufs.haigram.backend.service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.hufs.haigram.backend.api.ApiModels;
import com.hufs.haigram.backend.domain.ActivityEntity;
import com.hufs.haigram.backend.domain.BookmarkEntity;
import com.hufs.haigram.backend.domain.ConversationEntity;
import com.hufs.haigram.backend.domain.ConversationParticipantEntity;
import com.hufs.haigram.backend.domain.FollowRelationEntity;
import com.hufs.haigram.backend.domain.MessageEntity;
import com.hufs.haigram.backend.domain.PostCommentEntity;
import com.hufs.haigram.backend.domain.PostEntity;
import com.hufs.haigram.backend.domain.PostLikeEntity;
import com.hufs.haigram.backend.domain.PostMediaEntity;
import com.hufs.haigram.backend.domain.SessionEntity;
import com.hufs.haigram.backend.domain.UserEntity;
import com.hufs.haigram.backend.repository.ActivityRepository;
import com.hufs.haigram.backend.repository.BookmarkRepository;
import com.hufs.haigram.backend.repository.ConversationParticipantRepository;
import com.hufs.haigram.backend.repository.ConversationRepository;
import com.hufs.haigram.backend.repository.FollowRelationRepository;
import com.hufs.haigram.backend.repository.MessageRepository;
import com.hufs.haigram.backend.repository.PostCommentRepository;
import com.hufs.haigram.backend.repository.PostLikeRepository;
import com.hufs.haigram.backend.repository.PostMediaRepository;
import com.hufs.haigram.backend.repository.PostRepository;
import com.hufs.haigram.backend.repository.SessionRepository;
import com.hufs.haigram.backend.repository.UserRepository;

@Service
public class DemoSocialService {

    private static final int MAX_MEDIA = 4;
    private static final int MAX_DATA_URL_LENGTH = 12_000_000;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecureRandom secureRandom = new SecureRandom();
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final FollowRelationRepository followRelationRepository;
    private final PostRepository postRepository;
    private final PostMediaRepository postMediaRepository;
    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PostCommentRepository postCommentRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository conversationParticipantRepository;
    private final MessageRepository messageRepository;
    private final ActivityRepository activityRepository;

    public DemoSocialService(
        UserRepository userRepository,
        SessionRepository sessionRepository,
        FollowRelationRepository followRelationRepository,
        PostRepository postRepository,
        PostMediaRepository postMediaRepository,
        PostLikeRepository postLikeRepository,
        BookmarkRepository bookmarkRepository,
        PostCommentRepository postCommentRepository,
        ConversationRepository conversationRepository,
        ConversationParticipantRepository conversationParticipantRepository,
        MessageRepository messageRepository,
        ActivityRepository activityRepository
    ) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.followRelationRepository = followRelationRepository;
        this.postRepository = postRepository;
        this.postMediaRepository = postMediaRepository;
        this.postLikeRepository = postLikeRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.postCommentRepository = postCommentRepository;
        this.conversationRepository = conversationRepository;
        this.conversationParticipantRepository = conversationParticipantRepository;
        this.messageRepository = messageRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional
    public ApiModels.AuthResponse login(ApiModels.LoginRequest request) {
        UserEntity user = userRepository.findByEmailIgnoreCase(request.email().trim())
            .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String token = issueToken();
        sessionRepository.save(new SessionEntity(token, user, Instant.now()));
        return new ApiModels.AuthResponse(token, buildSnapshot(user));
    }

    @Transactional
    public ApiModels.AuthResponse signup(ApiModels.SignupRequest request) {
        String email = normalizeEmail(request.email());
        String username = request.username().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        UserEntity user = new UserEntity(
            "user-" + UUID.randomUUID(),
            email,
            username,
            request.fullName().trim(),
            normalizeText(request.bio()),
            initialsFor(request.fullName()),
            nextAccent((int) userRepository.count()),
            passwordEncoder.encode(request.password()),
            Instant.now()
        );
        userRepository.save(user);
        addActivity(user, "system", "새 계정이 준비되었습니다. 첫 게시물을 올려 보세요.");

        String token = issueToken();
        sessionRepository.save(new SessionEntity(token, user, Instant.now()));
        return new ApiModels.AuthResponse(token, buildSnapshot(user));
    }

    @Transactional(readOnly = true)
    public ApiModels.AppSnapshotResponse bootstrap(String token) {
        return buildSnapshot(requireUserByToken(token));
    }

    @Transactional
    public ApiModels.AppSnapshotResponse createPost(String token, ApiModels.CreatePostRequest request) {
        UserEntity viewer = requireUserByToken(token);
        List<ApiModels.UploadMediaRequest> mediaRequests = request.media();

        if (mediaRequests == null || mediaRequests.isEmpty()) {
            throw new IllegalArgumentException("사진이나 영상을 하나 이상 추가해 주세요.");
        }
        if (mediaRequests.size() > MAX_MEDIA) {
            throw new IllegalArgumentException("미디어는 최대 4개까지만 업로드할 수 있습니다.");
        }
        long videoCount = mediaRequests.stream().filter(media -> "video".equalsIgnoreCase(media.type())).count();
        if (videoCount > 1) {
            throw new IllegalArgumentException("영상은 한 번에 1개만 업로드할 수 있습니다.");
        }

        PostEntity post = postRepository.save(new PostEntity(
            "post-" + UUID.randomUUID(),
            viewer,
            normalizeText(request.caption()),
            normalizeText(request.location()),
            Instant.now()
        ));

        for (int index = 0; index < mediaRequests.size(); index++) {
            ApiModels.UploadMediaRequest mediaRequest = mediaRequests.get(index);
            postMediaRepository.save(toMediaEntity(post, mediaRequest, index));
        }

        addActivity(viewer, "system", "새 게시물이 업로드되었습니다.");
        return buildSnapshot(viewer);
    }

    @Transactional
    public ApiModels.AppSnapshotResponse toggleLike(String token, String postId) {
        UserEntity viewer = requireUserByToken(token);
        PostEntity post = requirePost(postId);

        postLikeRepository.findByPost_IdAndUser_Id(postId, viewer.getId())
            .ifPresentOrElse(
                postLikeRepository::delete,
                () -> {
                    postLikeRepository.save(new PostLikeEntity(post, viewer, Instant.now()));
                    if (!post.getAuthor().getId().equals(viewer.getId())) {
                        addActivity(post.getAuthor(), "like", viewer.getUsername() + "님이 회원님의 게시물을 좋아합니다.");
                    }
                }
            );

        return buildSnapshot(viewer);
    }

    @Transactional
    public ApiModels.AppSnapshotResponse toggleBookmark(String token, String postId) {
        UserEntity viewer = requireUserByToken(token);
        PostEntity post = requirePost(postId);

        bookmarkRepository.findByUser_IdAndPost_Id(viewer.getId(), postId)
            .ifPresentOrElse(bookmarkRepository::delete, () -> bookmarkRepository.save(new BookmarkEntity(viewer, post, Instant.now())));

        return buildSnapshot(viewer);
    }

    @Transactional
    public ApiModels.AppSnapshotResponse addComment(String token, String postId, ApiModels.CommentRequest request) {
        UserEntity viewer = requireUserByToken(token);
        PostEntity post = requirePost(postId);

        postCommentRepository.save(new PostCommentEntity(
            "comment-" + UUID.randomUUID(),
            post,
            viewer,
            request.text().trim(),
            Instant.now()
        ));

        if (!post.getAuthor().getId().equals(viewer.getId())) {
            addActivity(post.getAuthor(), "comment", viewer.getUsername() + "님이 회원님의 게시물에 댓글을 남겼습니다.");
        }

        return buildSnapshot(viewer);
    }

    @Transactional
    public ApiModels.AppSnapshotResponse toggleFollow(String token, String userId) {
        UserEntity viewer = requireUserByToken(token);
        UserEntity target = requireUser(userId);

        if (viewer.getId().equals(target.getId())) {
            throw new IllegalArgumentException("자기 자신은 팔로우할 수 없습니다.");
        }

        followRelationRepository.findByFollower_IdAndFollowing_Id(viewer.getId(), target.getId())
            .ifPresentOrElse(
                followRelationRepository::delete,
                () -> {
                    followRelationRepository.save(new FollowRelationEntity(viewer, target, Instant.now()));
                    addActivity(target, "follow", viewer.getUsername() + "님이 회원님을 팔로우하기 시작했습니다.");
                }
            );

        return buildSnapshot(viewer);
    }

    @Transactional
    public ApiModels.AppSnapshotResponse startConversation(String token, ApiModels.StartConversationRequest request) {
        UserEntity viewer = requireUserByToken(token);
        UserEntity target = requireUser(request.targetUserId());

        List<ConversationEntity> sharedConversation = conversationRepository.findSharedConversation(viewer.getId(), target.getId());
        if (sharedConversation.isEmpty()) {
            ConversationEntity conversation = conversationRepository.save(new ConversationEntity(
                "conversation-" + UUID.randomUUID(),
                Instant.now()
            ));
            conversationParticipantRepository.save(new ConversationParticipantEntity(conversation, viewer));
            conversationParticipantRepository.save(new ConversationParticipantEntity(conversation, target));
        }

        return buildSnapshot(viewer);
    }

    @Transactional
    public ApiModels.AppSnapshotResponse sendMessage(String token, String conversationId, ApiModels.MessageRequest request) {
        UserEntity viewer = requireUserByToken(token);
        ConversationEntity conversation = requireConversation(conversationId);
        List<ConversationParticipantEntity> participants = conversationParticipantRepository.findAllByConversation_Id(conversationId);

        boolean allowed = participants.stream().anyMatch(participant -> participant.getUser().getId().equals(viewer.getId()));
        if (!allowed) {
            throw new IllegalStateException("해당 대화방에 접근할 수 없습니다.");
        }

        messageRepository.save(new MessageEntity(
            "message-" + UUID.randomUUID(),
            conversation,
            viewer,
            request.text().trim(),
            Instant.now()
        ));
        conversation.setUpdatedAt(Instant.now());
        conversationRepository.save(conversation);

        participants.stream()
            .map(ConversationParticipantEntity::getUser)
            .filter(user -> !user.getId().equals(viewer.getId()))
            .findFirst()
            .ifPresent(user -> addActivity(user, "system", viewer.getUsername() + "님이 새 메시지를 보냈습니다."));

        return buildSnapshot(viewer);
    }

    @Transactional
    public void seedIfEmpty() {
        if (userRepository.count() > 0) {
            return;
        }

        UserEntity demo = saveUser("user-demo", "demo@haigram.dev", "demo.creator", "HAIku Demo", "서울 캠퍼스와 일상을 기록하는 계정입니다.", 0, "demo1234");
        UserEntity seoul = saveUser("user-seoul", "seoul@haigram.dev", "seoul.frames", "Seoul Frames", "도시 감도와 릴스를 좋아하는 크리에이터", 1, "seoul1234");
        UserEntity study = saveUser("user-study", "study@haigram.dev", "campus.notes", "Campus Notes", "공부, 카페, 프로젝트 일상 기록", 2, "study1234");
        UserEntity travel = saveUser("user-travel", "travel@haigram.dev", "night.route", "Night Route", "야경과 산책 영상 아카이브", 3, "travel1234");
        UserEntity design = saveUser("user-design", "design@haigram.dev", "studio.summer", "Studio Summer", "브랜딩과 컬러 감도 큐레이션", 4, "design1234");

        followRelationRepository.save(new FollowRelationEntity(demo, seoul, Instant.now().minus(10, ChronoUnit.DAYS)));
        followRelationRepository.save(new FollowRelationEntity(demo, study, Instant.now().minus(9, ChronoUnit.DAYS)));
        followRelationRepository.save(new FollowRelationEntity(study, seoul, Instant.now().minus(8, ChronoUnit.DAYS)));
        followRelationRepository.save(new FollowRelationEntity(travel, seoul, Instant.now().minus(7, ChronoUnit.DAYS)));

        PostEntity postSeoul = saveSeedPost(seoul, "황금빛 해 질 무렵의 서울 스카이라인.", "성수", List.of(
            new ApiModels.UploadMediaRequest("image", "Seoul Skyline", scenicImage("Seoul Skyline", "#ff7b54", "#ff4d6d"))
        ));
        PostEntity postStudy = saveSeedPost(study, "카페에서 프로젝트 문서를 정리하는 오후.", "이문동", List.of(
            new ApiModels.UploadMediaRequest("image", "Project Notes", scenicImage("Project Notes", "#4f46e5", "#8b5cf6"))
        ));
        PostEntity postTravel = saveSeedPost(travel, "짧고 강한 야간 산책 릴스.", "한강공원", List.of(
            new ApiModels.UploadMediaRequest("video", "Night Route", "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4")
        ));
        PostEntity postDesign = saveSeedPost(design, "무드보드 톤앤매너 정리.", "연남동", List.of(
            new ApiModels.UploadMediaRequest("image", "Mood Board", scenicImage("Mood Board", "#f59e0b", "#ef4444"))
        ));
        PostEntity postDemo = saveSeedPost(demo, "HUFS 인스타 클론 리디자인 진행 중.", "HUFS", List.of(
            new ApiModels.UploadMediaRequest("image", "HAIku Build", scenicImage("HAIku Build", "#0ea5e9", "#2563eb"))
        ));

        postLikeRepository.save(new PostLikeEntity(postStudy, demo, Instant.now().minus(5, ChronoUnit.HOURS)));
        postLikeRepository.save(new PostLikeEntity(postDemo, seoul, Instant.now().minus(4, ChronoUnit.HOURS)));

        postCommentRepository.save(new PostCommentEntity("comment-" + UUID.randomUUID(), postSeoul, seoul, "오늘도 기록 완료.", Instant.now().minus(5, ChronoUnit.HOURS)));
        postCommentRepository.save(new PostCommentEntity("comment-" + UUID.randomUUID(), postStudy, study, "정리 끝.", Instant.now().minus(4, ChronoUnit.HOURS)));
        postCommentRepository.save(new PostCommentEntity("comment-" + UUID.randomUUID(), postTravel, travel, "릴스 업로드 완료.", Instant.now().minus(3, ChronoUnit.HOURS)));
        postCommentRepository.save(new PostCommentEntity("comment-" + UUID.randomUUID(), postDesign, design, "무드보드 갱신.", Instant.now().minus(2, ChronoUnit.HOURS)));

        ConversationEntity firstConversation = conversationRepository.save(new ConversationEntity("conversation-demo-seoul", Instant.now().minus(5, ChronoUnit.HOURS)));
        conversationParticipantRepository.save(new ConversationParticipantEntity(firstConversation, demo));
        conversationParticipantRepository.save(new ConversationParticipantEntity(firstConversation, seoul));
        messageRepository.save(new MessageEntity("message-1", firstConversation, seoul, "UI를 인스타그램 웹처럼 더 정리해 볼까요?", Instant.now().minus(6, ChronoUnit.HOURS)));
        messageRepository.save(new MessageEntity("message-2", firstConversation, demo, "좋아요. 로그인 화면과 좌측 내비 구조부터 맞추고 있어요.", Instant.now().minus(5, ChronoUnit.HOURS)));

        ConversationEntity secondConversation = conversationRepository.save(new ConversationEntity("conversation-demo-study", Instant.now().minus(2, ChronoUnit.HOURS)));
        conversationParticipantRepository.save(new ConversationParticipantEntity(secondConversation, demo));
        conversationParticipantRepository.save(new ConversationParticipantEntity(secondConversation, study));
        messageRepository.save(new MessageEntity("message-3", secondConversation, study, "project.md도 새 구조 기준으로 다시 써야겠네요.", Instant.now().minus(2, ChronoUnit.HOURS)));

        addActivity(demo, "system", "Spring + MySQL 백엔드가 준비되었습니다.");
        addActivity(demo, "follow", "seoul.frames님이 새 릴스를 업로드했습니다.");
    }

    @Transactional(readOnly = true)
    private ApiModels.AppSnapshotResponse buildSnapshot(UserEntity viewer) {
        Set<String> followingIds = followRelationRepository.findAllByFollower_Id(viewer.getId()).stream()
            .map(relation -> relation.getFollowing().getId())
            .collect(Collectors.toSet());
        Set<String> likedPostIds = postLikeRepository.findAllByUser_Id(viewer.getId()).stream()
            .map(like -> like.getPost().getId())
            .collect(Collectors.toSet());
        Set<String> bookmarkedPostIds = bookmarkRepository.findAllByUser_Id(viewer.getId()).stream()
            .map(bookmark -> bookmark.getPost().getId())
            .collect(Collectors.toSet());

        List<PostEntity> orderedPosts = postRepository.findAllByOrderByCreatedAtDesc();
        List<UserEntity> otherUsers = userRepository.findAll().stream()
            .filter(user -> !user.getId().equals(viewer.getId()))
            .sorted(Comparator.comparing(UserEntity::getCreatedAt).reversed())
            .toList();

        return new ApiModels.AppSnapshotResponse(
            toViewerResponse(viewer, followingIds, likedPostIds, bookmarkedPostIds),
            otherUsers.stream().limit(8).map(user -> toUserCard(viewer, user, followingIds)).toList(),
            otherUsers.stream().filter(user -> !followingIds.contains(user.getId())).limit(6).map(user -> toUserCard(viewer, user, followingIds)).toList(),
            orderedPosts.stream().map(post -> toPost(viewer, post, followingIds, likedPostIds, bookmarkedPostIds)).toList(),
            orderedPosts.stream()
                .sorted(Comparator.comparingInt((PostEntity post) -> postLikeRepository.findAllByPost_Id(post.getId()).size()).reversed())
                .map(post -> toPost(viewer, post, followingIds, likedPostIds, bookmarkedPostIds))
                .toList(),
            orderedPosts.stream()
                .filter(post -> postMediaRepository.findAllByPost_IdOrderByOrderIndexAsc(post.getId()).stream().anyMatch(media -> "video".equals(media.getType())))
                .map(post -> toPost(viewer, post, followingIds, likedPostIds, bookmarkedPostIds))
                .toList(),
            activityRepository.findAllByUser_IdOrderByCreatedAtDesc(viewer.getId()).stream()
                .map(activity -> new ApiModels.ActivityResponse(activity.getId(), activity.getType(), activity.getText(), activity.getCreatedAt()))
                .toList(),
            conversationRepository.findAllByParticipantUserIdOrderByUpdatedAtDesc(viewer.getId()).stream()
                .map(conversation -> toConversation(viewer, conversation, followingIds))
                .toList()
        );
    }

    private ApiModels.ViewerResponse toViewerResponse(UserEntity viewer, Set<String> followingIds, Set<String> likedPostIds, Set<String> bookmarkedPostIds) {
        return new ApiModels.ViewerResponse(
            viewer.getId(),
            viewer.getEmail(),
            viewer.getUsername(),
            viewer.getFullName(),
            viewer.getBio(),
            viewer.getInitials(),
            viewer.getAccent(),
            false,
            List.copyOf(followingIds),
            List.copyOf(likedPostIds),
            List.copyOf(bookmarkedPostIds),
            followRelationRepository.countByFollowing_Id(viewer.getId()),
            followingIds.size(),
            postRepository.countByAuthor_Id(viewer.getId())
        );
    }

    private ApiModels.UserCardResponse toUserCard(UserEntity viewer, UserEntity user, Set<String> followingIds) {
        return new ApiModels.UserCardResponse(
            user.getId(),
            user.getUsername(),
            user.getFullName(),
            user.getBio(),
            user.getInitials(),
            user.getAccent(),
            followingIds.contains(user.getId()) && !viewer.getId().equals(user.getId())
        );
    }

    private ApiModels.PostResponse toPost(UserEntity viewer, PostEntity post, Set<String> followingIds, Set<String> likedPostIds, Set<String> bookmarkedPostIds) {
        List<PostMediaEntity> media = postMediaRepository.findAllByPost_IdOrderByOrderIndexAsc(post.getId());
        List<PostCommentEntity> comments = postCommentRepository.findAllByPost_IdOrderByCreatedAtAsc(post.getId());
        int likeCount = postLikeRepository.findAllByPost_Id(post.getId()).size();

        return new ApiModels.PostResponse(
            post.getId(),
            toUserCard(viewer, post.getAuthor(), followingIds),
            post.getCaption(),
            post.getLocation(),
            post.getCreatedAt(),
            media.stream().map(item -> new ApiModels.MediaResponse(item.getId(), item.getType(), item.getSrc(), item.getPosterUrl(), item.getAltText())).toList(),
            likedPostIds.contains(post.getId()),
            bookmarkedPostIds.contains(post.getId()),
            likeCount,
            comments.size(),
            comments.stream()
                .map(comment -> new ApiModels.CommentResponse(comment.getId(), toUserCard(viewer, comment.getAuthor(), followingIds), comment.getText(), comment.getCreatedAt()))
                .toList()
        );
    }

    private ApiModels.ConversationResponse toConversation(UserEntity viewer, ConversationEntity conversation, Set<String> followingIds) {
        List<ConversationParticipantEntity> participants = conversationParticipantRepository.findAllByConversation_Id(conversation.getId());
        UserEntity counterpart = participants.stream()
            .map(ConversationParticipantEntity::getUser)
            .filter(user -> !user.getId().equals(viewer.getId()))
            .findFirst()
            .orElse(viewer);

        return new ApiModels.ConversationResponse(
            conversation.getId(),
            toUserCard(viewer, counterpart, followingIds),
            messageRepository.findAllByConversation_IdOrderByCreatedAtAsc(conversation.getId()).stream()
                .map(message -> new ApiModels.MessageResponse(message.getId(), message.getSender().getId(), message.getText(), message.getCreatedAt()))
                .toList(),
            conversation.getUpdatedAt()
        );
    }

    private PostMediaEntity toMediaEntity(PostEntity post, ApiModels.UploadMediaRequest request, int orderIndex) {
        String type = request.type().trim().toLowerCase();
        if (!List.of("image", "video").contains(type)) {
            throw new IllegalArgumentException("이미지 또는 동영상만 업로드할 수 있습니다.");
        }

        String dataUrl = request.dataUrl().trim();
        if (dataUrl.length() > MAX_DATA_URL_LENGTH || !dataUrl.startsWith("data:") && !dataUrl.startsWith("http")) {
            throw new IllegalArgumentException("업로드 데이터 형식이 올바르지 않습니다.");
        }

        String poster = "video".equals(type) ? scenicPoster(request.name()) : null;
        return new PostMediaEntity("media-" + UUID.randomUUID(), post, type, dataUrl, poster, request.name().trim(), orderIndex);
    }

    private UserEntity saveUser(String id, String email, String username, String fullName, String bio, int accentIndex, String password) {
        return userRepository.save(new UserEntity(
            id,
            email,
            username,
            fullName,
            bio,
            initialsFor(fullName),
            nextAccent(accentIndex),
            passwordEncoder.encode(password),
            Instant.now().minus(12, ChronoUnit.DAYS)
        ));
    }

    private PostEntity saveSeedPost(UserEntity author, String caption, String location, List<ApiModels.UploadMediaRequest> mediaRequests) {
        PostEntity post = postRepository.save(new PostEntity(
            "post-" + UUID.randomUUID(),
            author,
            caption,
            location,
            Instant.now().minus(postRepository.count() + 1, ChronoUnit.HOURS)
        ));
        for (int index = 0; index < mediaRequests.size(); index++) {
            postMediaRepository.save(toMediaEntity(post, mediaRequests.get(index), index));
        }
        return post;
    }

    private UserEntity requireUserByToken(String token) {
        SessionEntity session = sessionRepository.findById(token)
            .orElseThrow(() -> new IllegalArgumentException("세션이 만료되었거나 올바르지 않습니다."));
        return session.getUser();
    }

    private UserEntity requireUser(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private PostEntity requirePost(String postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
    }

    private ConversationEntity requireConversation(String conversationId) {
        return conversationRepository.findById(conversationId)
            .orElseThrow(() -> new IllegalArgumentException("대화방을 찾을 수 없습니다."));
    }

    private void addActivity(UserEntity user, String type, String text) {
        activityRepository.save(new ActivityEntity("activity-" + UUID.randomUUID(), user, type, text, Instant.now()));
    }

    private String issueToken() {
        byte[] bytes = new byte[24];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String normalizeEmail(String value) {
        return value.trim().toLowerCase();
    }

    private String normalizeText(String value) {
        return value == null ? "" : value.trim();
    }

    private String initialsFor(String name) {
        return name.trim().codePoints()
            .mapToObj(codePoint -> new String(Character.toChars(codePoint)))
            .filter(StringUtils::hasText)
            .limit(2)
            .collect(Collectors.joining())
            .toUpperCase();
    }

    private String nextAccent(int index) {
        List<String> accents = List.of(
            "linear-gradient(135deg, #ff7a59, #d6249f)",
            "linear-gradient(135deg, #f59e0b, #ef4444)",
            "linear-gradient(135deg, #0ea5e9, #2563eb)",
            "linear-gradient(135deg, #8b5cf6, #ec4899)",
            "linear-gradient(135deg, #14b8a6, #0f766e)"
        );
        return accents.get(Math.floorMod(index, accents.size()));
    }

    private String scenicImage(String title, String start, String end) {
        String svg = """
            <svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1080 1080'>
              <defs>
                <linearGradient id='g' x1='0' y1='0' x2='1' y2='1'>
                  <stop offset='0%%' stop-color='%s'/>
                  <stop offset='100%%' stop-color='%s'/>
                </linearGradient>
              </defs>
              <rect width='1080' height='1080' fill='url(#g)'/>
              <circle cx='860' cy='240' r='120' fill='rgba(255,255,255,0.22)'/>
              <rect x='96' y='650' width='888' height='220' rx='34' fill='rgba(15,23,42,0.18)'/>
              <text x='96' y='750' font-size='92' font-family='Segoe UI, sans-serif' fill='white'>%s</text>
              <text x='96' y='840' font-size='34' font-family='Segoe UI, sans-serif' fill='rgba(255,255,255,0.92)'>Instagram inspired backend seed</text>
            </svg>
            """.formatted(start, end, escapeXml(title));
        return dataUri(svg);
    }

    private String scenicPoster(String title) {
        return scenicImage(title, "#111827", "#334155");
    }

    private String dataUri(String value) {
        return "data:image/svg+xml;base64," + Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String escapeXml(String value) {
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;");
    }
}
