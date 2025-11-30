package com.sernova.domain;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sernova.dto.PersonDto;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
	// fetch persons and their addresses in a single query to avoid N+1
	@Query("select distinct p from Person p left join fetch p.addresses")
	java.util.List<Person> findAllWithAddresses();
	
	@Query("""
	    select new com.sernova.dto.PersonDto(
	        p.id, p.firstName, p.lastName,
	        a
	    )
	    from Person p
	    left join p.addresses a
	""")
	List<PersonDto> findAllAsDto();
	
	@EntityGraph(attributePaths = "addresses")
	@Query("select p from Person p")
	List<Person> findAllWithAddressesGraph();
	
	@Cacheable("persons")
	@Query("select p from Person p join fetch p.addresses")
	List<Person> findAllWithAddressesCached();


}
