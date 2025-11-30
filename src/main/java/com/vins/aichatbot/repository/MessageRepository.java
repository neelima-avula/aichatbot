package com.vins.aichatbot.repository;

import com.vins.aichatbot.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> { }