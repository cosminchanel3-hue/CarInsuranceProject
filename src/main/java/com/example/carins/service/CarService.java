package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }

    public boolean isInsuranceValid(Long carId, LocalDate date) {
        if (carId == null || date == null) return false;
        // TODO: optionally throw NotFound if car does not exist
        if(!carRepository.existsById(carId)){
            throw new IllegalArgumentException("Car not found with id" + carId);
        }
        return policyRepository.existsActiveOnDate(carId, date);
    }

    public InsurancePolicy savePolicy(InsurancePolicy policy){
        if(policy.getEndDate() == null){
            throw new IllegalArgumentException("End date is required");
        }
        return policyRepository.save(policy);
    }

    public void fixOpenEndedPolicies() {
        policyRepository.findAll().stream()
                .filter(p -> p.getEndDate() == null)
                .forEach(p -> {
                    p.setEndDate(p.getStartDate().plusYears(1));
                    policyRepository.save(p);
                });
    }
}
