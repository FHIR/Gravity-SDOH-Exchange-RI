package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.service.ConvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Mykhailo Stefantsiv
 */
@RestController
@RequestMapping("administration")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(tags = {SpringFoxConfig.ADMINISTRATION_API_TAG})
public class AdministrationController {

  private final ConvertService convertService;

  @PostMapping("/$extract")
  @ApiOperation(value = "Converts QuestionnaireResponse resource to Observation and Conditions",
      notes = "Converts QuestionnaireResponse resource to Observation and Conditions based on StructureMap, and "
          + "returns resulted transaction bundle.")
  public Map<String, Object> convert(@RequestBody Map<String, Object> questionnaireResponse) throws Exception {
    return convertService.convert(new JSONObject(questionnaireResponse));
  }
}
