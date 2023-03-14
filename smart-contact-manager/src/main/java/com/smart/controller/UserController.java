package com.smart.controller;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.helper.Message;
import com.smart.models.Contact;
import com.smart.models.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private UserRepository userRepository;


	// Method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model,Principal principal)
	{
		String userName = principal.getName();
//		System.out.println("username: " + userName);
		// Getting the user using username
		User user = userRepository.getUserByUserName(userName);
//		System.out.println("User: " + user);
		model.addAttribute("user",user);
	}

	// Dashboard home
	@RequestMapping({"/index", "/"})
	public String dashboard(Model model, Principal principal)
	{
		model.addAttribute("title","User Dashboard");
		return "normal/user_dashboard";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model)
	{
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		return "normal/add_contact";
	}

	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute Contact contact,
								 BindingResult bindingResult,
								 @RequestParam(value="img",defaultValue = "default.png") MultipartFile file,
								 Principal principal, Model model,
								 HttpSession session)
	{
		try
		{
			if(bindingResult.hasErrors())
			{
				System.out.println(bindingResult);
				throw new Exception("Follow the Constraints");
			}
			if(file.isEmpty())
				contact.setImage("default.png");
			else
			{
				// Processing and uploading file
				contact.setImage(file.getOriginalFilename());
				File f = new ClassPathResource("static/img").getFile();
				Path p = Paths.get(f.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(),p, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploaded");
			}
			String name=principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			contact.setUser(user);

			user.getContacts().add(contact);
			this.userRepository.save(user);

			model.addAttribute("contact", new Contact()); // passes the new contact to display empty fields
			session.setAttribute("message", new Message("Successfully Added new Contact !!", "alert-success"));
			return "normal/add_contact";
		}
		catch (Exception e)
		{
			e.printStackTrace();
			model.addAttribute("contact",contact);	// Since error has occurred we pass the old user object back to retrieve the old values
			session.setAttribute("message", new Message("Something went wrong !!" + e.getMessage(), "alert-danger"));
			return "normal/add_contact";
		}
	}

	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model model,Principal principal)
	{
		model.addAttribute("title","Show User Contacts");
		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);

		Pageable pageable = PageRequest.of(page, 3);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",contacts.getTotalPages());

		return "normal/show_contacts";
	}

	// Show particular Contact
	@GetMapping("/contact/{cId}")
	public String showContactDetail(@PathVariable("cId") Integer cId,Model model,Principal principal)
	{
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		String username = principal.getName();
		User user = userRepository.getUserByUserName(username);

		if(user.getId() == contact.getUser().getId())
			model.addAttribute("contact",contact);

		return "normal/contact_detail";
	}

	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId,Model model,Principal principal,HttpSession session)
	{
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		String username = principal.getName();
		User user = userRepository.getUserByUserName(username);

		if(user.getId() == contact.getUser().getId())
		{
			contact.setUser(null);	// Unlinking the contact from user as we have set cascading all in the user model
			this.contactRepository.delete(contact);
			session.setAttribute("message",new Message("Contact Deleted Successfully","alert-success"));
		}

		return "redirect:/user/show-contacts/0";
	}

	@PostMapping("/update/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid,Model model)
	{
		model.addAttribute("title","Update Form");
		Contact contact = this.contactRepository.findById(cid).get();

		model.addAttribute("contact",contact);
		return "normal/update_form";
	}

	// Update contact handler
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,
								@RequestParam(value="img",defaultValue = "default.png") MultipartFile file,
								Model model,
								Principal principal,
								HttpSession session)
	{
		try{
			Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();

			if(!file.isEmpty())
			{
				// Delete old Photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile,oldContactDetail.getImage());
				file1.delete();

				// Update new photo
				File f = new ClassPathResource("static/img").getFile();
				Path p = Paths.get(f.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(),p, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			}
			else
			{
				contact.setImage(oldContactDetail.getImage());
			}

			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message",new Message("Your contact is updated...","alert-success"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("contact name: "+ contact.getName());
		System.out.println("contact name: "+ contact.getcId());
		return "redirect:/user/contact/"+contact.getcId();
	}
}
