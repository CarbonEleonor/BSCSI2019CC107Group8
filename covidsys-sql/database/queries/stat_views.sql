use covidsys;


CREATE VIEW vaccinated_statistics AS
SELECT
    COUNT(
        CASE WHEN vaccinationStatus = "vaccinated"
            THEN 1 ELSE NULL END
    ) AS totalVaccinated,
    COUNT(
        CASE WHEN vaccinationStatus = "unvaccinated" 
            THEN 1 ELSE NULL END
    ) AS totalUnvaccinated,
    COUNT(
        CASE WHEN vaccinationStatus = "pending" 
            THEN 1 ELSE NULL END
    ) AS totalPending
FROM covidsys.user_vaccination;


CREATE VIEW health_statistics AS
SELECT
    COUNT(
        CASE WHEN healthStatus = "healthy" 
            THEN 1 ELSE NULL END
    ) AS totalHealthy,
    COUNT(
        CASE WHEN healthStatus = "risk" 
            THEN 1 ELSE NULL END
    ) AS totalRisk,
    COUNT(
        CASE WHEN healthStatus = "pending" 
            THEN 1 ELSE NULL END
    ) AS totalPending
FROM covidsys.user_health;
