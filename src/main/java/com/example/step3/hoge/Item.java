package com.example.step3.hoge;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Item {

	final String name;

	final long id;

	@JsonCreator
	public Item(@JsonProperty("name") String name, @JsonProperty("id") long id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public long getId() {
		return id;
	}
}
