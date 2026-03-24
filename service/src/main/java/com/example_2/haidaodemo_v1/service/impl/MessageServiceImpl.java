package com.example_2.haidaodemo_v1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example_2.haidaodemo_v1.mapper.MessageMapper;
import com.example_2.haidaodemo_v1.pojo.Message;
import com.example_2.haidaodemo_v1.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

}
