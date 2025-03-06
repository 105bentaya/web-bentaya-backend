package org.scouts105bentaya.features.invoice;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.scouts105bentaya.features.user.role.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

//    @MockBean
//    private EventService eventService;
//
//    @MockBean
//    private AuthLogic authLogic;

    @MockBean
    private InvoiceService invoiceService;

    @ParameterizedTest
    @EnumSource(value = RoleEnum.class, names = {"ROLE_SCOUTER", "ROLE_GROUP_SCOUTER"})
    void authorizedUsersCanGetInvoices(RoleEnum roles) throws Exception {
        buildResultActions("/api/invoice", roles)
            .andExpect(status().isOk());
    }

    @ParameterizedTest
    @EnumSource(value = RoleEnum.class, mode = EnumSource.Mode.EXCLUDE, names = {"ROLE_SCOUTER", "ROLE_GROUP_SCOUTER"})
    void unauthorizedUsersCannotGetInvoices(RoleEnum roles) throws Exception {
        buildResultActions("/api/invoice", roles)
            .andExpect(status().isForbidden());
    }

    private ResultActions buildResultActions(String url, RoleEnum role) throws Exception {
        Mockito.when(invoiceService.findAll(ArgumentMatchers.any())).thenReturn(Page.empty());
        return role == null ?
            mockMvc.perform(MockMvcRequestBuilders.get(url)) :
            mockMvc.perform(MockMvcRequestBuilders.get(url).with(
                user("dummy").authorities(new SimpleGrantedAuthority(role.name()))
            ));
    }
}