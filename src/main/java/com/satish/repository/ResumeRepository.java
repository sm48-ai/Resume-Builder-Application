package com.satish.repository;

import com.satish.document.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResumeRepository extends MongoRepository<Resume,String> {
}
