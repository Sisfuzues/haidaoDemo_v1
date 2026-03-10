package com.example_2.haidaodemo_v1.mapper;

import org.apache.ibatis.annotations.*;
import com.example_2.haidaodemo_v1.pojo.Post;

import java.util.List;

@Mapper
public interface PostMapper {
    @Select("select * from post")
    List<Post> selectAll();

    @Insert("insert into post (title,content,category,create_time)" +
            "values(#{title},#{content},#{category},#{createTime})")
    int insert(Post post);

    @Update("<script> "+
            "update post " +
            "<set> " +
            " <if test='title != null'>title = #{title},</if>" +
            " <if test='content != null'>content = #{content},</if>" +
            " <if test='category != null'>category = #{category},</if>" +
            " <if test='updateTime != null'>update_time = #{updateTime},</if>" +
            " <if test='imageUrl != null'>image_url = #{imageUrl},</if>" +
            " <if test='status != null'>status = #{status},</if>" +
            "</set>" +
            " where id = #{id}" +
            "</script>")
    int updatePost(Post post);

    @Delete("delete from post where id = #{id}")
    int deletePost(Integer id);

    @Select("select * from post where id = #{id}")
    Post findPostById(Integer id);
}
