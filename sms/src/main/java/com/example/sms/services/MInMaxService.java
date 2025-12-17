package com.example.sms.services;

import com.example.sms.models.MinMax;
import com.example.sms.repositories.MaximumRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.util.Optional;

@Service
public class MInMaxService {

    @Autowired
    private MaximumRespository maximumRespository;

    public boolean deleteIfExists(Long id) {
        Optional<MinMax> minMaxOptional = maximumRespository.findById(id);
        if (minMaxOptional.isPresent()) {
            maximumRespository.deleteById(id);
            return true; // Row existed and was deleted
        }
        return false; // Row did not exist
    }

    public Long save(MinMax minMax){
        long count = maximumRespository.count();
        if(count !=0){
            maximumRespository.deleteAll();
        }
        maximumRespository.save(minMax);
        return minMax.getId();
    }

}
