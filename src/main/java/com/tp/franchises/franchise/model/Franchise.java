package com.tp.franchises.franchise.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "franchise")
@Getter
@Setter
@NoArgsConstructor
public class Franchise {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 100)
	private String name;

	@OneToMany(mappedBy = "franchise", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Branch> branches = new ArrayList<>();

	public Franchise(String name) {
		this.name = name;
	}

}
