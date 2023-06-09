package com.api.parkingcontrol.controller;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.parkingcontrol.dtos.ParkingSpotDto;
import com.api.parkingcontrol.model.ParkingSpotModel;
import com.api.parkingcontrol.service.ParkingSpotService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {
	
	@Autowired
	ParkingSpotService parkingSpotService;
	
	@PostMapping 
	/*Object: teremos diferentes tipos de retorno de acordo comas verificações*/
	/*@Valid faz com que as anotações do DTO funcionem*/
	public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto parkingSpotDto) {
		/*validar se a vaga está vazia*/
		if (parkingSpotService.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already in use");
		}
		/*validar se quela placa de carro tem algum registro*/
		if (parkingSpotService.existsByLicensePlateCar(parkingSpotDto.getLicensePlateCar())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate is already in use");
		}
		/*validar se para aquele apartamento+bloco já tem registro (cada condômino tem apenas 1 vaga)*/
		if (parkingSpotService.existsByApartmentAndBlock(parkingSpotDto.getApartment(), parkingSpotDto.getBlock() )) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot already registered for this apartment/block!");
		}

		/*O DTO faz a validação das entradas, mas devo converter para model antes de salvar no banco de dados*/
		/*var identifica que é d tipo ParkingSpotModel*/
		var parkingSpotModel = new ParkingSpotModel();
		BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
		/*setando a registrationDate*/
		parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
		return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
	}
	
	//COM PAGINAÇÃO
	@GetMapping
	//No PageableDefault inserimos dados default caso o cliente não os insira
	public ResponseEntity<Page<ParkingSpotModel>> gelAllParkingSpots(@PageableDefault(page=0, size=10, sort="id", direction = Sort.Direction.ASC) org.springframework.data.domain.Pageable pageable){
		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll(pageable));
	}
	
	
	
	
	//SEM PAGINAÇÃO
	/*@GetMapping
	//Enviaremos uma lista das vagas de estacionamento e se não tiver nenhuma registrada a lista vai vazia
	public ResponseEntity<List<ParkingSpotModel>> gelAllParkingSpots(){
		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll());
	}*/
	
	@GetMapping("/{id}")
	/*Buscar Prking Spot por ID */
	public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value="id") UUID id){
		Optional <ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findByID(id);
		if(!parkingSpotModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
		}
		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value="id") UUID id){
		Optional <ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findByID(id);
		if(!parkingSpotModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
		}
		parkingSpotService.delete(parkingSpotModelOptional.get());
		return ResponseEntity.status(HttpStatus.OK).body("Parking Spot Deleted sucessfuly");
	}
	
	@PutMapping("/put/{id}")
	public ResponseEntity<Object> updateParkingSpot(@PathVariable(value="id") UUID id, @RequestBody @Valid ParkingSpotDto parkingSpotDto) {
		Optional <ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findByID(id);
		if(!parkingSpotModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
		}
		/*Estamos convertendo DTA para model*/
		//var parkingSpotModel  = parkingSpotModelOptional.get();
		/*seto cada um dos campos que podem ter sido atualizados (todos menos ID e Data de Registro)*/
		//parkingSpotModel.setParkingSpotNumber(parkingSpotDto.getParkingSpotNumber());
		//parkingSpotModel.setLicensePlateCar(parkingSpotDto.getLicensePlateCar());
		//parkingSpotModel.setModelCar(parkingSpotDto.getModelCar());
		//parkingSpotModel.setBrandCar(parkingSpotDto.getBrandCar());
		//parkingSpotModel.setColorCar(parkingSpotDto.getColorCar());
		//parkingSpotModel.setResponsibleName(parkingSpotDto.getResponsibleName());
		//parkingSpotModel.setApartment(parkingSpotDto.getApartment());
		//parkingSpotModel.setBlock(parkingSpotDto.getBlock());
		//return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
		
		/*Forma 2*/
		var parkingSpotModel = new ParkingSpotModel();
		BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
		/*No lugar de passar um por um que PODERIAM ser alterados, também posso passar apenas os 2 que não serão alterados pelo usuário*/
		parkingSpotModel.setId(parkingSpotModelOptional.get().getId());
		parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());
		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
	}
}
