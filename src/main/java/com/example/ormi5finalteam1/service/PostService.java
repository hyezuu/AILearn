package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.post.dto.PostDto;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Page<PostDto> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc(pageable)
                .map(this::convertToDto);
    }

    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Post getPostById(Long id) {
        Post newPost = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        return newPost;
    }

    @Transactional
    public PostDto createPost(PostDto postDto) {
        Post post = new Post(
                new User(postDto.getUserId()),
                postDto.getTitle(),
                postDto.getContent()
        );
        postRepository.save(post);
        return convertToDto(post);
    }

    @Transactional
    public PostDto updatePost(Long id, PostDto postDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        post.update(postDto.getTitle(), postDto.getContent());
        return convertToDto(post);
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
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