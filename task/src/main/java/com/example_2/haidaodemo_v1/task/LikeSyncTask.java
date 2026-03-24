package com.example_2.haidaodemo_v1.task;

import com.example_2.haidaodemo_v1.service.CommentService;
import com.example_2.haidaodemo_v1.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Set;

import static com.example_2.haidaodemo_v1.constant.RedisConstants.COMMENT_LIKE_CHANGE_KEY;

@Slf4j
@Component
public class LikeSyncTask {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Scheduled(cron = "0/30 * * * * ?")
    private void syncLikeToDatabase() {
        log.info("[定时任务] 开始扫描 Redis 中变动的点赞数据");

        Set<String> changedCommentIds = stringRedisTemplate.opsForSet()
                .members(COMMENT_LIKE_CHANGE_KEY);

        if (changedCommentIds == null || changedCommentIds.isEmpty()) {
            log.info("[定时任务] 本次无数据需要同步");
            return;
        }

        for(String idStr : changedCommentIds){
            try{
                Long id = Long.valueOf(idStr);
                String key = COMMENT_LIKE_CHANGE_KEY + id;

                Long currentLikes = stringRedisTemplate.opsForSet().size(key);
                commentService.update()
                        .set("likes",currentLikes)
                        .eq("id", id)
                        .update();

                stringRedisTemplate.opsForSet().remove(key);

                log.info("[定时任务] 同步成功：评论ID {}, 最新点赞数 {}",id,currentLikes);
            } catch (Exception e){
                log.error("[定时任务] 同步评论 ID {} 时发生异常:",idStr,e);
            }
        }
        log.info("[定时任务] 本次同步结束");
    }
}
