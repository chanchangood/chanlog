package com.example.chanlog.repository;

import com.example.chanlog.domain.Board;
import com.example.chanlog.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>, PagingAndSortingRepository<Board, Long> {
    Page<Board> findByAuthor(User author, Pageable pageable);

    Page<Board> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
