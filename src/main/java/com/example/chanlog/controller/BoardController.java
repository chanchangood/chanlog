package com.example.chanlog.controller;

import com.example.chanlog.domain.Board;
import com.example.chanlog.domain.Role;
import com.example.chanlog.domain.User;
import com.example.chanlog.repository.UserRepository;
import com.example.chanlog.securiy.CustomUserDetails;
import com.example.chanlog.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final UserRepository userRepository;

//    홈페이지
    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "9") int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Board> latestBoards = boardService.findLatestBoards(pageable);

        model.addAttribute("latestBoards", latestBoards);

        return "home";
    }

//    개인 블로그
    @GetMapping("/userBlog/{username}")
    public String userBlog(Model model,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "9") int size,
                           @PathVariable String username) {

        User user = userRepository.findByUsername(username);

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Board> boards = boardService.findPaginatedByUser(pageable, user);

        model.addAttribute("boards", boards);
        model.addAttribute("currentPage", page);
        model.addAttribute("user", user);


        return "userBlog";
    }


//    게시글 상세 페이지
    @GetMapping("/boards/board/{id}")
    public String boardView(@PathVariable Long id, Model model) {
        Board board = boardService.findById(id);
        model.addAttribute("board", board);

        return "boards/board";
    }

//    게시글 작성 폼
    @GetMapping("/boards/postForm")
    public String postForm(Model model) {
        model.addAttribute("board", new Board());
        return "boards/postForm";
    }

    @PostMapping("/postForm")
    public String postBoard(
            @ModelAttribute Board board,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // Authentication에서 username 가져오기
        String username = authentication.getName();
        // UserRepository를 통해 User 객체 가져오기
        User currentUser = userRepository.findByUsername(username);


        if (currentUser == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        board.setAuthor(currentUser); // 현재 로그인된 사용자 설정

        boardService.saveBoard(board);

        redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 작성되었습니다.");
        return "redirect:/userBlog/" + currentUser.getUsername();
    }

//    게시글 삭제 폼
    @GetMapping("/boards/deleteForm/{id}")
    public String deleteForm(@PathVariable Long id, Model model) {
        Board board = boardService.findById(id);
        model.addAttribute("board", board);
        return "boards/deleteForm";
    }



    @PostMapping("/deleteForm")
    public String deleteBoard(@ModelAttribute Board board,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {

        String username = authentication.getName();
        Board currentBoard = boardService.findById(board.getId());
        if (!currentBoard.getAuthor().getUsername().equals(username)) {
            redirectAttributes.addFlashAttribute("error", "권한 없음 : 게시글 작성자가 아닙니다!");
            return "redirect:/boards/board/" + board.getId();
        }

        boardService.deleteById(board.getId());

        redirectAttributes.addFlashAttribute("success", "게시글이 정상적으로 삭제되었습니다.");
        return "redirect:/userBlog/" + username;
    }



//    게시글 수정 폼
    @GetMapping("/boards/updateForm/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        Board board = boardService.findById(id);
        model.addAttribute("board", board);
        return "boards/updateForm";
    }

    @PostMapping("/updateForm")
    public String updateBoard(@ModelAttribute Board board,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {

        String username = authentication.getName();
        Board currentBoard = boardService.findById(board.getId());
        if (!currentBoard.getAuthor().getUsername().equals(username)) {
            redirectAttributes.addFlashAttribute("error", "권한 없음 : 게시글 작성자가 아닙니다!");
            return "redirect:/boards/board/" + board.getId();
        }


        boardService.saveBoard(board);
        redirectAttributes.addFlashAttribute("success", "게시글이 성공적으로 업데이트되었습니다.");


        return "redirect:/boards/board/" + board.getId();
    }


}
