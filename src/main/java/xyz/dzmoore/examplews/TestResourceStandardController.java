package xyz.dzmoore.examplews;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/standard/test-resources")
@AllArgsConstructor
public class TestResourceStandardController {
  private TestResourceService testResourceService;

  @PostMapping
  public ResponseEntity<?> create(@RequestBody TestResource testResource) {
    final boolean invalid =
      Optional
        .ofNullable(testResource)
        .map(TestResource::getId)
        .isEmpty();

    if (invalid) {
      return ResponseEntity.badRequest().build();
    }

    return
      Optional
        .ofNullable(testResourceService.create(testResource))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.internalServerError().build());
  }

  @GetMapping
  public Collection<TestResource> get() {
    return testResourceService.get();
  }

  @GetMapping("/{id}")
  public ResponseEntity<TestResource> get(@PathVariable Long id) {
    return ResponseEntity.of(testResourceService.get(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<TestResource> update(@PathVariable Long id, @RequestBody TestResource testResource)
    throws URISyntaxException {
    if (testResourceService.update(id, testResource).getLeft()) {
      return ResponseEntity.created(new URI(String.valueOf(id))).build();
    }

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    if (testResourceService.delete(id)) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler
  public void handleException(Throwable t) {
    log.error(t.getMessage(), t);
  }
}
