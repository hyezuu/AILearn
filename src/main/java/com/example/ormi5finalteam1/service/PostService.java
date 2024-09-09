package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.post.dto.AdminPostListDto;
import com.example.ormi5finalteam1.domain.post.dto.PostDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    // 게시글 전체 조회 또는 키워드 검색
    public Page<PostDto> getAllPosts(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);

        // 키워드가 null 이거나 빈 문자열이면 전체 조회, 그렇지 않으면 검색
        if (keyword == null || keyword.trim().isEmpty()) {
            return postRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc(pageable)
                    .map(this::convertToDto);
        } else {
            return postRepository.findAllByTitleContainingAndDeletedAtIsNullOrderByCreatedAtDesc(keyword, pageable)
                    .map(this::convertToDto);
        }
    }

    // 게시글 상세 조회
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        if (post.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }
        post.increaseViewCount();
        postRepository.save(post);
        return convertToDto(post);
    }

    // 게시글 생성
    @Transactional
    public PostDto createPost(PostDto postDto, Provider provider) {
        User user = userService.getUser(provider.id());
        Post post = new Post(
                user,
                postDto.getTitle(),
                postDto.getContent()
        );
        postRepository.save(post);
        return convertToDto(post);
    }

    // 게시글 수정
    @Transactional
    public PostDto updatePost(Long id, PostDto postDto, Provider provider) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        if (!post.getUser().getId().equals(provider.id())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        post.update(postDto.getTitle(), postDto.getContent());
        postRepository.save(post);
        return convertToDto(post);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long id, Provider provider) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        if (!post.getUser().getId().equals(provider.id())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        post.delete();
        postRepository.save(post);
    }

    // 마이페이지 내 게시글 조회
    public Page<PostDto> getPostsByUserId(int page, int size, Provider provider) {
        Long userId = provider.id();
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId, pageable)
                .map(this::convertToDto);
    }

    public Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }

    // 관리자가 전체 게시글 조회 시 호출되는 메서드
    public Page<AdminPostListDto> getAllPostsByAdmin(Pageable pageable, String keyword) {

        // 키워드가 null 이거나 빈 문자열이면 전체 조회, 그렇지 않으면 검색
        if (keyword == null || keyword.trim().isEmpty()) {
            return postRepository.findAll(pageable).map(AdminPostListDto::toDto);
        } else {
            return postRepository.findAllByTitleContaining(keyword, pageable).map(AdminPostListDto::toDto);
        }
    }

    // 관리자가 게시글 상세 조회시 호출되는 메서드
    public Post getPostByAdmin(Long postId) {

        return postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }


    private PostDto convertToDto(Post post) {
        return new PostDto(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}