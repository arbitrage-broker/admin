package com.arbitragebroker.admin.controller;

import com.arbitragebroker.admin.enums.RoleType;
import com.arbitragebroker.admin.model.UserModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;


    @WithMockUser(roles="ADMIN")
    @Test
    @Order(1)
    @Commit
    void register_shouldRegisterUserModelToDatabase() throws Exception {
        UserModel model = new UserModel() {{
            setUserName("bm_vip");
            setPassword("System.Admin");
            setFirstName("System");
            setLastName("Admin");
        }};
        String json = objectMapper.writeValueAsString(model);

        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("92d18767-6336-474d-9b57-9cec381db56b"))
                .andExpect(jsonPath("$.roles.[0].role").value(RoleType.USER))
        ;
    }

    @WithMockUser(roles="ADMIN")
    @Test
    @Order(2)
    void deleteById_shouldDeleteByIdFromDatabase() throws Exception {
        mockMvc.perform(delete("/api/v1/user/deleteById/{id}","92d18767-6336-474d-9b57-9cec381db56b"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(roles="ADMIN")
    @Test
    void save_shouldThrowBadRequestError() throws Exception {
        mockMvc.perform(post("/api/v1/user/save").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
        ;
    }

    @WithMockUser(roles="ADMIN")
    @Test
    void findById_shouldReturnUserModel() throws Exception {
        mockMvc.perform(get("/api/v1/user/findById/{id}", "92d18767-6336-474d-9b57-9cec381db56b"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("bm_vip"));
    }

    @WithMockUser(roles="ADMIN")
    @Test
    void findById_shouldThrowNotFoundError() throws Exception {
        mockMvc.perform(get("/api/v1/user/findById/{id}", "92d18767-6336-474d-9b57-9cec381db57b"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(roles="ADMIN")
    @Test
    void findAll_shouldReturnPageableUserModels() throws Exception {
        String filter = objectMapper.writeValueAsString(new UserModel(){{setFirstName("B");}});

        mockMvc.perform(get("/api/v1/user/findAll").param("model",filter))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect((jsonPath("$.content", Matchers.hasSize(1))));
    }

    @WithMockUser(roles="ADMIN")
    @Test
    void findAllSelect_shouldReturnPageableSelect2Models() throws Exception {
        String filter = objectMapper.writeValueAsString(new UserModel(){{setUserName("bm_vip");}});

        mockMvc.perform(get("/api/v1/user/findAllSelect")
                        .param("model",filter)
                        .param("page","0")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect((jsonPath("$.content", Matchers.hasSize(1))))
                .andExpect(jsonPath("$.content.[0].text").value("System Admin"))
        ;
    }

    @WithMockUser(roles="ADMIN")
    @Test
    void countAll_shouldReturnTotalNumberOfUsers() throws Exception {
        String filter = objectMapper.writeValueAsString(new UserModel(){{setFirstName("B");}});

        mockMvc.perform(get("/api/v1/user/countAll").param("model",filter))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }
}
