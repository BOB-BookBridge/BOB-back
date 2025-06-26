package com.bob.web.dummy.controller;

import com.bob.web.common.CommonResponse;
import com.bob.web.common.symbol.ResponseSymbol;
import com.bob.web.dummy.command.CreateDummyManagerCommand;
import com.bob.web.dummy.service.DummyService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dummy")
public class DummyController {

  private final DummyService dummyService;

  @PostMapping
  public CommonResponse<ResponseSymbol> handleCreateDummyManager(HttpServletResponse response) {
    CreateDummyManagerCommand command = new CreateDummyManagerCommand();
    dummyService.createDummyManagerProcess(command, response);
    return new CommonResponse<>(true, ResponseSymbol.OK);
  }
}
