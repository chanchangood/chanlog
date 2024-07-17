package com.example.chanlog.controller;

import com.example.chanlog.domain.SocialLoginInfo;
import com.example.chanlog.domain.User;
import com.example.chanlog.service.SocialLoginInfoService;
import com.example.chanlog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SocialLoginInfoService socialLoginInfoService;


    @GetMapping("/mypage")
    public String mypage() {
        return "/mypage";
    }

    @GetMapping("/userregform")
    public String userRegForm() {
        return "users/userregform";
    }

    @PostMapping("/userreg")
    public String userreg(@ModelAttribute("user") @Valid User user, BindingResult result) {
        // 유효성 검사에서 오류가 있을 경우, 회원가입 폼 다시 보여주기
        if (result.hasErrors()) {
            return "userregform";
        }

        // 이미 존재하는 사용자 이름 확인
        User byUsername = userService.findByUsername(user.getUsername());
        if (byUsername != null) {
            result.rejectValue("username", null, "이미 사용중인 아이디입니다.");
            return "users/userregerror";// 이미 사용 중인 아이디 오류 페이지 보여주기
        }

        // 이미 존재하는 이메일 확인
        User byEmail = userService.findByEmail(user.getEmail());
        if (byEmail != null) {
            result.rejectValue("email", null, "이미 사용중인 이메일입니다.");
            return "users/userregerror";// 이미 사용 중인 이메일 오류 페이지 보여주기
        }

        // 회원 가입 로직 처리
        userService.registUser(user);
        //회원 가입 되면 welcome페이지로 이동
        return "redirect:/welcome";
    }

    @GetMapping("/loginform")
    public String loginform() {
        return "users/loginform";
    }


    @GetMapping("/welcome")
    public String welcome() {
        return "users/welcome";
    }

    //oauth로그인
    @GetMapping("/registerSocialUser")
    public String registerSocialUser(@RequestParam("provider") String provider,
                                     @RequestParam("socialId") String socialId,
                                     @RequestParam("name") String name,
                                     @RequestParam("uuid") String uuid,
                                     Model model) {
        model.addAttribute("provider", provider);
        model.addAttribute("socialId", socialId);
        model.addAttribute("name", name);
        model.addAttribute("uuid", uuid);
        return "users/registerSocialUser";
    }

    @PostMapping("/saveSocialUser")
    public String saveSocialUser(@RequestParam("provider") String provider,
                                 @RequestParam("socialId") String socialId,
                                 @RequestParam("name") String name,
                                 @RequestParam("username") String username,
                                 @RequestParam("email") String email,
                                 @RequestParam("uuid") String uuid, Model model) {
        Optional<SocialLoginInfo> socialLoginInfoOptional =
                socialLoginInfoService.findByProviderAndUuidAndSocialId(provider, uuid, socialId);
        if (socialLoginInfoOptional.isPresent()) {
            SocialLoginInfo socialLoginInfo = socialLoginInfoOptional.get();
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(socialLoginInfo.getCreatedAt(), now);
            if (duration.toMinutes() > 20) {
                return "redirect:/error"; // 20분 이상 경과한 경우 에러 페이지로 리다이렉트
            }
            // 유효한 경우 User 정보를 저장합니다.
            userService.saveUser(username, name, email, socialId, provider);
            return "redirect:/";
        } else {
            return "redirect:/error"; // 해당 정보가 없는 경우 에러 페이지로 리다이렉트
        }
    }


}
