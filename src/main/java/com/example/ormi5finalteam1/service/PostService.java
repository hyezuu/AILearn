package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.post.dto.PostDto;
import com.example.ormi5finalteam1.domain.post.dto.PostRequestDto;
import com.example.ormi5finalteam1.domain.post.dto.PostResponseDto;
import com.example.ormi5finalteam1.domain.user.User;
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
        postRepository.delete(post);
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
//
//    private final PostRepository postRepository;
//
//    public PostService(PostRepository postRepository) {
//        this.postRepository = postRepository;
//    }
//
//    @Transactional
//    public PostResponseDto createPost(PostRequestDto postRequestDto) {
//        Post post = new Post(postRequestDto.getUserId(), postRequestDto.getTitle(), postRequestDto.getContent());
//        postRepository.save(post);
//        return convertToDto(post);
//    }
//
//    @Transactional(readOnly = true)
//    public List<PostResponseDto> getAllPosts() {
//        return postRepository.findAll().stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public PostResponseDto getPostById(Long id) {
//        Post post = postRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
//        return convertToDto(post);
//    }
//
//    @Transactional
//    public PostResponseDto updatePost(Long id, PostRequestDto postRequestDto) {
//        Post post = postRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
//        post(postRequestDto.getTitle(), postRequestDto.getContent());
//        return convertToDto(post);
//    }
//
//    @Transactional
//    public void deletePost(Long id) {
//        Post post = postRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
//        postRepository.delete(post);
//    }
//
//    private PostResponseDto convertToDto(Post post) {
//        return new PostResponseDto(post);
//    }
}