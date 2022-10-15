package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepo;
import com.smart.entities.Contact;
import com.smart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepo userRepo;
	
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		
		String userName =principal.getName();
		System.out.println("USERNAME "+userName);
		
		User user=userRepo.getUserByUserName(userName);
		System.out.println("USER " +user);
		
		model.addAttribute("user", user);
		
	}
	
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
	
		//get the user using username(email)
		return "normal/user_dashboard";
	}
	
	//open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
}
