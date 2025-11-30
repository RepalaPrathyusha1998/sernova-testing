package com.sernova.web;

import com.sernova.domain.Address;
import com.sernova.domain.Person;
import com.sernova.domain.PersonRepository;
import com.sernova.dto.PersonDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final PersonRepository personRepository;

    public ApiController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello from Sernova test API");
    }

    @GetMapping("/persons")
    public ResponseEntity<List<Person>> persons() {
        return ResponseEntity.ok(personRepository.findAll());
    }
    
    @GetMapping("/persons-with-addresses-initial")
    public ResponseEntity<List<Person>> personsWithAddressesInitial() {
     return ResponseEntity.ok(personRepository.findAll());
    }

    @GetMapping("/persons-with-addresses")
    public ResponseEntity<List<Person>> personsWithAddresses() {
     // use repository method that performs a join-fetch to load addresses in one query
     return ResponseEntity.ok(personRepository.findAllWithAddresses());
    }
    
    @GetMapping("/persons-with-addresses-dto")
    public ResponseEntity<List<PersonDto>> personsWithAddressesDto() {
        return ResponseEntity.ok(personRepository.findAllAsDto());
    }
    
    @GetMapping("/persons-page")
    public ResponseEntity<Page<Person>> getPersonsPage(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "500") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(personRepository.findAll(pageable));
    }
    
    @GetMapping("/persons-graph")
    public ResponseEntity<List<Person>> getPersonsWithGraph() {
        return ResponseEntity.ok(personRepository.findAllWithAddressesGraph());
    }
    
    @GetMapping("/persons-with-cache")
    public ResponseEntity<List<Person>> getPersonsWithCache() {
        return ResponseEntity.ok(personRepository.findAllWithAddressesCached());
    }

    @PostMapping("/seed/people")
    public ResponseEntity<String> seedPeople() {
        // Insert 10,000 persons with 2 addresses each. Simple batching to reduce memory usage.
        int total = 10_000;
        int batchSize = 500;
        List<Person> batch = new ArrayList<>(batchSize);
        for (int i = 1; i <= total; i++) {
            Person p = new Person();
            p.setFirstName("First" + i);
            p.setLastName("Last" + i);

            Address home = new Address();
            home.setLine1("Home Street " + i);
            home.setCity("City" + (i % 100));
            home.setCountry("Country");
            home.setType("HOME");
            p.addAddress(home);

            Address work = new Address();
            work.setLine1("Work Ave " + i);
            work.setCity("City" + (i % 100));
            work.setCountry("Country");
            work.setType("WORK");
            p.addAddress(work);

            batch.add(p);

            if (batch.size() == batchSize) {
                personRepository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            personRepository.saveAll(batch);
        }
        return ResponseEntity.ok("Seeded " + total + " persons with 2 addresses each");
    }
}
