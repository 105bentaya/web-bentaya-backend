package org.scouts105bentaya.features.event;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.scouts105bentaya.core.security.service.AuthLogic;
import org.scouts105bentaya.features.event.service.EventService;
import org.scouts105bentaya.features.user.role.RoleEnum;
import org.scouts105bentaya.utils.GroupUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private AuthLogic authLogic;

    @ParameterizedTest
    @EnumSource(value = RoleEnum.class, names = {"ROLE_SCOUTER", "ROLE_USER"})
    void authorizedUsersCanGetEvents(RoleEnum roles) throws Exception {
        buildResultActions("/api/event", roles)
            .andExpect(status().isOk());
    }

    @ParameterizedTest
    @EnumSource(value = RoleEnum.class, mode = EnumSource.Mode.EXCLUDE, names = {"ROLE_SCOUTER", "ROLE_USER"})
    void unauthorizedUsersCannotGetEvents(RoleEnum roles) throws Exception {
        buildResultActions("/api/event", roles)
            .andExpect(status().isForbidden());
    }

    private ResultActions buildResultActions(String url, RoleEnum role) throws Exception {
        return role == null ?
            mockMvc.perform(MockMvcRequestBuilders.get(url)) :
            mockMvc.perform(MockMvcRequestBuilders.get(url).with(
                user("dummy").authorities(new SimpleGrantedAuthority(role.name()))
            ));
    }

    @ParameterizedTest
    @EnumSource(value = RoleEnum.class, names = {"ROLE_SCOUTER"})
    void findAllAsScouterShouldReturnAll(RoleEnum role) throws Exception {
        Mockito.when(eventService.findAll()).thenReturn(buildEvents());

        this.buildResultActions("/api/event", role)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(4)));

        Mockito.verify(eventService, Mockito.times(1)).findAll();
    }

    @ParameterizedTest
    @EnumSource(value = RoleEnum.class, names = {"ROLE_USER"})
    void findAllAsScouterShouldReturnNoScouterEvents(RoleEnum role) throws Exception {
        Mockito.when(eventService.findAll()).thenReturn(buildEvents());

        this.buildResultActions("/api/event", role)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));

        Mockito.verify(eventService, Mockito.times(1)).findAll();
    }

    @ParameterizedTest
    @EnumSource(value = RoleEnum.class, mode = EnumSource.Mode.EXCLUDE, names = {"ROLE_SCOUTER", "ROLE_USER"})
    void findAllAsUnauthorizedUserShouldReturnForbidden(RoleEnum role) throws Exception {
        this.buildResultActions("/api/event", role).andExpect(status().isForbidden());
        Mockito.verifyNoInteractions(authLogic, eventService);
    }

    private List<Event> buildEvents() {
        var group1 = GroupUtils.basicGroup();
        var group2 = GroupUtils.groupOfId(2);
        Event event = new Event();
        event.setGroup(group1);
        Event event2 = new Event();
        event2.setGroup(group2);
        Event event3 = new Event();
        event3.setGroup(group1).setForScouters(true);
        Event event4 = new Event();
        event4.setGroup(group2);
        return Arrays.asList(event, event2, event3, event4);
    }
}
