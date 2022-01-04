package com.project.app.service.impl;

import com.project.app.entity.Post;
import com.project.app.entity.Topic;
import com.project.app.exception.NotFoundException;
import com.project.app.repository.PostRepository;
import com.project.app.repository.TopicRepository;
import com.project.app.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class TopicServiceImpl implements TopicService {
    @Autowired
    private TopicRepository topicRepository;

    @Override
    public Topic getById(String id) {
        Optional<Topic> optionalTopic = topicRepository.findById(id);
        if ( optionalTopic.isPresent()){
            return  optionalTopic.get();
        } else {
            throw new NotFoundException(String.format("Topic with ID %s Not Found", id));
        }
    }

    @Override
    public List<Topic> getAll() {
        return topicRepository.findAll();
    }

    @Override
    public Topic create(Topic topic) {
        return topicRepository.save(topic);
    }

    @Override
    public Topic update(Topic topic) {
        Topic topicGet = getById(topic.getId());
        topic.setPosts(topicGet.getPosts());
        return topicRepository.save(topic);
    }

    @Override
    @Transactional
    public String deleteById(String id) {
        Topic topic = getById(id);
        topicRepository.delete(topic);
        return String.format("Topic with ID %s has been deleted", id);
    }

}
