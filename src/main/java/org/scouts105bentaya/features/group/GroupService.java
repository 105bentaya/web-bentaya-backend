package org.scouts105bentaya.features.group;

import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public List<Group> findAll() {
        return groupRepository.findAllByOrderByOrder();
    }

    public Group findById(int id) {
        return groupRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }
}
