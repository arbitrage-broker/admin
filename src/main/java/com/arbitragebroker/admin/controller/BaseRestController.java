package com.arbitragebroker.admin.controller;

import com.arbitragebroker.admin.model.PageModel;
import com.arbitragebroker.admin.model.Select2Model;
import com.arbitragebroker.admin.validation.Create;
import com.arbitragebroker.admin.validation.Update;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

@Validated
public interface BaseRestController<F, M, ID extends Serializable> {

    @GetMapping(value = {"/{id}"})
    @Operation(summary = "${api.baseRest.findById}", description = "${api.baseRest.findById.desc}")
    ResponseEntity<M> findById(@PathVariable("id") ID id);

//    @GetMapping(value = {"/history/{id}/{revisionId}"})
//    @Operation(summary = "${api.baseRest.findHistoryAtRevision}", description = "${api.baseRest.findById.desc}")
//    ResponseEntity<Revision<Long, M>> findHistoryAtRevision(@PathVariable ID id, @PathVariable Long revisionId);

    @GetMapping
    @Operation(summary = "${api.baseRest.findAll}", description = "${api.baseRest.findAll.desc}")
    ResponseEntity<Page<M>> findAll(F filter, @PageableDefault Pageable pageable);

    @GetMapping("/history/{id}")
    @Operation(summary = "${api.baseRest.findAllHistory}", description = "${api.baseRest.findAllHistory.desc}")
    ResponseEntity<Page<Revision<Long, M>>> findAllHistory(@PathVariable ID id, @PageableDefault Pageable pageable);

    @GetMapping(value = {"/findAllTable"})
    @Operation(summary = "${api.baseRest.findAllTable}", description = "${api.baseRest.findAll.desc}")
    ResponseEntity<PageModel<M>> findAllTable(F filter, @PageableDefault Pageable pageable);

    @GetMapping(value = {"/findAllSelect"})
    @Operation(summary = "${api.baseRest.findAllSelect}", description = "${api.baseRest.findAll.desc}")
    Page<Select2Model> findAllSelect(F filter, @RequestParam int page);

    @GetMapping(value = {"/countAll"})
    @Operation(summary = "${api.baseRest.countAll}", description = "${api.baseRest.countAll.desc}")
    ResponseEntity<Long> countAll(F filter);

    @GetMapping(value = {"/exists"})
    @Operation(summary = "${api.baseRest.exists}", description = "${api.baseRest.exists.desc}")
    ResponseEntity<Boolean> exists(F filter);

    @DeleteMapping(value = {"/{id}"})
    @Operation(summary = "${api.baseRest.deleteById}", description = "${api.baseRest.deleteById.desc}")
    ResponseEntity<Void> deleteById(@PathVariable("id") ID id);

    @PostMapping
    @ResponseBody
    @Operation(summary = "${api.baseRest.create}", description = "${api.baseRest.create.desc}")
    ResponseEntity<M> create(@Validated(Create.class) @RequestBody M model);

    @PatchMapping
    @ResponseBody
    @Operation(summary = "${api.baseRest.update}", description = "${api.baseRest.update.desc}")
    ResponseEntity<M> update(@Validated(Update.class) @RequestBody M model);
}
