package com.example_2.haidaodemo_v1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example_2.haidaodemo_v1.VO.PostVO;
import com.example_2.haidaodemo_v1.pojo.Post;
import java.util.List;

/**
 * 🛰️ 文章服务
 * <hr/>
 * 🧩 职责：处理文章的展示，点赞等业务
 * 🛡️ 关联：继承iservice
 * 🗺️ 架构： (haidaoDemo_v1)
 *
 * @author Sisfuzues
 * @date 2026/3/16 21:03
 */
public interface PostService extends IService<Post> {

    PostVO findById(Long id);
    List<PostVO> findAll();
    List<Post> findAllPosts();
    int createPost(Post post);
    int updatePost(Post post);
    int deletePost(Long id);
    Post findPostById(Long id);
    void likePost(Long postId);
    Long getLikeCount(Long postId);
}
