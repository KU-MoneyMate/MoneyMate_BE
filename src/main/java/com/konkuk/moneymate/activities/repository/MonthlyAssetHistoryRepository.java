package com.konkuk.moneymate.activities.repository;

import com.konkuk.moneymate.activities.entity.MonthlyAssetHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MonthlyAssetHistoryRepository extends JpaRepository<MonthlyAssetHistory, UUID> {
}
