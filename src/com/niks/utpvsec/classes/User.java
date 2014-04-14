package com.niks.utpvsec.classes;

import java.io.Serializable;


public class User implements Serializable {
	private static final long serialVersionUID = -2211148249841037064L;
	public String name;
	public String id;

	public User(String name, String id) {
		super();
		this.name = name;
		this.id = id;
	}

	public User() {
		super();
		this.name = "akmal";
		this.id = "13097";
	}
}
