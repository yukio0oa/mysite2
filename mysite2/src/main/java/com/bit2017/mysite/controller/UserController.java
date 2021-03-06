package com.bit2017.mysite.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bit2017.mysite.dto.JSONResult;
import com.bit2017.mysite.service.UserService;
import com.bit2017.mysite.vo.UserVo;
import com.bit2017.security.Auth;
import com.bit2017.security.AuthUser;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@ResponseBody
	@RequestMapping("/checkemail")
	public JSONResult checkEmail(
			@RequestParam(value="email", required=true, defaultValue="") String email){
		
		boolean isExists = userService.exists(email);
		return JSONResult.success(isExists ? "exist" : "not exist");
	}
	
	@RequestMapping("/joinform")
	public String joinform(@ModelAttribute UserVo userVo) {
		return "user/joinform";
	}
	
	@RequestMapping("/join")
	public String join(@ModelAttribute @Valid UserVo userVo,
					   BindingResult result,
					   Model model) {
		if(result.hasErrors() == true) {
			List<ObjectError> list = result.getAllErrors();
			for(ObjectError error : list) {
				System.out.println(error);
			}
			model.addAllAttributes(result.getModel());
			return "/user/joinform";
		}
		
		//userService.join(userVo);
		return "redirect:/user/joinsuccess";
	}
	
	@RequestMapping("/joinsuccess")
	public String joinSuccess() {
		return "user/joinsuccess";
	}
	
	@RequestMapping("/loginform")
	public String loginform() {
		return "user/loginform";
	}
	
	@Auth
	@RequestMapping("/modifyform")
	public String modifyform(
			@AuthUser UserVo authUser,
			Model model) {
		UserVo userVo = userService.getUser(authUser.getNo());
		model.addAttribute("userVo", userVo);
		return "user/modifyform";
	}
	
	@Auth
	@RequestMapping("/modify")
	public String modify(
			@AuthUser UserVo authUser,
			@ModelAttribute UserVo userVo) {		
		userVo.setNo(authUser.getNo());
		
		userService.modify(userVo);

		authUser.setName(userVo.getName());
		return "redirect:/user/modifyform";
	}
}
