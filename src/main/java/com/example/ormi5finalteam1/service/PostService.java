package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.post.dto.PostDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public Page<PostDto> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc(pageable)
                .map(this::convertToDto);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }

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

    private PostDto convertToDto(Post post) {
        return new PostDto(
                post.getId(),
                post.getUser().getId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount()
        );
    }
}