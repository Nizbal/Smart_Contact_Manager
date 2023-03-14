package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.smart.dao.UserRepository;
import com.smart.helper.Message;
import com.smart.models.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
		
	@RequestMapping("/")
	public String home(Model model)
	{
		model.addAttribute("title","Home - Smart contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model)
	{
		model.addAttribute("title","About - Smart contact Manager");
		return "about";
	}
	
	@RequestMapping("/register")
	public String signup(Model model)
	{
		model.addAttribute("title","Register - Smart contact Manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
			@RequestParam(value="agreement",defaultValue = "false") boolean agreement,  
			Model model,HttpSession session)
	{
		try {
			if(!agreement)
			{
				System.out.println("You have not agreed T&C");
				throw new Exception("You have not agreed T&C");
			}
			if(bindingResult.hasErrors())
			{
				throw new Exception(" Follow the Constraints");
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			System.out.println("Agreement: "+ agreement);
			System.out.println("user: "+ user);
			User result = userRepository.save(user);	// Sends back a User object
			
			model.addAttribute("user", new User()); // passes the new user so as to display empty fields
			session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));
			return "signup";
		}
		catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);	// Since error has occurred we pass the old user object back to retrieve the old values
			session.setAttribute("message", new Message("Something went wrong !!" + e.getMessage(), "alert-danger"));
			return "signup";
		}		
	}

	@GetMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("title","About - Smart contact Manager");
		return "login";
	}
}