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

    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Board> latestBoards = boardService.findLatestBoards(pageable);

        model.addAttribute("latestBoards", latestBoards);

        return "home";
    }


    @GetMapping("/userBlog/{username}")
    public String userBlog(Model model,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "5") int size,
                           @PathVariable String username) {

        User user = userRepository.findByUsername(username);

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Board> boards = boardService.findPaginatedByUser(pageable, user);

        model.addAttribute("boards", boards);
        model.addAttribute("currentPage", page);
        model.addAttribute("user", user);


        return "userBlog";
    }

    @GetMapping("/boards/board/{id}")
    public String boardView(@PathVariable Long id, Model model) {
        Board board = boardService.findById(id);
        model.addAttribute("board", board);

        return "boards/board";
    }

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
//
//    @GetMapping("/deleteForm/{id}")
//    public String deleteForm(@PathVariable Long id, Model model) {
//        Board board = boardService.findById(id);
//        model.addAttribute("board", board);
//        return "deleteForm";
//    }
//
//    public boolean verifyPassword(@PathVariable Long id, @PathVariable String password) {
//        Board board = boardService.findById(id);
//        return password.equals(board.getPassword());
//    }
//
//    @PostMapping("/deleteForm")
//    public String deleteBoard(@ModelAttribute Board board, Model model, RedirectAttributes redirectAttributes) {
//        if (!verifyPassword(board.getId(), board.getPassword())) {
//            model.addAttribute("message", "잘못된 비밀번호 입니다.");
//            return "deleteForm";
//        }
//        boardService.deleteById(board.getId());
//        redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 삭제되었습니다");
//        return "redirect:/list";
//    }
//
//    @GetMapping("/updateForm/{id}")
//    public String updateForm(@PathVariable Long id, Model model) {
//        Board board = boardService.findById(id);
//        model.addAttribute("board", board);
//        return "/updateForm";
//    }
//
//    @PostMapping("/updateForm")
//    public String updateBoard(@ModelAttribute Board board, Model model) {
//        if (!verifyPassword(board.getId(), board.getPassword())) {
//            model.addAttribute("message", "잘못된 비밀번호 입니다.");
//            return "updateForm";
//        }
//
//        long id = board.getId();
//        boardService.saveBoard(board);
//        return "redirect:/view/" + id;
//    }


}
