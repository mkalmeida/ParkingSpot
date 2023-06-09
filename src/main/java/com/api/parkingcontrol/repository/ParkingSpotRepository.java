package com.api.parkingcontrol.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.parkingcontrol.model.ParkingSpotModel;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpotModel, UUID> {
	
	public boolean existsByParkingSpotNumber(String parkingSpotNumber);
	
	public boolean existsByLicensePlateCar(String parkingSpotNumber);
	
	public boolean existsByApartmentAndBlock(String apartment, String block);
	

}
