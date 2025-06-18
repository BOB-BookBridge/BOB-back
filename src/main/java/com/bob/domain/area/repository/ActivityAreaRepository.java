package com.bob.domain.area.repository;

import com.bob.domain.area.entity.activity.ActivityArea;
import com.bob.domain.area.entity.activity.ActivityAreaId;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface ActivityAreaRepository extends CrudRepository<ActivityArea, ActivityAreaId> {

  Optional<ActivityArea> findByIdMemberId(UUID memberId);
}
