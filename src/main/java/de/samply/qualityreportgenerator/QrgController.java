package de.samply.qualityreportgenerator;

import de.samply.qualityreportgenerator.utils.ProjectVersion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QrgController {

  private final String projectVersion = ProjectVersion.getProjectVersion();

  //@CrossOrigin(origins = "${CROSS_ORIGINS}", allowedHeaders = {"Authorization"})
  @GetMapping(value = QrgConst.INFO)
  public ResponseEntity<String> info() {
    return new ResponseEntity<>(projectVersion, HttpStatus.OK);
  }

  @GetMapping(value = QrgConst.GENERATE)
  public ResponseEntity<String> generate() {

    return new ResponseEntity<>("Hello World!", HttpStatus.OK);
  }


}
