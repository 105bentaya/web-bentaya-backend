package org.scouts105bentaya.features.invoice.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.shared.Group;

import java.io.IOException;

@Entity
@Getter
@Setter
public class InvoicePayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String payer;
    @Enumerated(EnumType.ORDINAL)
    @JsonSerialize(using = GroupSerializer.class)
    private Group groupId;

    private static class GroupSerializer extends JsonSerializer<Group> {
        @Override
        public void serialize(Group group, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeNumber(group.getValue());
        }
    }
}
