package com.example.chanlog.service;

import com.example.chanlog.domain.Board;
import com.example.chanlog.domain.User;
import com.example.chanlog.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    //자신의 게시글 페이징 보기
    @Transactional(readOnly = true)
    public Page<Board> findPaginatedByUser(Pageable pageable, User author) {
        Pageable sortedByDescId = PageRequest.of(pageable.getPageNumber(),
                pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));
        return boardRepository.findByAuthor(author ,sortedByDescId);
    }

    //최신 게시글 페이징 보기
    @Transactional(readOnly = true)
    public Page<Board> findLatestBoards(Pageable pageable) {
        return boardRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    //id에 해당하는 게시글 보기
    @Transactional(readOnly = true)
    public Board findById(Long id) {
        return boardRepository.findById(id).orElse(null);
    }

    //게시글 추가
    @Transactional
    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }

    //게시글 삭제
    @Transactional
    public void deleteById(Long id) {
        boardRepository.deleteById(id);
    }

}
