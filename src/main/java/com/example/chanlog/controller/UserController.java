package com.example.chanlog.controller;

import com.example.chanlog.domain.SocialLoginInfo;
import com.example.chanlog.domain.User;
import com.example.chanlog.service.SocialLoginInfoService;
import com.example.chanlog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SocialLoginInfoService socialLoginInfoService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/mypage")
    public String mypage() {
        return "/mypage";
    }

    @GetMapping("/userregform")
    public String userRegForm() {
        return "users/userregform";
    }

    @PostMapping("/userreg")
    public String userreg(@ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "userregform";
        }
        User byUsername = userService.findByUsername(user.getUsername());
        if (byUsername != null) {
            result.rejectValue("username", null, "이미 사용중인 아이디입니다.");
            return "users/userregerror";
        }

        userService.registUser(user);
        return "redirect:/welcome";
    }

    @GetMapping("/loginform")
    public String loginform() {
        return "users/loginform";
    }

    @GetMapping("/loginerror")
    public String loginError() {
        return "users/loginerror";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "users/welcome";
    }

    //oauth로그인
    @GetMapping("/registerSocialUser")
    public String registerSocialUser(@RequestParam("provider") String provider, @RequestParam("socialId")
    String socialId, @RequestParam("name") String name, @RequestParam("uuid") String uuid, Model model) {
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
