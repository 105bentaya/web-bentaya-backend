package org.scouts105bentaya.features.invoice.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.scouts105bentaya.features.group.Group;
import org.scouts105bentaya.features.group.GroupService;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GroupDeserializer extends JsonDeserializer<Group> {

    private final GroupService groupService;

    public GroupDeserializer(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    public Group deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        int groupId = jsonParser.getValueAsInt();
        return groupId == 0 ? null : groupService.findById(groupId);
    }
}
