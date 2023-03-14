package com.smart.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cId;
	@NotBlank(message = "Username cannot be empty..!")
	@Size(min=3,max=20,message = "UserName can have between 3-20 characters only..!")
	private String name;
	private String secondName;
	@NotBlank(message = "Work cannot be empty..!")
	private String work;
	@Column(unique = true)
	@NotBlank(message = "Email cannot be empty..!")
	@Email
	private String email;
	@Column(unique = true)
	@NotBlank(message = "Phone cannot be empty..!")
	@Size(min=10,max=10,message = "Must contain 10 digits!")
	private String phone;
	@JsonIgnore
	private String image;
	@Column(length = 500)
	private String description;
	@ManyToOne
	@JsonIgnore 	// to avoid circular dependency
	private User user;
	
	public int getcId() {
		return cId;
	}
	public void setcId(int cId) {
		this.cId = cId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSecondName() {
		return secondName;
	}
	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
	public String getWork() {
		return work;
	}
	public void setWork(String work) {
		this.work = work;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}