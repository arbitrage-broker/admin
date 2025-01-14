package com.arbitragebroker.admin.controller.impl;

import com.arbitragebroker.admin.enums.TransactionType;
import com.arbitragebroker.admin.filter.WalletFilter;
import com.arbitragebroker.admin.model.WalletModel;
import com.arbitragebroker.admin.service.WalletService;
import com.arbitragebroker.admin.validation.Create;
import com.arbitragebroker.admin.exception.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@Tag(name = "Wallet Rest Service v1")
@RequestMapping(value = "/api/v1/wallet")
public class WalletRestController extends BaseRestControllerImpl<WalletFilter, WalletModel, Long> {

    private WalletService walletService;

    public WalletRestController(WalletService service) {
        super(service);
        this.walletService = service;
    }
    @GetMapping("/total-balance")
    public ResponseEntity<BigDecimal> totalBalance(){
        return ResponseEntity.ok(walletService.totalBalance());
    }
    @GetMapping("/total-deposit")
    public ResponseEntity<BigDecimal> totalDeposit(){
        return ResponseEntity.ok(walletService.totalDeposit());
    }
    @GetMapping("/total-withdrawal")
    public ResponseEntity<BigDecimal> totalWithdrawal(){
        return ResponseEntity.ok(walletService.totalWithdrawal());
    }
    @GetMapping("/total-bonus")
    public ResponseEntity<BigDecimal> totalBonus(){
        return ResponseEntity.ok(walletService.totalBonus());
    }
    @GetMapping("/total-reward")
    public ResponseEntity<BigDecimal> totalReward(){
        return ResponseEntity.ok(walletService.totalReward());
    }
    @GetMapping("/get-date-range/{startDate}/{endDate}/{transactionType}")
    public ResponseEntity<Map<Long, BigDecimal>> findAllWithinDateRange(@PathVariable long startDate, @PathVariable long endDate, @PathVariable TransactionType transactionType){
        return ResponseEntity.ok(walletService.findAllWithinDateRange(startDate,endDate,transactionType));
    }
    @PostMapping("/admin")
    @ResponseBody
    @Operation(summary = "${api.baseRest.create}", description = "${api.baseRest.create.desc}")
    ResponseEntity<WalletModel> createForAdmin(@Validated(Create.class) @RequestBody WalletModel model){
        if (model.getId() != null) {
            throw new BadRequestException("The id must not be provided when creating a new record");
        }
        return ResponseEntity.ok(walletService.createFromAdmin(model));
    }
}
