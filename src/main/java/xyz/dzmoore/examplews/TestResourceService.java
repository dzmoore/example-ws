package xyz.dzmoore.examplews;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TestResourceService {
  private Map<Long, TestResource> testResourceMap = new ConcurrentHashMap<>();
 
  public TestResource create(TestResource testResource) {
    return testResourceMap.computeIfAbsent(testResource.getId(), k -> testResource);
  }

  public Collection<TestResource> get() {
    return testResourceMap.values();
  }

  public Optional<TestResource> get(Long id) {
    return Optional.ofNullable(testResourceMap.get(id));
  }

  public Pair<Boolean, TestResource> update(Long id, TestResource testResource) {
    AtomicBoolean isNew = new AtomicBoolean(false);
    testResourceMap
      .compute(id, (k, v) -> {
        if (v == null) {
          isNew.set(true);
        }

        testResource.setId(id);

        return testResource;
      });

    return Pair.of(isNew.get(),testResource);
  }

  public boolean delete(Long id) {
    return testResourceMap.remove(id) != null;
  }


}
