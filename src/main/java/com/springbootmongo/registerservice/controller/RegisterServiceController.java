package com.springbootmongo.registerservice.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springbootmongo.registerservice.model.Instance;
import com.springbootmongo.registerservice.model.ServiceRegistry;
import com.springbootmongo.registerservice.repository.ServiceRegistryRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/serviceregistry/v1")
@Api(value = "Register/deregister service")
public class RegisterServiceController {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	private ServiceRegistryRepository serviceRegistryRepository;

	public RegisterServiceController(ServiceRegistryRepository serviceRegistryRepository) {
		this.serviceRegistryRepository = serviceRegistryRepository;
	}

	@ApiOperation(value = "Add details to register a service")
	@PostMapping("/register")
	public ResponseEntity<ServiceRegistry> addNewInstance(@RequestBody ServiceRegistry service) {
		LOG.info("Saving service details.");
		try {
			ServiceRegistry serviceRegistry = serviceRegistryRepository.save(service);
			return new ResponseEntity<>(serviceRegistry, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation(value = "View details of specific service")
	@GetMapping("/{name}")
	public ResponseEntity<ServiceRegistry> getServiceDetails(@PathVariable String name) {
		LOG.info("Getting service with name: {}.", name);

		Optional<ServiceRegistry> serviceData = serviceRegistryRepository.findById(name);
		if (serviceData.isPresent()) {
			return new ResponseEntity<>(serviceData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation(value = "View the list of services registered")
	@GetMapping("/all")
	public ResponseEntity<List<ServiceRegistry>> getAllServices() {
		LOG.info("Getting all instances.");
		try {
			List<ServiceRegistry> list = serviceRegistryRepository.findAll();
			if (list.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(list, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation(value = "Update details of service")
	@PutMapping("/update/{name}")
	public ResponseEntity<ServiceRegistry> updateService(@PathVariable("name") String name,
			@RequestBody ServiceRegistry service) {
		Optional<ServiceRegistry> serviceData = serviceRegistryRepository.findById(name);

		if (serviceData.isPresent()) {
			ServiceRegistry serviceDetails = serviceData.get();
			serviceDetails.setEnabled(service.getEnabled());
			serviceDetails.setName(service.getName());
			serviceDetails.setInstance(service.getInstance());
			return new ResponseEntity<>(serviceRegistryRepository.save(serviceDetails), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation(value = "Deregister a service")
	@DeleteMapping("/deregister/{name}")
	public ResponseEntity<HttpStatus> deleteServiceByName(@PathVariable("name") String name) {
		try {
			serviceRegistryRepository.deleteById(name);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation(value = "Deregister instance of a service")
	@DeleteMapping("/deregister/{name}/instance/{instanceId}")
	public ResponseEntity<HttpStatus> deleteInstanceOfService(@PathVariable("name") String name,
			@PathVariable("instanceId") String instanceId) {
		try {
			Optional<ServiceRegistry> serviceData = serviceRegistryRepository.findById(name);
			if (serviceData.isPresent()) {
				ServiceRegistry serviceDetails = serviceData.get();
				List<Instance> list = serviceDetails.getInstance();
				Iterator<Instance> it = list.iterator();
				while(it.hasNext()) {
					Instance instance = it.next();
					if (instanceId.equalsIgnoreCase(instance.getInstanceId())) {
						it.remove();
					}
				}
				serviceRegistryRepository.save(serviceDetails);
			}
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
