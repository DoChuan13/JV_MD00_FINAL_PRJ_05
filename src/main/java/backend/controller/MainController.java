package backend.controller;

import backend.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = {"/"})
@CrossOrigin(origins = "*")
public class MainController {
    @Autowired
    private IUserService userService;

    @GetMapping
    public ModelAndView showHomePage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("authentication --->" + authentication);
        ModelAndView view = new ModelAndView("index");
        userService.generateDefaultValueDatabase();
        return view;
    }

    /*@GetMapping("/user")
    public ModelAndView showUserPage() {
        return new ModelAndView("user/user");
    }

    @GetMapping("/admin")
    public ModelAndView showAdminPage() {
        return new ModelAndView("admin/admin");
    }

    @GetMapping("*")
    public ModelAndView pageNotFound() {
        return new ModelAndView("404/error.404");
    }*/
}
