package com.julio_compressor.myblog.repository;

import com.julio_compressor.myblog.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    List<Author> findByFirstNameAndLastName(String firstName, String lastName);
    List<Author> findByFirstName(String firstName);
    List<Author> findByLastName(String lastName);
}
