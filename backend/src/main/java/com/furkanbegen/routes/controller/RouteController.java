package com.furkanbegen.routes.controller;

import static com.furkanbegen.routes.constant.AppConstant.API_BASE_PATH;

import com.furkanbegen.routes.dto.RouteDTO;
import com.furkanbegen.routes.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API_BASE_PATH + "/routes")
@RequiredArgsConstructor
public class RouteController {

  private final RouteService routeService;

  @GetMapping
  public Page<RouteDTO> findRoutes(
      @PageableDefault Pageable pageable,
      @RequestParam(name = "fromLocationId") Long fromLocationId,
      @RequestParam(name = "toLocationId") Long toLocationId) {
    return routeService.findRoutes(fromLocationId, toLocationId, pageable);
  }
}
