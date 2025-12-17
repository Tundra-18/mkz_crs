package com.example.sms.repositories;

import com.example.sms.models.MinMax;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MinMaxRepository {

    Double getMinValue();
    Double getMaxValue();
}
