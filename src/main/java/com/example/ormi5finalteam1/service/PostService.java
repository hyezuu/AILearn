package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.post.dto.PostRequestDto;
import com.example.ormi5finalteam1.domain.post.dto.PostResponseDto;
import com.example.ormi5finalteam1.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.RequestEntity.post;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public PostResponseDto createPost(PostRequestDto postRequestDto) {
        Post post = new Post(postRequestDto.getUser(), postRequestDto.getTitle(), postRequestDto.getContent());
        postRepository.save(post);
        return convertToDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        return convertToDto(post);
    }

    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto postRequestDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        post(postRequestDto.getTitle(), postRequestDto.getContent());
        return convertToDto(post);
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        postRepository.deleteById(id);
    }

    private PostResponseDto convertToDto(Post post) {
        return new PostResponseDto(post.getId(), post.getUser(), post.getTitle(), post.getContent(), post.getViewCount());
    }
}