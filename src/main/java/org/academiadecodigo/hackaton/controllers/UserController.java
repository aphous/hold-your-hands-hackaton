package org.academiadecodigo.hackaton.controllers;

import org.academiadecodigo.hackaton.commands.UserDto;
import org.academiadecodigo.hackaton.converter.UserDtoToUser;
import org.academiadecodigo.hackaton.converter.UserToUserDto;
import org.academiadecodigo.hackaton.persistence.model.User;
import org.academiadecodigo.hackaton.services.AuthService;
import org.academiadecodigo.hackaton.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    private AuthService authService;
    private UserService userService;

    private UserToUserDto userToUserDto;
    private UserDtoToUser userDtoToUser;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Autowired
    public void setUserToUserDto(UserToUserDto userToUserDto) {
        this.userToUserDto = userToUserDto;
    }

    @Autowired
    public void setUserDtoToUser(UserDtoToUser userDtoToUser) {
        this.userDtoToUser = userDtoToUser;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/show/{id}")
    public String showUser(Model model, @PathVariable Integer id) {
        UserDto user = userToUserDto.convert(userService.get(id));
        model.addAttribute("user", user);
        return "user/show";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/show/{id}/edit")
    public String editUser(Model model, @PathVariable Integer id) {
        UserDto user = userToUserDto.convert(userService.get(id));
        model.addAttribute("user", user);
        return"user/show-edit";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/edit/save", params = "action=save")
    public String editSave(@Valid @ModelAttribute("user") UserDto user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()){
            return "user/show-edit";
        }

        userService.delete(user.getId());
        User savedUser = userService.add(userDtoToUser.convert(user));

        authService.setAccessingUser(savedUser);
        redirectAttributes.addFlashAttribute("lastAction", "Saved " + savedUser.getName() + " ID: " + savedUser.getId());
        return "redirect:/user/show/" + savedUser.getId();

    }

    @RequestMapping(method = RequestMethod.POST, path = "/edit/save", params = "action=cancel")
    public String editCancel() {
        return "redirect:/user/show/" + authService.getAccessingUser().getId();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/addPacket")
    public String addPacote(Model model){
        return "";
    }
}
