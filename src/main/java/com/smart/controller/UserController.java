package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepo;
import com.smart.dao.UserRepo;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private ContactRepo contactRepo;
	
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
		model.addAttribute("title", "Dashboard");
		//get the user using (email)
		System.out.println("Normat ka dashboard");
		return "normal/user_dashboard";
	}
	
	//open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	 
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(
			@ModelAttribute Contact contact, 
			@RequestParam("profileImage") MultipartFile file, 
			Principal principal,HttpSession session) {
		
		try {
		String name = principal.getName();
		User user = this.userRepo.getUserByUserName(name);
		
		//processing and uploading file
		if(file.isEmpty()) {
			contact.setImage("contact.png");
		}
		else {
			contact.setImage(file.getOriginalFilename());
			
			File saveFile=new ClassPathResource("static/img").getFile();
			
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			System.out.println("Image is uploaded");
		}
		
		contact.setUser(user);
		
		user.getContacts().add(contact);
		
		
		
		this.userRepo.save(user);
		
		System.out.println("DATA "+contact);
		
		System.out.println("Added to data base");
		
		session.setAttribute("message", new Message("Your Contact is added add more", "success"));
		
		}catch(Exception e) {
			System.out.println("Error "+e.getMessage());
			e.printStackTrace();	
			
			session.setAttribute("message", new Message("somthing went wrong", "danger"));

			}
		return "normal/add_contact_form";
	}
	
//	show contacts
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model,Principal principal) {
		model.addAttribute("title", "Show Contacts");
		
		String userName = principal.getName();
		User user = this.userRepo.getUserByUserName(userName);
		
		Pageable pageable = PageRequest.of(page, 5);
		
		Page<Contact> contacts = this.contactRepo.findContactsByUser(user.getId(),pageable);
		
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/show_contacts";
	}
	
//	showing perticular conbtact detail
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model,Principal principal) {
		
		System.out.println("CiD "+cId);
		
		Optional<Contact> contactOptional = this.contactRepo.findById(cId);
		Contact contact = contactOptional.get();
		
		String userName = principal.getName();
		User user = this.userRepo.getUserByUserName(userName);
		
		if(user.getId()==contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}
		
		return "normal/contact_detail";
	}
	
//	delete contact handeler
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Model model,HttpSession session) {
		
		Contact contact = this.contactRepo.findById(cId).get();
		
		contact.setUser(null);
		
		this.contactRepo.delete(contact);
		
		session.setAttribute("message", new Message("Contact Deleted Successfully","success"));
		return "redirect:/user/show-contacts/0";
	}
	
	
	//update form handler
	
	public S
}
