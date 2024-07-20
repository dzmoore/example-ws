package xyz.dzmoore.examplews;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TestResourceStandardController.class)
class TestResourceStandardControllerTest {
  public static final TestResource TEST_RESOURCE =
    TestResource
      .builder()
      .id(123L)
      .name("Example")
      .password("abc123")
      .build();
  public static final String TEST_RESOURCES_API_ENDPOINT = "/standard/test-resources";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TestResourceService mockedTestResourceService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testResourceShouldReturnOkAfterCreate() throws Exception {
    when(mockedTestResourceService.create(eq(TEST_RESOURCE)))
      .thenReturn(TEST_RESOURCE);

    mockMvc
      .perform(
        post(TEST_RESOURCES_API_ENDPOINT)
          .content("{ \"id\":123, \"name\":\"Example\", \"password\":\"abc123\" }") /* password is write-only */
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(TEST_RESOURCE)));
  }

  @Test
  void testResourceShouldReturnBadRequestAfterCreate() throws Exception {
    when(mockedTestResourceService.create(eq(TEST_RESOURCE)))
      .thenReturn(TEST_RESOURCE);

    mockMvc
      .perform(
        post(TEST_RESOURCES_API_ENDPOINT)
          .content("{ \"nm\":\"Example\", \"pword\":\"abc123\" }")
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isBadRequest());
  }

  @Test
  void testResourceShouldReturnCreatedAfterUpdate() throws Exception {
    when(mockedTestResourceService.update(eq(TEST_RESOURCE.getId()), eq(TEST_RESOURCE)))
      .thenReturn(Pair.of(true, TEST_RESOURCE));

    mockMvc
      .perform(
        put(TEST_RESOURCES_API_ENDPOINT + "/{id}", TEST_RESOURCE.getId())
          .content("{ \"id\":123, \"name\":\"Example\", \"password\":\"abc123\" }") /* password is write-only */
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(header().string("Location", String.valueOf(TEST_RESOURCE.getId())));
  }

  @Test
  void testResourceShouldReturnNoContentAfterUpdate() throws Exception {
    when(mockedTestResourceService.update(eq(TEST_RESOURCE.getId()), eq(TEST_RESOURCE)))
      .thenReturn(Pair.of(false, TEST_RESOURCE));

    mockMvc
      .perform(
        put(TEST_RESOURCES_API_ENDPOINT + "/{id}", TEST_RESOURCE.getId())
          .content("{ \"id\":123, \"name\":\"Example\", \"password\":\"abc123\" }") /* password is write-only */
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isNoContent());
  }

  @Test
  void testResourceShouldReturnEmptyArrayAfterGet() throws Exception {
    mockMvc
      .perform(get(TEST_RESOURCES_API_ENDPOINT))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json("[]"));
  }

  @Test
  void testResourceShouldReturnNonEmptyArrayAfterGet() throws Exception {
    when(mockedTestResourceService.get())
      .thenReturn(List.of(TEST_RESOURCE));

    mockMvc
      .perform(get(TEST_RESOURCES_API_ENDPOINT))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(List.of(TEST_RESOURCE))));
  }

  @Test
  void testResourceShouldReturnNoContentAfterDelete() throws Exception {
    when(mockedTestResourceService.delete(eq(TEST_RESOURCE.getId()))).thenReturn(true);

    mockMvc
      .perform(delete(TEST_RESOURCES_API_ENDPOINT + "/{id}", TEST_RESOURCE.getId()))
      .andDo(print())
      .andExpect(status().isNoContent());
  }

  @Test
  void testResourceShouldReturnNotFoundAfterDelete() throws Exception {
    mockMvc
      .perform(delete(TEST_RESOURCES_API_ENDPOINT + "/{id}", TEST_RESOURCE.getId()))
      .andDo(print())
      .andExpect(status().isNotFound());
  }
}
