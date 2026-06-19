package com.camping.pms.public_api;

import com.camping.pms.accommodations.AcsiService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/public/acsi")
public class AcsiController {

    private final AcsiService acsiService;

    public AcsiController(AcsiService acsiService) {
        this.acsiService = acsiService;
    }

    @GetMapping("/check")
    public Map<String, Object> checkEligibility(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        boolean eligible = acsiService.isEligible(start, end);
        BigDecimal acsiPrice = eligible ? acsiService.calculateAcsiPrice(start, end) : BigDecimal.ZERO;

        return Map.of(
            "eligible", eligible,
            "acsiPrice", acsiPrice
        );
    }
}