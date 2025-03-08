package com.arbitragebroker.admin.controller;


import com.arbitragebroker.admin.config.MessageConfig;
import com.arbitragebroker.admin.enums.RoleType;
import com.arbitragebroker.admin.model.UserModel;
import com.arbitragebroker.admin.util.CustomObjectMapper;
import com.arbitragebroker.admin.util.SessionHolder;
import com.arbitragebroker.admin.service.UserService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@Controller
public class LoginController {

    final MessageConfig messages;
    final SessionHolder sessionHolder;
    final HttpServletRequest request;
    final UserService userService;
    final CustomObjectMapper objectMapper;

    public LoginController(MessageConfig messages, SessionHolder sessionHolder, HttpServletRequest request, UserService userService) {
        this.messages = messages;
        this.sessionHolder = sessionHolder;
        this.request = request;
        this.userService = userService;
        this.objectMapper = new CustomObjectMapper();
    }

    @SneakyThrows
    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public ModelAndView loadPage(@PathVariable String name) {
        if (name == null || name.isEmpty() || name.equals("favicon.ico"))
            return new ModelAndView("dashboard");

        UserModel user = sessionHolder.getCurrentUser();
        ModelAndView modelAndView = new ModelAndView(name);
        var userAsString = user != null ? objectMapper.writeValueAsString(user) : "{}";
        modelAndView.addObject("currentUser", userAsString);
        modelAndView.addObject("fullName", user.getFirstName() + " " + user.getLastName());
        modelAndView.addObject("pageTitle", messages.getMessage(name));
        modelAndView.addObject("errorMsg", null);
        return modelAndView;
    }

    @GetMapping(value = {"", "/"})
    public String index() {
        var requestWrapper = sessionHolder.getRequestWrapper();
        String targetUrl = "redirect:/dashboard";
        if (requestWrapper.isUserInRole(RoleType.USER))
            targetUrl = "/access-denied";
        return targetUrl;
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        request.getParameterMap().forEach((key, value) -> {
            modelAndView.addObject(key, value[0]);
        });
        return modelAndView;
    }

    @PostMapping("/registration")
    public String register(@Valid @ModelAttribute("userModel") UserModel userModel) {
        userService.register(userModel);
        return "redirect:/login#signin";
    }

}
